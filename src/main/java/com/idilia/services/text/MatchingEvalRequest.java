package com.idilia.services.text;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.RequestBase;
import com.idilia.tagging.Sense;

/**
 * Message request to the matching/eval API
 *
 */
public class MatchingEvalRequest extends RequestBase {

  private String expression;
  private List<String> documents;
  
  final String getExpression() {
    return expression;
  }

  /**
   * Set the match expression words and their meaning.
   * <p>
   * This information is normally obtained using method "sensesAsObjects" in the jquery
   * tagging menu plugin.
   * <p>
   * @param senses A Sense for each word in the match expression
   * @throws JsonProcessingException if the given senses cannot be serialized to a string
   */
  public final void setExpression(List<Sense> senses) throws JsonProcessingException {
    this.expression = jsonMapper.writeValueAsString(senses);
  }
  
  final List<String> getDocuments() {
    return documents;
  }

  /**
   * Set the list of documents to evaluate.
   * 
   * @param documents plain text string for each document to evaluate
   */
  public final void setDocuments(List<String> documents) {
    this.documents = documents;
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
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IllegalStateException {
    super.getHttpQueryParms(parms);
    parms.add(new BasicNameValuePair("expression", expression));
    
    // Convert the array of documents to a JSON array
    try {
      parms.add(new BasicNameValuePair("documents", jsonMapper.writeValueAsString(documents)));
    } catch (IOException ioe) {
    }
  }
  
  static private final ObjectMapper jsonMapper = new ObjectMapper();
}
