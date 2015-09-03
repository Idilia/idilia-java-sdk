/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.text;

import java.io.IOException;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.idilia.services.base.ResponseBase;


/**
 * Response from the paraphrase server.
 *
 */
public class ParaphraseResponse extends ResponseBase {

  /**
   * Class to represent one paraphrase
   */
  public static class Paraphrase {
    private String surface;
    private double weight = 0.0;
    private String transformations = "";
    
    /**
     * Recreate from JSON stream
     */
    Paraphrase(JsonParser jp) throws JsonParseException, IOException {
      while (jp.nextToken() != JsonToken.END_OBJECT) {
        char c = jp.getTextCharacters()[jp.getTextOffset()];
        jp.nextToken(); // move to value or end object
        if (c == 's')
          surface = jp.getText();
        else if (c == 'w')
          weight = jp.getValueAsDouble();
        else if (c == 't')
          transformations = jp.getText();
      }
    }
    
    public void setSurface(String s) { surface = s; }
    public void setWeight(double w) { weight = w;  }
    public void setTransformations(String t) { transformations = t; }
    
    /**
     * Text of the paraphrase. May be senses or words
     *
     * @return paraphrase text
     */
    public String getSurface() { return surface; }
    
    /**
     * Weight of the paraphrase. Original query has weight == 1 and
     * others have decreasing weights based on usefulness of the transformations
     * performed to generate the paraphrase.
     *
     * @return paraphrase weight
     */
    public double getWeight() { return weight; }
    
    /**
     * Comma separated list of transformations applied to yield the paraphrase
     *
     * @return comma separated list of transformations
     */
    public String getTransformations() { return transformations; }
  }
  
  
  /** 
   * Class to represent the query confidence
   */
  public static class QueryConfidence {
    
    /**
     * Confidence that for all words in the query with open class interpretations, 
     * the most probable fine sense of their sense distribution is correct. 
     * This is the most conservative threshold available.
     */
    public Double confCorrectFineMostProbable = 0.0;

    /**
     * Confidence that for all words in the query with open class interpretations, 
     * the correct fine sense is present in their sense distributions. This 
     * threshold is more relaxed than confCorrectFineMostProbable because 
     * the correct sense need not be the most probable at each position.
     * 
     */
    public Double confCorrectFinePresent = 0.0;

    /**
     * Confidence that for all words in the query with open class interpretations, 
     * the most probable coarse sense of their sense distribution is correct.
     */
    public Double confCorrectCoarseMostProbable = 0.0;

    /**
     * Confidence that for all words in the query with open class interpretations, 
     * the correct coarse sense is present in their sense distributions.
     */
    public Double confCorrectCoarsePresent = 0.0;
  }
  
  
  /**
   * Returns the generated paraphrases
   *
   * @return paraphrases
   */
  public final Vector<Paraphrase> getParaphrases() {
    return paraphrases;
  }
  
  /**
   * Returns the server's annotated document as a byte buffer.
   *
   * @return disambiguation result
   */
  public final DisambiguatedDocument getWsdResult() {
    return wsdResult;
  }
  
  /**
   * Return the QueryConfidence object
   *
   * @return query confidence
   */
  public final QueryConfidence getQueryConfidence() {
    return queryConfidence;
  }
  
  
  /**
   * Store the annotated document received in the server's response. Normally not used by application code.
   *
   * @param r disambiguated document
   */
  public final void setWsdResult(DisambiguatedDocument r) {
    this.wsdResult = r;
  }
  
  
  /**
   * Store the querfy confidence object received in the server's response. Normally not used by application code.
   *
   * @param qf query confidence
   */
  public void setQueryConfidence(QueryConfidence qf) {
    this.queryConfidence = qf;
  }
  
  /**
   * Store the paraphrases
   *
   * @param p paraphrase
   */
  public final void addParaphrase(Paraphrase p) {
    paraphrases.add(p);
  }
  
  /**
   * Creates an empty object. Normally not used by application code.
   */
  public ParaphraseResponse() {}

  /**
   * Creates an object with an error condition. Normally not used by application code.
   *
   * @param status status
   * @param errorMsg error message
   */
  public ParaphraseResponse(int status, String errorMsg) {
    super(status, errorMsg);
  }
  
  /**
   * Recreate from JSON stream
   *
   * @throws JsonParseException
   * @throws JsonProcessingException
   * @throws IOException
   */
  public ParaphraseResponse(JsonParser jp) throws JsonParseException, JsonProcessingException, IOException {
    while (jp.nextToken() != JsonToken.END_OBJECT) {
      char c = jp.getTextCharacters()[jp.getTextOffset()];
      jp.nextToken();
      if (c == 'r') {
        setRequestId(jp.getText());
      } else if (c == 'e') {
        setErrorMsg(jp.getText());
      } else if (c == 's') {
        setStatus(jp.getValueAsInt());
      } else if (c == 'q') {
        // Start of an object. Process it all using the mapper
        QueryConfidence qf = jp.readValueAs(QueryConfidence.class);
        setQueryConfidence(qf);
      } else if (c == 'p') {
        while (jp.nextToken() != JsonToken.END_ARRAY)
          paraphrases.add(new Paraphrase(jp));
        paraphrases.trimToSize();
      }
    }
  }
  
  
  private DisambiguatedDocument wsdResult;
  private Vector<Paraphrase> paraphrases = new Vector<Paraphrase>();
  private QueryConfidence queryConfidence;
}
