package com.idilia.services.text;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.idilia.services.base.ResponseBase;

/**
 * Response from the matching/eval API.
 *
 */
public class MatchingEvalResponse extends ResponseBase {

  /**
   * Describes the matching model status for a sensekey in the match expression.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  static public class SkModelStatus {

    /**
     * Return the sensekey in the search expression for which this status applies.
     * <p>
     * @return sensekey of the status.
     */
    public final String getFsk() {
      return fsk;
    }
    
    /**
     * Return the text in the search expression for which this status applies.
     * <p>
     * @return text fragment of the search expression.
     */
    public final String getText() {
      return text;
    }

    /**
     * Return numeric code for the status.
     * <ul>
     * <li>1 Ok
     * <li>2 No Counter Examples
     * <li>3 Very Likely
     * <li>4 Very Unlikely
     * <li>5 Counter Examples Only
     * <li>6 Sparse
     * </ul>
     * <p>
     * @return numeric code for the status.
     */
    public final Integer getCode() {
      return code;
    }

    /**
     * Return string value for the status.
     * <p>
     * @return string value for the status
     */
    public final String getDesc() {
      return desc;
    }

    /**
     * Return whether the sensekey was used when matching. When false, matching
     * was performed using the text.
     * <p>
     * @return true if matching used the sensekey
     */
    public final Boolean wasUsed() {
      return used;
    }

    public String fsk;
    public String text;
    public Integer code;
    public String desc;
    public Boolean used;
  }

  private List<Double> result;

  private List<SkModelStatus> skModelStatuses;

  
  /**
   * Creates an empty object. Normally not used by application code.
   */
  MatchingEvalResponse() {
  }

  /**
   * Creates an object with an error condition. Normally not used by application
   * code.
   */
  MatchingEvalResponse(int status, String errorMsg) {
    super(status, errorMsg);
  }

  /**
   * Obtain the result computed by the server for each document. The list is
   * ordered in the same order as the documents provided in
   * MatchingEvalRequest.setDocuments.
   * <p>
   * For each document, the result can be:
   * <ul>
   * <li>&lt; 0: The document does not match.
   * <li>== 0: The analysis is inconclusive and the document may or may not
   * match.
   * <li>&gt; 0: The document matches.
   * </ul>
   * <p>
   * 
   * @return result code for each document
   */
  public List<Double> getResult() {
    return result;
  }

  /**
   * @param result
   *          the result to set
   */
  void setResult(List<Double> result) {
    this.result = result;
  }

  /**
   * Obtain the sense key model status (SkModelStatus) for each (text,sensekey) pair in the
   * request expression.
   * <p>
   * 
   * @return SkModelStatus for each (text,sensekey) pair in the matching expression
   */
  public List<SkModelStatus> getSkModelStatuses() {
    return skModelStatuses;
  }

  /**
   * @param skModelStatuses
   *          the skModelStatuses to set
   */
  void setSkModelStatuses(List<SkModelStatus> skModelStatuses) {
    this.skModelStatuses = skModelStatuses;
  }
}
