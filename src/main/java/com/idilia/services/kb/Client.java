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

package com.idilia.services.kb;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.IdiliaCredentials;
import com.idilia.services.base.SyncClientBase;


/**
 * Client for accessing the servers that return annotated document.
 * 
 * This is a synchronous client that blocks until the server returns a
 * response. Only one instance is needed and is obtained through a singleton
 * interface.
 * 
 * Internally the class uses an HttpClient that supports multiple simultaneous operations.
 * The number of requests is controlled using member "maxConnectionsPerRoute".
 */
public class Client extends SyncClientBase {

  /**
   * Constructs a client for requesting text services provided at the default service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * <p>
   * @param creds
   * @throws MalformedURLException
   */
  public Client(IdiliaCredentials creds) throws MalformedURLException {
    this(creds, new URL("http://api.idilia.com/"));
  }
  
  
  /**
   * Constructs a client for requesting text services provided at the given service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * <p>
   * @param creds
   */
  public Client(IdiliaCredentials creds, URL url) {
    super(creds, url);
  }
  

  /**
   * Sends a request to the kb server.
   * 
   * @param req Request message. One concrete implementation of {@link QueryRequest}
   * @return {@link QueryResponse}
   * @throws IdiliaClientException when the request is not successful for any reason
   */
  public QueryResponse query(QueryRequest req) throws IdiliaClientException {
    return query(req, Object.class);
  }
  
  
  /**
   * Sends a kb/query request to the kb server.
   * 
   * @param req   Request message. One concrete implementation of {@link QueryRequest}
   * @param tpRef Type of the object into which to deserialize the result of each query.
   * @return {@link QueryResponse}
   * @throws IdiliaClientException when the request is not successful for any reason
   */
  public <T> QueryResponse query(QueryRequest req, Class<T> tpRef) throws IdiliaClientException {
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {
    
      // Recover the response.
      QueryResponse resp = KbQueryCodec.decode(jsonMapper_, tpRef, httpResponse.getEntity());
      if (resp.getStatus() != HttpURLConnection.HTTP_OK)
        throw new IdiliaClientException(resp);
      return resp;
  	} catch (IOException e) {
  	  throw new IdiliaClientException(e);
  	}
  }
  

  /**
   * Sends a request to obtain a sense menu
   */
  public SenseMenuResponse senseMenu(SenseMenuRequest req) throws IdiliaClientException {
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {
    
      // Recover the response.
      HttpEntity rxEntity = httpResponse.getEntity();
      if (rxEntity == null)
        throw new IdiliaClientException("Did not received a response from the server");
      
      String ct = rxEntity.getContentType().getValue();
      if (!ct.startsWith("application/json"))
        throw new IdiliaClientException("Unexpected content type");
  
      SenseMenuResponse resp = jsonMapper_.readValue(rxEntity.getContent(), SenseMenuResponse.class);
      if (resp.getStatus() != HttpURLConnection.HTTP_OK)
        throw new IdiliaClientException(resp);
      return resp;
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  
  /**
   * Sends a request to obtain a tagging menu
   */
  public TaggingMenuResponse taggingMenu(TaggingMenuRequest req) throws IdiliaClientException {
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {
    
      // Recover the response.
      HttpEntity rxEntity = httpResponse.getEntity();
      if (rxEntity == null)
        throw new IdiliaClientException("Did not received a response from the server");
      
      String ct = rxEntity.getContentType().getValue();
      if (!ct.startsWith("application/json"))
        throw new IdiliaClientException("Unexpected content type");
  
      TaggingMenuResponse resp = jsonMapper_.readValue(rxEntity.getContent(), TaggingMenuResponse.class);
      if (resp.getStatus() != HttpURLConnection.HTTP_OK)
        throw new IdiliaClientException(resp);
      return resp;
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  
  /**
   * Retrieve a sense card an as HTML string.
   * @param req
   * @return A string containing the card's HTML
   * @throws IdiliaClientException when the request is not successful for any reason
   */
  public SenseCardResponse senseCard(SenseCardRequest req) throws IdiliaClientException {
    
    try (CloseableHttpResponse httpResponse = getServerResponse(req)) {
      
      // Recover the response.
      HttpEntity rxEntity = httpResponse.getEntity();
      if (rxEntity == null)
        throw new IdiliaClientException("Did not received a response from the server");
      
      String ct = rxEntity.getContentType().getValue();
      if (!ct.startsWith("application/json"))
        throw new IdiliaClientException("Unexpected content type");
  
      SenseCardResponse resp = jsonMapper_.readValue(rxEntity.getContent(), SenseCardResponse.class);
      if (resp.getStatus() != HttpURLConnection.HTTP_OK)
        throw new IdiliaClientException(resp);
      
      return resp;
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
  }
    
}
