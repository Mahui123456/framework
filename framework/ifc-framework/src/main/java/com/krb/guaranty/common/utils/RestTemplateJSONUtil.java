package com.krb.guaranty.common.utils;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * @author owell
 * @date 2020/9/10 10:48
 */
@Data
@Component
public class RestTemplateJSONUtil {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 用resttemplate发送post 请求的时候 对requestBody(application/json) tojson 时返回的json对象
     * @param requestBody resttemplate post 请求体
     * @return
     */
    @SneakyThrows
    public String getRequestBodyJSON(Object requestBody){
        RestTemplate2JsonBodyHttpOutputMessage httpRequest = new RestTemplate2JsonBodyHttpOutputMessage(new ByteArrayOutputStream(), new HttpHeaders());

        Class<?> requestBodyClass = requestBody.getClass();
        Type requestBodyType = requestBodyClass;
        MediaType requestContentType = MediaType.APPLICATION_JSON;
        for (HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
            if (messageConverter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter<Object> genericConverter =
                        (GenericHttpMessageConverter<Object>) messageConverter;
                if (genericConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
                    genericConverter.write(requestBody, requestBodyType, requestContentType, httpRequest);
                    break;
                }
            }
            else if (messageConverter.canWrite(requestBodyClass, requestContentType)) {
                ((HttpMessageConverter<Object>) messageConverter).write(
                        requestBody, requestContentType, httpRequest);
                break;
            }
        }

        return httpRequest.getBody().toString();
    }

    public static class RestTemplate2JsonBodyHttpOutputMessage implements HttpOutputMessage {

        private OutputStream body;

        private HttpHeaders headers;

        public RestTemplate2JsonBodyHttpOutputMessage() {
        }

        public RestTemplate2JsonBodyHttpOutputMessage(OutputStream body, HttpHeaders headers) {
            this.body = body;
            this.headers = headers;
        }


        @Override
        public OutputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }

}
