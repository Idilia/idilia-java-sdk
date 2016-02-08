/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements the base functionality for clients communicating
 *     with Idilia's server.
 *     
 *     This is a singleton object that can use up to "maxConnectionsPerRoute"
 *     for each destination Url. This parameter can be set prior to calling
 *     getInstance().
 */
package com.idilia.services.base;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class SyncClientBase extends ClientBase implements Closeable {

  /**
   * Constructor. Instantiates the internal synchronous HTTP client
   * 
   * @param creds
   *          : IdiliaCredentials object to use for the request.
   * @param serviceUrl
   *          : URL where the requests can be sent.
   */
  public SyncClientBase(IdiliaCredentials creds, URL serviceUrl) {
    super(creds, serviceUrl);
  }
  

  /**
   * Used internally to initialize the internal HTTP client used by all
   * instances of a client.
   * <p>
   * This method can be overriden to provide a client with different options.
   * The client built gets an extra interceptor to add the credentials headers.
   * @return a builder for the HTTP clients instantiated.
   */
  protected static HttpClientBuilder defaultClientBuilder() {
    return HttpClients
        .custom()
        .setRetryHandler(retryHandler)
        .setMaxConnPerRoute(maxConnectionsPerRoute)
        .setMaxConnTotal(maxConnections)
        .setDefaultRequestConfig(
            RequestConfig.custom()
            .setSocketTimeout(3600 * 1000) // 1 hour
                .build())
        .setKeepAliveStrategy(keepAliveStrategy);
  }

  /**
   * Return the internal HTTP client used for API requests.
   * 
   * <p>
   * This method can be overridden when it is not appropriate to used a shared
   * internal HTTP client for all clients.
   * 
   * @return the configured HTTP client
   */
  protected CloseableHttpClient getClient() {
    return httpClient_;
  }

  
  /** Helper function to return the response
   * @param req the request to issue
   * @return a response to an API request
   * @throws IdiliaClientException on any error encountered 
   */
  protected CloseableHttpResponse getServerResponse(RequestBase req) throws IdiliaClientException {
    HttpPost httpPost = createPost(req);
    HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
      CloseableHttpResponse resp = getClient().execute(httpPost, ctxt);
      
      if (resp.getEntity() == null)
        throw new IdiliaClientException("Unexpected null response from server");
      
      return resp;

    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
  }

  @Override
  public void close() {
    /* We're not really closable because we use a static CloseableHttpClient. */
  }

  /** The internal HTTP client. */
  final private static CloseableHttpClient httpClient_ = 
      defaultClientBuilder()
        .addInterceptorFirst(new RequestSigner())
        .build();
  
  final protected static HttpRequestRetryHandler retryHandler = new RetryHandler();
}
