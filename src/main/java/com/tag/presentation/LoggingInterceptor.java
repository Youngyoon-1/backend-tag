package com.tag.presentation;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws  IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        System.out.println("===========================request begin================================================");
        System.out.println("URI         : " + request.getURI());
        System.out.println("Method      : " + request.getMethod());
        System.out.println("Headers     : " + request.getHeaders());
        System.out.println("Request body: " + new String(body, "UTF-8"));
        System.out.println("==========================request end================================================");
    }
}