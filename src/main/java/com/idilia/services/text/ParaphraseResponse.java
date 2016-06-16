/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
   * Document a sense used in a paraphrase and its location in the paraphrase's text.
   */
  public static class Sense {
    private int start;
    private int end;
    private String fsk;
    private String fs;
    
    /**
     * Recreate from JSON stream
     */
    Sense(JsonParser jp) throws JsonParseException, IOException {
      while (jp.nextToken() != JsonToken.END_OBJECT) {
        String name = jp.getText();
        jp.nextToken();
        if (name.contentEquals("start"))
          start = jp.getValueAsInt();
        else if (name.contentEquals("end"))
          end = jp.getValueAsInt();
        else if (name.contentEquals("fsk"))
          fsk = jp.getText();
        else if (name.contentEquals("fs"))
          fs = jp.getText();
        else
          jp.skipChildren();
      }
    }

    /**
     * Return the start offset of the sense in {@link Paraphrase#getText}.
     *
     * @return start offset
     */
    public final int getStart() {
      return start;
    }

    /**
     * Return the one past the offset of the last character for the sense in {@link Paraphrase#getText}.
     *
     * @return one past the last character offset
     */
    public final int getEnd() {
      return end;
    }

    /**
     * Return the string of the sense key present in the paraphrase in the range returned by {@link #getStart}
     * and {@link #getEnd}.
     *
     * @return sense key
     */
    public final String getFsk() {
      return fsk;
    }
    
    /**
     * Return the string of the sense id present in the paraphrase in the range returned by {@link #getStart}
     * and {@link #getEnd}.
     *
     * @return fine sense
     */
    public final String getFs() {
      return fs;
    }
    
    @Override
    public String toString() {
      return fsk;
    }
  }
  
  
  /**
   * Class to represent one paraphrase
   */
  public static class Paraphrase {
    private String text;
    private String surface;
    private double weight = 0.0;
    private String transformations = "";
    private ArrayList<Sense> senses = new ArrayList<>();
    
    /**
     * Recreate from JSON stream
     */
    Paraphrase(JsonParser jp) throws JsonParseException, IOException {
      while (jp.nextToken() != JsonToken.END_OBJECT) {
        char c = jp.getTextCharacters()[jp.getTextOffset()];
        char c2 = jp.getTextCharacters()[jp.getTextOffset()+1];
        jp.nextToken(); // move to value or end object
        if (c == 't' && c2 == 'e')
          text = jp.getText();
        else if (c == 's' && c2 == 'e')  {
          while (jp.nextToken() != JsonToken.END_ARRAY)
            senses.add(new Sense(jp));
          senses.trimToSize();
        }
        else if (c == 's')
          surface = jp.getText();
        else if (c == 'w')
          weight = jp.getValueAsDouble();
        else if (c == 't')
          transformations = jp.getText();
        else
          jp.skipChildren();
      }
    }
    
    /**
     * Text of the paraphrase. Unlike {@link #getSurface}, the compounds
     * used during the generation are not quoted.
     * 
     * @return plain text for the paraphrase.
     */
    public String getText() {
      return text;
    }
    
    /**
     * Text of the paraphrase. Where the paraphrase was generated from a compound
     * sense, the text of the compound is quoted. This is useful when 
     * using the paraphrase with a search engine to keep the compound words
     * collocated.
     *
     * @return paraphrase text with quoted compounds
     */
    public String getSurface() {
      return surface;
    }
    
    /**
     * Weight of the paraphrase. Original query has weight == 1 and
     * others have decreasing weights based on usefulness of the transformations
     * performed to generate the paraphrase.
     *
     * @return paraphrase weight
     */
    public double getWeight() {
      return weight;
    }
    
    /**
     * Comma separated list of transformations applied to yield the paraphrase
     *
     * @return comma separated list of transformations
     */
    public String getTransformations() {
      return transformations;
    }
    
    /**
     * Return the senses that were used to generate the paraphrase. They are sorted
     * in increasing start offset.
     *
     * @return senses list
     */
    public List<Sense> getSenses() {
      return senses;
    }
  }
  
  
  /** 
   * Class to represent the query confidence
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
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
  public final List<Paraphrase> getParaphrases() {
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
   * Recreate from JSON stream
   *
   * @param  JsonParser positioned at the start of this object
   * @throws JsonParseException when failing to parse the JSON
   * @throws JsonProcessingException when failing to parse the JSON
   * @throws IOException when failing to parse the JSON
   */
  ParaphraseResponse(JsonParser jp) throws JsonParseException, JsonProcessingException, IOException {
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
      } else {
        jp.skipChildren();
      }
    }
  }
  
  
  private DisambiguatedDocument wsdResult;
  private ArrayList<Paraphrase> paraphrases = new ArrayList<Paraphrase>();
  private QueryConfidence queryConfidence;
}
