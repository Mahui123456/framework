package com.krb.guaranty.common.config;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * 基于httpclient连接池的restTemplate配置
 *
 * @author ningbo.zhao
 * @since 2018年08月27日
 */
@Configuration
public class RestTemplateGuarantyConfig {

    /**
     * 打印RestTempalte调用前后日志
     */
    public static class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        private static final Logger log = LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            long startTime = System.currentTimeMillis();
            traceRequest(request, body);
            ClientHttpResponse response = execution.execute(request, body);
            long endTime = System.currentTimeMillis();;
            traceResponse(response,endTime-startTime);
            return response;
        }

        private void traceRequest(HttpRequest request, byte[] body) throws IOException {
            log.info("before RestTemplate URI:{},Method:{},Headers:{},Request body:{}",request.getURI(),request.getMethod(),request.getHeaders(),new String(body,0,Math.min(body.length,2000), "UTF-8"));
        }

        private void traceResponse(ClientHttpResponse response, long time) throws IOException {
            log.error("after RestTemplate error Status cost:{}ms Code:{},Status text:{},Headers:{}",time,response.getRawStatusCode(),response.getStatusText(),response.getHeaders());
        }

    }

    /*public static class CurrentUserAuthorityClientHttpRequestInterceptor implements ClientHttpRequestInterceptor{

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

            UserAuthority currentUserAuthority = UserAuthorityUtil.getCurrentUserAuthority();
            if(currentUserAuthority != null && currentUserAuthority.getUserId() != null){
                //rest调用时 为 当前用户添加header
                request.getHeaders().add(UserAuthorityTokenConst.USER_AUTHORITY, IFCJSONUtil.toJSONString(currentUserAuthority));
            }
            return execution.execute(request, body);
        }
    }*/

    @ConditionalOnMissingBean
    @Bean
    @Primary
    public RestTemplate restTemplate () throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());
        return restTemplate;
    }

    @ConditionalOnMissingBean
    @Bean
    public CloseableHttpClient httpClient() {
        return httpClientBuilder().build();
    }

    @ConditionalOnMissingBean
    @Bean
    public HttpClientBuilder httpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager());
        httpClientBuilder.setRetryHandler(httpRequestRetryHandler());
        httpClientBuilder.setDefaultRequestConfig(requestConfig());
        return httpClientBuilder;
    }

    @ConditionalOnMissingBean
    @Bean
    public HttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 设置整个连接池最大连接数
        poolingHttpClientConnectionManager.setMaxTotal(200);
        // 路由是对maxTotal的细分
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);
        //闲置连接超时回收
        poolingHttpClientConnectionManager.closeIdleConnections(10, TimeUnit.SECONDS);
        return poolingHttpClientConnectionManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
        return new DefaultHttpRequestRetryHandler(3, true);
    }

    @ConditionalOnMissingBean
    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                // 服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setSocketTimeout(30000)
                // 连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectTimeout(30000)
                // 从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(1000)
                .build();
    }
}
