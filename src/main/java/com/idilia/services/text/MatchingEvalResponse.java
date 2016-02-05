package com.idilia.services.text;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.idilia.services.base.ResponseBase;


/**
 * Response from the matching/eval API.
 *
 */
public class MatchingEvalResponse extends ResponseBase {

  private List<Integer> result;

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class SkModelStatus {
    public String sk;
    public Integer code;
    public String desc;
    public Boolean used;
  }

  private List<SkModelStatus> skModelStatuses;
  
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

  /**
   * Obtain the sense key model status (SkModelStatus) for each sk in the request
   * expression.
   * <p>
   * For each sense key, the model status is composed of:
   * <ul>
   *   <li> sk: sense key for which the model status applies
   *   <li> code, desc: integer and description for the status
   *    0 Ok
   *    1 No Counter Examples
   *    2 Very Likely
   *    3 Very Unlikely
   *    4 Counter Examples Only
   *    5 Sparse
   *   <li> used: boolean indicating if the model is being used at all. If not,
   *        presence of the query string is enough
   * </ul>
   * <p>
   * @return SkModelStatus for each sk in the expression
   */
  public List<SkModelStatus> getSkModelStatuses() {
    return skModelStatuses;
  }

  /**
   * @param skModelStatuses the skModelStatuses to set
   */
  void setSkModelStatuses(List<SkModelStatus> skModelStatuses) {
    this.skModelStatuses = skModelStatuses;
  }
}
