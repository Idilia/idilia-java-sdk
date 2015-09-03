/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements the base functionality for clients communicating
 *     asynchronously with Idilia's server.
 *     
 */
package com.idilia.services.base;

import java.io.Closeable;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;

public class AsyncClientBase extends ClientBase implements Closeable {

  protected AsyncClientBase(IdiliaCredentials creds, URL serviceUrl) {
    super(creds, serviceUrl);
  }

  /**
   * Used internally to initialize the internal HTTP client used by all
   * instances of a client.
   * <p>
   * This method can be overriden to provide a client with different options.
   * The client built gets an extra interceptor to add the credentials headers.
   */
  protected static HttpAsyncClientBuilder defaultClientBuilder() {
    return HttpAsyncClients
        .custom()
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
  protected CloseableHttpAsyncClient getClient() {
    /* 
     * This default implementation returns the static client shared between all
     * instances.
     */
    return httpClient_;
  }

  /**
   * Base class for our implementation of the HTTP async callback.
   * Individual services override to provide method {@link #completedHdlr} to decode a response.
   * 
   */
  protected abstract class HttpCallback<Response> implements FutureCallback<HttpResponse> {
    final CompletableFuture<Response> future_;

    public HttpCallback(CompletableFuture<Response> future) {
      future_ = future;
    }

    /**
     * Decode the entity in the HTTP response and store in member #resp.
     * If the response is not successfull, the implementation must throw to interrupt
     * asynchronous completion stages.
     * @throws IdiliaClientException when the response is not a success and processing of an asynchronous 
     *   chain that relies on the response has to be interrupted.
     * @throws Exception Handler is allowed to throw any exception it wants and this class will wrap it
     *   into an IdiliaClientException
     */
    abstract public Response completedHdlr(HttpResponse result) throws IdiliaClientException, Exception;

    @Override
    public void completed(HttpResponse result) {
      try {
        future_.complete(completedHdlr(result));
      } catch (IdiliaClientException ice) {
        future_.completeExceptionally(ice);
      } catch (Exception e) {
        future_.completeExceptionally(new IdiliaClientException(e));
      }
    }

    @Override
    public void failed(Exception e) {
      future_.completeExceptionally(new IdiliaClientException(e));
    }

    @Override
    public void cancelled() {
      future_.cancel(false);
    }
  }
  
  @Override
  public void close() {
    /* We're not really closable because we use a static CloseableHttpAsyncClient. */
  }
  
  /**
   * The HTTP internal asynchronous client.
   */
  final private static CloseableHttpAsyncClient httpClient_;
  static {
    httpClient_ = defaultClientBuilder()
        .addInterceptorFirst(new RequestSigner())
        .build();
    httpClient_.start();
  }

}
