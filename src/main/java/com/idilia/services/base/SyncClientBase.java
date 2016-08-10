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
import org.apache.http.client.methods.HttpUriRequest;
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
        .addInterceptorLast(new GzipInterceptors.GzipRequestInterceptor())
        .addInterceptorFirst(new GzipInterceptors.GzipResponseInterceptor())
        .setRetryHandler(retryHandler)
        .setMaxConnPerRoute(maxConnections)
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

  
  /** Helper function to return the response from an API request
   * @param req the request to issue
   * @return a response to an API request
   * @throws IdiliaClientException on any error encountered 
   */
  protected CloseableHttpResponse getServerResponse(RequestBase req) throws IdiliaClientException {
    HttpPost httpPost = createPost(req);
    HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
    return getServerResponse(httpPost, ctxt);
  }
  
  /**
   * Helper function to return a response from an Http request
   * @param request http request to transmit
   * @param ctxt http context for request
   * @return received http response
   * @throws IdiliaClientException on any error encountered
   */
  protected CloseableHttpResponse getServerResponse(HttpUriRequest request, HttpClientContext ctxt) throws IdiliaClientException {
    try {
      for (int retryCnt = 0; ; ) {
        CloseableHttpResponse resp = getClient().execute(request, ctxt);
        
        if ((resp.getStatusLine().getStatusCode() >= 500) &&
            retryHandler.retryRequest(null, ++retryCnt, ctxt))
          continue;
        
        if (resp.getEntity() == null)
          throw new IdiliaClientException("Unexpected null response from server");
        
        return resp;
      }

    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  /**
   * Function to be called by users to perform an API request.
   * 
   * @param req request object which will be sent to the server
   * @return ResponseBase to be cast in the response the request expects
   * @throws IdiliaClientException on any error encountered
   */
  public ResponseBase perform(RequestBase req) throws IdiliaClientException {
    
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {
      return decodeHttpResponse(httpResponse, req);
    }
    catch (IOException e) {
      throw new IdiliaClientException(e);
    } 
  }


  @Override
  public void close() {
    /* We're not really closable because we use a static CloseableHttpClient. */
  }

  
  /** A retry handler that pauses when overflowing with requests */
  final protected static HttpRequestRetryHandler retryHandler = new SyncRetryHandler();
  
  /** The internal HTTP client. */
  final private static CloseableHttpClient httpClient_ = 
      defaultClientBuilder()
        .addInterceptorFirst(new RequestSigner())
        .build();
}
