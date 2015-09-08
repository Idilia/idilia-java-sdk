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
   * @param document recovered
   */
  final void addResult(DisambiguatedDocument r) {
    this.results.add(r);
  }
  
  /**
   * Return the result when the request included a single document to process.
   * @return document recovered
   */
  public final DisambiguatedDocument getResult() {
    return results.get(results.size() - 1);
  }
  
  /**
   * Return the results for all the documents requested.
   * @return all documents recovered
   */
  public final ArrayList<DisambiguatedDocument> getResults() {
    return results;
  }
  
  /**
   * Creates an empty object. Normally not used by application code.
   */
  DisambiguateResponse() {}

  private ArrayList<DisambiguatedDocument> results = new ArrayList<DisambiguatedDocument>();
}
