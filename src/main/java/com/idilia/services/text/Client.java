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
import java.net.URL;

import javax.mail.MessagingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;

import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.IdiliaCredentials;
import com.idilia.services.base.SyncClientBase;


/**
 * ClientBase for accessing the servers that return annotated document.
 * 
 * This is a synchronous client that blocks until the server returns a
 * response.
 * 
 * Internally the class uses an HttpClient that supports multiple simultaneous operations.
 * The number of requests is controlled using member "maxConnectionsPerRoute".
 */
public class Client extends SyncClientBase
{
 
  /**
   * Constructs a client for requesting text services provided at the default service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * Multithread safe.
   * 
   * @param creds Idilia API credentials for a project
   */
  public Client(IdiliaCredentials creds) {
    this(creds, defaultApiUrl);
  }
  
  
  /**
   * Constructs a client for requesting text services provided at the given service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * Multithread safe.
   * 
   * @param creds Idilia API credentials for a project
   * @param url   URL to reach the API. Normally http://api.idilia.com
   */
  public Client(IdiliaCredentials creds, URL url) {
    super(creds, url);
  }
  
  
  /**
   * Sends a disambiguation request to a text server.
   * 
   * Sends an HTTP request to a text server, waits for the response, and
   * constructs a DisambiguateResponse with the server's response. This method
   * contacts the default server and is the one normally used.
   * 
   * @param req Request message. One concrete implementation of {@link DisambiguateRequest}
   * @return {@link DisambiguateResponse}
   * @throws IdiliaClientException when the request is not successful for any reason
   */
  public DisambiguateResponse disambiguate(DisambiguateRequest req) throws IdiliaClientException {
    
    /* Create the request */
    final HttpPost httpPost = createMultipartPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException ioe) {
      throw new IdiliaClientException(ioe);
    }
    
    /* Get the response and decode it */
    try (CloseableHttpResponse httpResponse = getServerResponse(httpPost, ctxt)) {
      // Recover the response. It can be a single part or multipart
      HttpEntity rxEntity = httpResponse.getEntity();
      DisambiguateResponse resp = DisambiguateCodec.decode(jsonMapper_, rxEntity);
      if (resp.getStatus() != HttpStatus.SC_OK && resp.getStatus() != HttpStatus.SC_ACCEPTED)
        throw new IdiliaClientException(resp);
      return resp;
    } catch (IOException | UnsupportedOperationException | MessagingException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  
  /**
   * Sends a paraphrase request to the text server.
   * 
   * Sends an HTTP request to the text server, waits for the response, and
   * constructs a ParaphraseResponse with the server's response. This method
   * contacts the default server and is the one normally used.
   * 
   * @param req Request message. One concrete implementation of {@link ParaphraseRequest}
   * @return {@link ParaphraseResponse}
   * @throws IdiliaClientException when the request is not successful for any reason
   */
  public ParaphraseResponse paraphrase(ParaphraseRequest req) throws IdiliaClientException {
    
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {

      // Recover the response. It can be a single part or multipart
      HttpEntity rxEntity = httpResponse.getEntity();
      ParaphraseResponse resp = ParaphraseCodec.decode(jsonMapper_, rxEntity);
      if (resp.getStatus() != HttpStatus.SC_OK && resp.getStatus() != HttpStatus.SC_ACCEPTED)
        throw new IdiliaClientException(resp);
      return resp;
    } catch (IOException | UnsupportedOperationException | MessagingException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  
  /**
   * Sends a semantic match request to the text server.
   * 
   * Sends an HTTP request to the text server, waits for the response, and
   * constructs a MatchResponse with the server's response. This method
   * contacts the default server and is the one normally used.
   * 
   * @param req Request message. One concrete implementation of {@link MatchRequest}
   * @return {@link MatchResponse}
   * @throws IdiliaClientException when the request is not successful for any reason
   */
  public MatchResponse match(MatchRequest req) throws IdiliaClientException {
    
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {

      // Recover the response. It can be a single part or multipart
      HttpEntity rxEntity = httpResponse.getEntity();
      MatchResponse resp = MatchCodec.decodeMatchResponse(jsonMapper_, rxEntity);
      if (resp.getStatus() != HttpStatus.SC_OK && resp.getStatus() != HttpStatus.SC_ACCEPTED)
        throw new IdiliaClientException(resp);
      return resp;
    } catch (IOException | UnsupportedOperationException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  
  /**
   * Sends a matching eval request to the text server.
   * 
   * Sends an HTTP request to the text server, waits for the response, and
   * constructs a MatchingEvalResponse with the server's response. This method
   * contacts the default server and is the one normally used.
   * 
   * @param req Request message. One concrete implementation of {@link MatchRequest}
   * @return {@link MatchingEvalResponse}
   * @throws IdiliaClientException when the request is not successful for any reason
   */
  public MatchingEvalResponse matchingEval(MatchingEvalRequest req) throws IdiliaClientException {
    
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {

      // Recover the response. It can be a single part or multipart
      HttpEntity rxEntity = httpResponse.getEntity();
      MatchingEvalResponse resp = MatchingEvalCodec.decode(jsonMapper_, rxEntity);
      if (resp.getStatus() != HttpStatus.SC_OK)
        throw new IdiliaClientException(resp);
      return resp;
    } catch (IOException | UnsupportedOperationException e) {
      throw new IdiliaClientException(e);
    }
  }

 }
