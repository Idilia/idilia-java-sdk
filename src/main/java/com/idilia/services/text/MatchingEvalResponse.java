package com.idilia.services.text;

import java.util.List;

import com.idilia.services.base.ResponseBase;

/**
 * Response from the matching/eval API.
 *
 */
public class MatchingEvalResponse extends ResponseBase {

  private List<Integer> result;
  
  /**
   * Creates an empty object. Normally not used by application code.
   */
  MatchingEvalResponse() {}

  /**
   * Creates an object with an error condition. Normally not used by application code.
   */
  MatchingEvalResponse(int status, String errorMsg) {
    super(status, errorMsg);
  }

  /**
   * Obtain the result computed by the server for each document. The list is ordered
   * in the same order as the documents provided in MatchingEvalRequest.setDocuments.
   * <p>
   * For each document, the result can be:
   * <ul>
   * <li> &lt; 0: The document does not match.
   * <li> == 0: The analysis is inconclusive and the document may or may not match.
   * <li> &gt; 0: The document matches.
   * </ul>
   * <p>
   * @return result code for each document
   */
  public List<Integer> getResult() {
    return result;
  }

  /**
   * @param result the result to set
   */
  void setResult(List<Integer> result) {
    this.result = result;
  }

}
