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
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.RequestBase;

/**
 * Class for encoding the kb/query request.
 * 
 * A request is best performed by defining a class with the expected result. This
 * class also serves as the template for the query. E.g.,
 * <pre>
 * {@code
 *   static class FskInfo {
 *     // List of public fields for JSON recovery. Can include nested objects as well.
 *     public String fsk;
 *     public ArrayList<String> children;
 *     public ArrayList<String> parents;
 *     public NeInfo neInfo;
 *     
 *     // Method to generate an object that will JSON serialize to the correct query form.
 *     static FskInfo query(String fsk) {
 *       FskInfo res = new FskInfo();
 *       res.fsk = fsk;
 *       res.children = Collections.emptyList(); // Must not be null because server expects an array
 *       res.parents = Collections.emptyList();  // same
 *       return res;
 *     }
 *   }
 *   
 *   QueryRequest q = new QueryRequest(Collections.singletonList(FskInfo.query("dog/N1")));
 *   QueryResponse<FskInfo> r = kbClient.query(q, FskInfo.class);
 *   FskInfo fi = r.getResult().get(0);
 * }
 * </pre>
 */
public class QueryRequest extends RequestBase {


  public QueryRequest() {
  }
  
  /**
   * Constructor with pre-formatted query string.
   * @param query A string with the JSON template expected by the server.
   */
  public QueryRequest(String query) {
    setQuery(query);
  }
  
  /**
   * Construct specifying the objects to transmit as requests.
   * @param qrys iterables with objects to encode as the request.
   * @throws IdiliaClientException when the iterable collection cannot be serialized to a JSON string.
   * @see #setQuery(Iterable)
   */
  public QueryRequest(Iterable<? extends Object> qrys) throws IdiliaClientException {
    setQuery(qrys);
  }
  
  /**
   * Construct specifying the object to transmit as a request.
   * @param qry object to encode as the request.
   * @throws IdiliaClientException when the object cannot be serialized to a JSON string.
   * @see #setQuery(Iterable)
   */
  public QueryRequest(Object qry) throws IdiliaClientException {
    setQuery(Collections.singletonList(qry));
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
  
  
  /**
   * Specifies the query to transmit as a series of objects, each one representing one
   * expected result. The response will include the result for each in the same order.
   * The objects should be of the same type to allow JSON recovery to a user specified POJO class.
   * Otherwise recovery will be to a generic Object.
   * @param qrys iterable with the objects to encode.
   * @throws IdiliaClientException if the given objects cannot be converted to a JSON string
   */
  public void setQuery(Iterable<? extends Object> qrys) throws IdiliaClientException {
    try {
      query = new ObjectMapper().writeValueAsBytes(qrys);
    } catch (JsonProcessingException e) {
      throw new IdiliaClientException(e);
    }
  }
  

  @Override
  final public String requestPath() {
    return new String("/1/kb/query.json");
  }
  
  
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IdiliaClientException {
    
    if (this.query == null)
      throw new IdiliaClientException("No query specified");
    
    // Add base parameters
    super.getHttpQueryParms(parms);
    
    // Add parameters from this class
    parms.add(new BasicNameValuePair("query", new String(query, StandardCharsets.UTF_8)));
  }
  
  /** Return the content to sign when creating the authentication information */
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
