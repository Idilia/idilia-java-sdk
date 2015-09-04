/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements a client for communicating with Idilia's
 *     document server for sense analysis.
 *     
 *     Both the queries and responses use multipart messaging.
 *     
 *     This is a singleton object that can use up to "maxConnectionsPerRoute"
 *     for each destination Url. This parameter can be set prior to calling
 *     getInstance().
 */

package com.idilia.services.text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.idilia.services.base.AsyncClientBase;
import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.IdiliaCredentials;


/**
 * ClientBase for accessing the servers that return annotated document.
 * 
 * This is an asynchronous client that returns futures when issuing a request.
 * 
 * Internally the class uses an HttpAsyncClient that supports multiple simultaneous operations.
 * The number of requests is controlled using member "maxConnectionsPerRoute".
 */
public class AsyncClient extends AsyncClientBase
{
 
  /**
   * Constructs a client for requesting text services provided at the default service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * Multithread safe.
   * <p>
   * @param creds Idilia API credentials
   */
  public AsyncClient(IdiliaCredentials creds) {
    this(creds, defaultApiUrl);
  }
  
  /**
   * Constructs a client for requesting text services provided at the given service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * Multithread safe.
   * <p>
   * @param creds Idilia API credentials
   * @param url URL to reach the Idilia API
   */
  public AsyncClient(IdiliaCredentials creds, URL url) {
    super(creds, url);
  }
    
  /**
   * Sends a disambiguation request to a text server.
   * <p>
   * Asynchronously sends an HTTP request to a text server and signals the returned
   * future when the result is available. 
   * <p>
   * @param req Request message. One concrete implementation of {@link DisambiguateRequest}
   * @return {@link DisambiguateResponse}
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public CompletableFuture<DisambiguateResponse> disambiguateAsync(final DisambiguateRequest req) throws IdiliaClientException {
    
    final HttpPost httpPost = createMultipartPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException ioe) {
      throw new IdiliaClientException(ioe);
    }
    
    final CompletableFuture<DisambiguateResponse> future = new CompletableFuture<>();
    
    getClient().execute(httpPost, ctxt, new HttpCallback<DisambiguateResponse>(future) {
      @Override
      public DisambiguateResponse completedHdlr(HttpResponse result) throws IdiliaClientException, JsonParseException, UnsupportedOperationException, IOException, MessagingException {
        DisambiguateResponse resp = DisambiguateCodec.decode(jsonMapper_, result.getEntity());
        if (resp.getStatus() != HttpURLConnection.HTTP_OK && resp.getStatus() != HttpURLConnection.HTTP_ACCEPTED)
          throw new IdiliaClientException(resp);
        return resp;
      }
    });
    return future;
  }
  
  /**
   * Sends a paraphrase request to the text server.
   * 
   * Asynchronously sends an HTTP request to a text server and signals the returned
   * future when the result is available. 
   * 
   * @param req Request message. One concrete implementation of {@link ParaphraseRequest}
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public CompletableFuture<ParaphraseResponse> paraphraseAsync(final ParaphraseRequest req) throws IdiliaClientException {
   
    final HttpPost httpPost = createPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
    
    final CompletableFuture<ParaphraseResponse> future = new CompletableFuture<>();
    getClient().execute(httpPost, ctxt, 
        new HttpCallback<ParaphraseResponse>(future) {
      @Override
      public ParaphraseResponse completedHdlr(HttpResponse result) throws IdiliaClientException, JsonParseException, JsonProcessingException, IOException, MessagingException {
        ParaphraseResponse resp = ParaphraseCodec.decode(jsonMapper_, result.getEntity());
        if (resp.getStatus() != HttpURLConnection.HTTP_OK && resp.getStatus() != HttpURLConnection.HTTP_ACCEPTED)
          throw new IdiliaClientException(resp);
        return resp;
      }
    });
    return future;
  }

  
  /**
   * Sends a semantic match request to the text server.
   * 
   * Asynchronously sends an HTTP request to a text server and signals the returned
   * future when the result is available. 
   * 
   * @param req Request message. One concrete implementation of {@link MatchRequest}
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public CompletableFuture<MatchResponse> matchAsync(final MatchRequest req) throws IdiliaClientException {
   
    final HttpPost httppost = createPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
    
    final CompletableFuture<MatchResponse> future = new CompletableFuture<>();
    getClient().execute(httppost, ctxt, new HttpCallback<MatchResponse>(future) {
      @Override
      public MatchResponse completedHdlr(HttpResponse result) throws IdiliaClientException, JsonParseException, UnsupportedOperationException, IOException {
        MatchResponse resp = MatchCodec.decodeMatchResponse(jsonMapper_, result.getEntity());
        if (resp.getStatus() != HttpURLConnection.HTTP_OK && resp.getStatus() != HttpURLConnection.HTTP_ACCEPTED)
          throw new IdiliaClientException(resp);
        return resp;
      }
    });
    return future;
  }
  

  /**
   * Sends a matching eval request to the text server.
   * 
   * Asynchronously sends an HTTP request to a text server and signals the returned
   * future when the result is available. 
   * 
   * @param req Request message. One concrete implementation of {@link MatchingEvalRequest}
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public CompletableFuture<MatchingEvalResponse> matchingEvalAsync(final MatchingEvalRequest req) throws IdiliaClientException {
    // Sign the request and transmit it
    final HttpPost httppost = createPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
    
    final CompletableFuture<MatchingEvalResponse> future = new CompletableFuture<>();
    getClient().execute(httppost, ctxt, 
        new HttpCallback<MatchingEvalResponse>(future) {
      @Override
      public MatchingEvalResponse completedHdlr(HttpResponse result) throws IdiliaClientException, JsonParseException, UnsupportedOperationException, IOException {
        MatchingEvalResponse resp = MatchingEvalCodec.decode(jsonMapper_, result.getEntity());
        if (resp.getStatus() != HttpURLConnection.HTTP_OK)
          throw new IdiliaClientException(resp);
        return resp;
      }
    });
    return future;
  }
    
}
