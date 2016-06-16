/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements the base functionality for clients communicating
 *     asynchronously with Idilia's server.
 *     
 */
package com.idilia.services.base;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

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
   *
   * @return HTTP default async client builder
   */
  protected static HttpAsyncClientBuilder defaultClientBuilder() {
    
    try {
      DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
      connMgr = new PoolingNHttpClientConnectionManager(ioReactor);
      connMgr.setMaxTotal(maxConnections);
      connMgr.setDefaultMaxPerRoute(maxConnections);
    } catch (IOReactorException e) {
    }
    
    return HttpAsyncClients
        .custom()
        .addInterceptorLast(new GzipInterceptors.GzipRequestInterceptor())
        .setConnectionManager(connMgr)
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
    final HttpUriRequest request_;
    final HttpClientContext context_;
    final CompletableFuture<Response> future_;
    int retryCnt_ = 0;

    /** Create a callback that does not support retries */
    public HttpCallback(CompletableFuture<Response> future) {
      request_ = null;
      context_ = null;
      future_ = future;
    }

    /**
     * Create a callback with retry capability.
     * An condition for using this is that the entity in the request can be sent again.
     */
    public HttpCallback(HttpUriRequest request, HttpClientContext context, CompletableFuture<Response> future) {
      request_ = request;
      context_ = context;
      future_ = future;
    }

    /**
     * Decode the entity in the HTTP response].
     * If the response is not successful, the implementation must throw to interrupt
     * asynchronous completion stages.
     * @param result the result to decode
     * @return a decoded Response object
     * @throws IdiliaClientException when the response is not a success and processing of an asynchronous 
     *   chain that relies on the response has to be interrupted.
     * @throws Exception Handler is allowed to throw any exception it wants and this class will wrap it
     *   into an IdiliaClientException
     */
    abstract public Response completedHdlr(HttpResponse result) throws IdiliaClientException, Exception;

    
    @Override
    public void completed(HttpResponse result) {
      try {
        /* Retry on a failure when we have the retry information */
        if (context_ != null && 
            result != null &&
            (result.getStatusLine().getStatusCode() >= 500)) {
          int r = retryHandler.retryRequest(null, ++retryCnt_, context_);
          if (r >= 0) {
            /* Ensure that a minimum wait to prevent a race condition with out of order response */
            long waitMs = r == 0 ? 200 : r * 1000;
            executor.schedule(
                () -> { getClient().execute(request_, context_, this); },
                waitMs, TimeUnit.MILLISECONDS);
            return;
          }
        }
        
        gzipDecoder.process(result, null);
        if (result.getEntity() == null)
          future_.completeExceptionally(new IdiliaClientException("Unexpected null response from server"));
        else
          future_.complete(completedHdlr(result));
        
      } catch (IdiliaClientException ice) {
        future_.completeExceptionally(ice);
      } catch (Exception e) {
        future_.completeExceptionally(new IdiliaClientException(e));
      }
    }

    @Override
    public void failed(Exception e) {
      /* Retry on a failure when we have the retry information */
      if (context_ != null && (e instanceof IOException) &&
          retryHandler.retryRequest((IOException) e, ++retryCnt_, context_) == 0) {
        getClient().execute(request_, context_, this);
        return;
      }
      
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
   * Stop the internal static HTTP asynchronous client.
   * <p>
   * This should be done at program exit to terminate its thread pool.
   */
  static public void stop() {
    /* Stop the client */
    try {
      httpClient_.close();
    } catch (IOException ioe) {
    }
    
    /* Stop executor */
    try {
      executor.shutdownNow();
      executor.awaitTermination(1, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
    }
  }
  
  /** Shared connection manager for the connections established by any instances of the client */
  static protected PoolingNHttpClientConnectionManager connMgr;
  
  /** Thread pool for running a connection cleanup service and delayed retries */
  static protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  
  /**
   * The HTTP internal asynchronous client.
   */
  final private static CloseableHttpAsyncClient httpClient_;
  
  static {
    /** Initialized the shared client */
    httpClient_ = defaultClientBuilder()
        .addInterceptorFirst(new RequestSigner())
        .build();
    httpClient_.start();
    
    /** Initialize connection cleanup */
    if (connMgr != null) {
      executor.scheduleAtFixedRate(() -> {
        connMgr.closeExpiredConnections();
      }, 30, 30, TimeUnit.SECONDS);
    }
  }
  
  /** Using an inline interceptor with the client does not work. Use it on the received response */
  static protected HttpResponseInterceptor gzipDecoder = new GzipInterceptors.GzipResponseInterceptor();
  
  /** 
   * A retry handler.
   */
  static protected AsyncRetryHandler retryHandler = new AsyncRetryHandler();
}
