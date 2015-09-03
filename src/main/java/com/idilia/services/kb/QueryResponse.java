/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.kb;

import java.util.ArrayList;

import com.idilia.services.base.ResponseBase;


/**
 * Response from the KnowledgeBase server.
 *
 */
public class QueryResponse extends ResponseBase {

  
  /**
   * Returns the server's response as an array of objects
   * where each object contains the result of one query.
   * This is using Simple Data Binding from Jackson JSON.
   */
  public final ArrayList<Object> getResult() {
    return this.result;
  }
  
  
  /**
   * Store the query results received in the server's response. Normally not used by application code.
   */
  public final void setResult(ArrayList<Object> result) {
    this.result = result;
  }
  
  /**
   * Adds a query results received in the server's response. Normally not used by application code.
   */
  public final void addResult(Object result) {
    this.result.add(result);
  }

  /**
   * Creates an empty object. Normally not used by application code.
   */
  public QueryResponse() {}

  /**
   * Creates an object with an error condition. Normally not used by application code.
   */
  public QueryResponse(int status, String errorMsg) {
    super(status, errorMsg);
  }
  
  private ArrayList<Object> result;
}
