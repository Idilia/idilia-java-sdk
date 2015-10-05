/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.kb;

import java.util.ArrayList;

import com.idilia.services.base.ResponseBase;


/**
 * Response from the KnowledgeBase server for a QueryRequest.
 *
 */
public class QueryResponse<T extends Object> extends ResponseBase {

  
  /**
   * Returns the server's response as an array of objects
   * where each object contains the result of one query.
   * This is using Simple Data Binding from Jackson JSON.
   * @return a list of of objects for each recovered result from the server. Each of these
   *          object can be casted to the POJO type specified in 
   *          Client#query(QueryRequest, Class&lt;T&gt;)
   */
  public final ArrayList<T> getResult() {
    return this.result;
  }
  
  
  /**
   * Store the query results received in the server's response. Normally not used by application code.
   * @param result the recovered objects
   */
  void setResult(ArrayList<T> result) {
    this.result = result;
  }
  
  /**
   * Adds a query results received in the server's response. Normally not used by application code.
   * @param result a recovered object
   */
  void addResult(T result) {
    this.result.add(result);
  }

  /**
   * Creates an empty object. Normally not used by application code.
   */
  QueryResponse() {}

  private ArrayList<T> result;
}
