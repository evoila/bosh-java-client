package io.bosh.client;


import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import io.bosh.client.Authentication;
import io.bosh.client.DirectorException;
import io.bosh.client.RequestLoggingInterceptor;
import io.bosh.client.SpringDirectorClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.oauth2.common.*;

import javax.net.ssl.SSLContext;

/**
 * @author David Ehringer, Jannik Heyl.
 */
public class SpringDirectorClientBuilder {

    private Scheme scheme;
    private int port;
    private String host;
    private String username;
    private String password;
    private Authentication auth;
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(SpringDirectorClientBuilder.class);

    public SpringDirectorClientBuilder withCredentials(String username, String password, Authentication auth){
        this.username = username;
        this.password = password;
        this.auth = auth;
        return this;
    }

    public SpringDirectorClientBuilder withScheme(Scheme scheme){
        this.scheme = scheme;
        return this;
    }

    public SpringDirectorClientBuilder withPort(int port){
        this.port = port;
        return this;
    }

    public SpringDirectorClientBuilder withHost(String host){
        this.host = host;
        return this;
    }

    public SpringDirectorClient build(){
        // TODO validate
        URI root = UriComponentsBuilder.newInstance().scheme(scheme.name()).host(host).port(port)
                .build().toUri();
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(createRequestFactory(host, username, password, auth)));
        restTemplate.getInterceptors().add(new ContentTypeClientHttpRequestInterceptor());
        restTemplate.getInterceptors().add(new RequestLoggingInterceptor());
        handleTextHtmlResponses(restTemplate);
        return new SpringDirectorClient(root, restTemplate);
    }

    private ClientHttpRequestFactory createRequestFactory(String host, String username,
                                                          String password, Authentication auth) {

        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new DirectorException("Unable to configure ClientHttpRequestFactory", e);
        }

        SSLConnectionSocketFactory connectionFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext).setTlsVersions(TLS.V_1_3, TLS.V_1_2, TLS.V_1_1, TLS.V_1_0).setHostnameVerifier(new NoopHostnameVerifier())
                .build();

        HttpClient httpClient;

        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(connectionFactory).build();
        if(auth.equals(Authentication.BASIC)){
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(host, 25555),
                    new UsernamePasswordCredentials(username, password.toCharArray()));

            // disabling redirect handling is critical for the way BOSH uses 302's
            httpClient = HttpClientBuilder.create().disableRedirectHandling()
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setConnectionManager(connectionManager).build();
        } else {

            // disabling redirect handling is critical for the way BOSH uses 302's
            httpClient = HttpClientBuilder.create().disableRedirectHandling()
                    .setDefaultHeaders(Arrays.asList(new OAuthCredentialsProvider(host, username, password)))
                    .setConnectionManager(connectionManager).build();

        }


        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private void handleTextHtmlResponses(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter());
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json"), new MediaType("application", "*+json"),
                new MediaType("text", "html")));
        messageConverters.add(messageConverter);
        restTemplate.setMessageConverters(messageConverters);
    }

    private static class ContentTypeClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            // some BOSH resources return text/plain and this modifies this response
            // so we can use Jackson
            try {
                HttpHeaders.writableHttpHeaders(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
            }
            catch (Exception ex) {
                log.warn(ex.getMessage());
            }
            return response;
        }

    }
}