/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements the base class for issuing a request to Idilia's
 *     KnowledgeBase server for retrieving semantic connections and properties.
 */

package com.idilia.services.kb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.RequestBase;

/**
 * Class for encoding the query request.
 */
public class QueryRequest extends RequestBase {


  public QueryRequest() {
  }
  
  public QueryRequest(String query) {
    setQuery(query);
  }
  
  public QueryRequest(Iterable<? extends Object> qrys) throws JsonProcessingException {
    setQuery(qrys);
  }
  
  /**
   * Specifies the query to transmit.
   * 
   * The string given represents a JSON array of JSON objects
   * where each object is a unique query.
   * 
   * @param query Array of queries to be processed. 
   */
  public void setQuery(String query) {
    this.query = query.getBytes();
  }
  
  
  public void setQuery(Iterable<? extends Object> qrys) throws JsonProcessingException {
    query = new ObjectMapper().writeValueAsBytes(qrys);
  }
  

  // Return the name of the REST path used when accessing the server
  /**
   * Returns the request path for the REST method on the server.
   * @return request path.
   */
  @Override
  final public String requestPath() {
    return new String("/1/kb/query.json");
  }
  
  
  //
  // Protected and abstract methods for the subclasses to implement
  
  // Encode the content as HTTP query parameters
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IllegalStateException {
    
    if (this.query == null)
      throw new IllegalStateException("No query specified");
    
    // Add base parameters
    super.getHttpQueryParms(parms);
    
    // Add parameters from this class
    parms.add(new BasicNameValuePair("query", new String(query, StandardCharsets.UTF_8)));
  }
  
  // Return the content to sign when creating the authentication information
  final public byte[] toSign() throws IOException {
    return query;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != this.getClass())
      return false;
    QueryRequest other = (QueryRequest) obj;
    return Arrays.equals(query, other.query);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(query);
  }
  
  @Override
  public String toString() {
    return new String(query);
  }
  
  private byte[] query;
}
