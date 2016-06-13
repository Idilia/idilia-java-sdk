/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.text;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.idilia.services.base.ResponseBase;

/**
 * Response from the match server.
 *
 */
public class MatchResponse extends ResponseBase {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KeywordMatch {
    public String kw;
    public ArrayList<Integer> position;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KeywordMatches {
    public ArrayList<KeywordMatch> positive;
    public ArrayList<KeywordMatch> negative;
  }
  
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SenseMatch {
    public String foundSk;
    public Double conf;
    public ArrayList<Integer> position;
    public ArrayList<String> reasons;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class FskMatchResult {
    public String fsk;
    public ArrayList<SenseMatch> sks;
    public KeywordMatches kws;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class MatchResult {
    public boolean match;
    public ArrayList<FskMatchResult> matches;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class WSD {
    public String mime;
    public String data;
  }

  public MatchResult result;
  public WSD wsd;
}
