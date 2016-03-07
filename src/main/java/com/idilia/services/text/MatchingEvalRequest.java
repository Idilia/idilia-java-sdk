package com.idilia.services.text;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.RequestBase;
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
    onDisjunction,
    
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
  
  /**
   * Specify matching behavior when the search term is not present in the provided document.
   * @param val
   */
  public void setRequireTerm(RequireTerm val) {
    this.requireTerm = val;
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
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IdiliaClientException {
    super.getHttpQueryParms(parms);
    parms.add(new BasicNameValuePair("expression", expression));
    parms.add(new BasicNameValuePair("documents", documents));
    if (requireTerm != null)
      parms.add(new BasicNameValuePair("requireTerm", requireTerm.toString()));
  }
  
  private String expression;
  private String documents;
  private RequireTerm requireTerm;
  static private final ObjectMapper jsonMapper = new ObjectMapper();
}
