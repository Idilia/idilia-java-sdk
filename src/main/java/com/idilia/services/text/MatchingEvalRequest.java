package com.idilia.services.text;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.RequestBase;
import com.idilia.services.base.ResponseBase;
import com.idilia.tagging.Sense;

/**
 * Message request to the matching/eval API
 *
 */
public class MatchingEvalRequest extends RequestBase {

  /** Value for parameter requireTerm */
  public enum RequireTerm {
    /** Rejects documents where search terms are not all present. */
    yes,
    
    /** Rejects documents where search terms are not present only if the search expression includes "or" clauses. */
    onDisjunctionOnly,
    
    /** Do not consider whether the search terms are present. */
    no,
  }
  
  /**
   * Set the match expression words and their meaning.
   * <p>
   * This information is normally obtained using method "sensesAsObjects" in the jquery
   * tagging menu plugin.
   * <p>
   * @param senses A Sense for each word in the match expression
   * @throws IdiliaClientException if the given senses cannot be serialized
   */
  public final void setExpression(List<Sense> senses) throws IdiliaClientException {
    try {
      this.expression = jsonMapper.writeValueAsString(senses);
    } catch (JsonProcessingException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  @JsonIgnoreProperties(ignoreUnknown = true)
  static public class SkModelOverride {
    /**
     * Return the offset of the sensekey in the expression that is overridden
     * <p>
     * @return offset of the override
     */
    public final Integer getOffset() {
      return offset;
    }
    
    /**
     * Return the override value of the offset.
     * <p>
     * @return text fragment of the search expression.
     */
    public final Boolean getUse() {
      return use;
    }

    public Integer offset;
    public Boolean use;
  }
  
  /**
   * Set sk model overrides
   * 
   * @param skModelOverrides list of SkModelOverride to be applied to the request
   */
  
  public void setSkModelOverrides(List<SkModelOverride> skModelOverrides) {
    if (skModelOverrides != null)
      try {
        this.skModelOverrides = jsonMapper.writeValueAsString(skModelOverrides);
      } catch (JsonProcessingException e) {
        throw new IdiliaClientException(e);
      }
 }
  
  /**
   * Specify matching behavior when the search term is not present in the provided document.
   * @param val RequireTerm option to be used while matching
   */
  public void setRequireTerm(RequireTerm val) {
    this.requireTerm = val;
  }

  /**
   * Set the list of documents to evaluate.
   * 
   * @param documents plain text string for each document to evaluate
   * @throws IdiliaClientException if the given documents cannot be serialized
   */
  public final void setDocuments(List<String> documents) throws IdiliaClientException {
    try {
      this.documents = jsonMapper.writeValueAsString(documents);
    } catch (JsonProcessingException e) {
      throw new IdiliaClientException(e);
    }
  }
  
  @Override
  public byte[] toSign() throws IOException {
    return expression.getBytes();
  }

  @Override
  public String requestPath() {
    return "/1/text/matching/eval.json";
  }
  
  @Override
  public Class<? extends ResponseBase> responseClass() { return MatchingEvalResponse.class; }
  
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IdiliaClientException {
    super.getHttpQueryParms(parms);
    parms.add(new BasicNameValuePair("expression", expression));
    if (requireTerm != null)
      parms.add(new BasicNameValuePair("requireTerm", requireTerm.toString()));
    parms.add(new BasicNameValuePair("documents", documents));
    if (skModelOverrides != null)
      parms.add(new BasicNameValuePair("skModelOverrides", skModelOverrides));
  }
  
  private String expression;
  private RequireTerm requireTerm;
  private String documents;
  private String skModelOverrides;
  static private final ObjectMapper jsonMapper = new ObjectMapper();
}
