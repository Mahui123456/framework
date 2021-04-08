package com.krb.guaranty.common.extention.sleuth;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.krb.guaranty.common.context.IFCInit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.cloud.sleuth.util.SpanNameUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class IFCSleuthUtil implements IFCInit {

    /**
     *
     * 参考 TraceSchedulingAspect {@link org.springframework.cloud.sleuth.instrument.scheduling.TraceSchedulingAspect}
     *
     * 重新开始一个
     * @param pjp
     * @return
     * @throws Throwable
     */
    public static Object newStart(final ProceedingJoinPoint pjp) throws Throwable {
        String spanName = SpanNameUtil.toLowerHyphen(pjp.getSignature().getName());
        Span span = startOrContinueRenamedSpan(spanName);
        try(Tracer.SpanInScope ws = IFCSleuthUtil.tracer.withSpanInScope(span.start())) {
            span.tag(CLASS_KEY, pjp.getTarget().getClass().getSimpleName());
            span.tag(METHOD_KEY, pjp.getSignature().getName());
            return pjp.proceed();
        }catch (Throwable e){
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }

    private static Span startOrContinueRenamedSpan(String spanName) {
        Span currentSpan = IFCSleuthUtil.tracer.currentSpan();
        if (currentSpan != null) {
            return currentSpan.name(spanName);
        }
        return IFCSleuthUtil.tracer.nextSpan().name(spanName);
    }

    /**
     *
     * 参考
     *
     *  TracingFilter {@link brave.servlet.TracingFilter}
     *  HttpServerHandler {@link brave.http.HttpServerHandler}
     *
     * 加入一个开始
     * @param pjp
     * @param traceBean
     * @return
     * @throws Throwable
     */
    public static Object joinStart(final ProceedingJoinPoint pjp, IFCTraceBean traceBean) throws Throwable {
        String spanName = SpanNameUtil.toLowerHyphen(pjp.getSignature().getName());
        ScopedSpan span = IFCSleuthUtil.tracer.startScopedSpanWithParent(spanName,handleReceive(traceBean).context());
        try {
            // The span is in "scope" so that downstream code such as loggers can see trace IDs
            return pjp.proceed();
        } catch (RuntimeException | Error e) {
            span.error(e); // Unless you handle exceptions, you might not know the operation failed!
            throw e;
        } finally {
            span.finish();
        }

    }

    static Span handleReceive(IFCTraceBean carrier) {
        Span span = nextSpan(IFCSleuthUtil.extractor.extract(carrier));
        return handleStart(span);
    }

    static Span nextSpan(TraceContextOrSamplingFlags extracted) {
        return extracted.context() != null
                ? IFCSleuthUtil.tracer.joinSpan(extracted.context())
                : IFCSleuthUtil.tracer.nextSpan(extracted);
    }

    static Span handleStart(Span span) {
        if (span.isNoop()) return span;
        // all of the above parsing happened before a timestamp on the span
        return span.start();
    }

    /**
     * 获取当前trace信息
     * @return
     */
    public static IFCTraceBean getCurrentTraceBean(){
        final Span span = IFCSleuthUtil.tracer.currentSpan();
        if(span == null){
            return null;
        }
        final TraceContext context = span.context();
        return new IFCTraceBean(context.traceIdString(),context.traceIdString());
    }

    private static Tracer tracer;
    private static TraceContext.Extractor<IFCTraceBean> extractor;
    private static CurrentTraceContext currentTraceContext;

    public static final String CLASS_KEY = "class";
    public static final String METHOD_KEY = "method";

    /**
     * 128 or 64-bit trace ID lower-hex encoded into 32 or 16 characters (required)
     */
    static final String TRACE_ID_NAME = "X-B3-TraceId";
    /**
     * 64-bit span ID lower-hex encoded into 16 characters (required)
     */
    static final String SPAN_ID_NAME = "X-B3-SpanId";
    /**
     * 64-bit parent span ID lower-hex encoded into 16 characters (absent on root span)
     */
    static final String PARENT_SPAN_ID_NAME = "X-B3-ParentSpanId";
    /**
     * "1" means report this span to the tracing system, "0" means do not. (absent means defer the
     * decision to the receiver of this header).
     */
    static final String SAMPLED_NAME = "X-B3-Sampled";
    /**
     * "1" implies sampled and is a request to override collection-tier sampling policy.
     */
    static final String FLAGS_NAME = "X-B3-Flags";
    @Data
    public static class IFCTraceBean{
        Map<String,String> map = new HashMap<>();

        public IFCTraceBean() {
        }

        public IFCTraceBean(String traceId,String spanId) {
            map.put(TRACE_ID_NAME,traceId);
            map.put(SPAN_ID_NAME,spanId);
        }

        public IFCTraceBean put(String key,String value){
            map.put(key,value);
            return this;
        }

        public String get(String key){
            return map.get(key);
        }
    }

    static final Propagation.Getter<IFCTraceBean, String> GETTER =
            new Propagation.Getter<IFCTraceBean, String>() {
                @Override
                public String get(IFCTraceBean carrier, String key) {
                    return carrier.get(key);
                }

                @Override
                public String toString() {
                    return "IFCTraceBean::get";
                }
            };

    @Override
    public void doInit(ApplicationContext application) throws Exception {
        Tracing tracing = application.getBean(Tracing.class);
        IFCSleuthUtil.tracer = tracing.tracer();
        IFCSleuthUtil.currentTraceContext = tracing.currentTraceContext();
        IFCSleuthUtil.extractor = tracing.propagation().extractor(GETTER);
    }
}
