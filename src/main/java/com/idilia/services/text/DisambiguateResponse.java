/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.text;

import java.util.ArrayList;

import com.idilia.services.base.ResponseBase;


/**
 * Response from the document server.
 *
 */
public class DisambiguateResponse extends ResponseBase {

  /**
   * Store the annotated document received in the server's response. Normally not used by application code.
   */
  public final void addResult(DisambiguatedDocument r) {
    this.results.add(r);
  }
  
  /**
   * Return the result
   */
  public final DisambiguatedDocument getResult() {
    return results.get(results.size() - 1);
  }
  
  /**
   * Return all the results
   */
  public final ArrayList<DisambiguatedDocument> getResults() {
    return results;
  }
  
  /**
   * Creates an empty object. Normally not used by application code.
   */
  public DisambiguateResponse() {}

  /**
   * Creates an object with an error condition. Normally not used by application code.
   */
  public DisambiguateResponse(int status, String errorMsg) {
    super(status, errorMsg);
  }
  
  private ArrayList<DisambiguatedDocument> results = new ArrayList<DisambiguatedDocument>();
}
