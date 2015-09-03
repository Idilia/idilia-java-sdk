/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.text;

import java.util.ArrayList;

import com.idilia.services.base.ResponseBase;

/**
 * Response from the match server.
 *
 */
public class MatchResponse extends ResponseBase {

  public static class KeywordMatch {
    public String kw;
    public ArrayList<Integer> position;
  }

  public static class KeywordMatches {
    public ArrayList<KeywordMatch> positive;
    public ArrayList<KeywordMatch> negative;
  }
  
  public static class SenseMatch {
    public String foundSk;
    public Double conf;
    public ArrayList<Integer> position;
    public ArrayList<String> reasons;
  }

  public static class FskMatchResult {
    public String fsk;
    public ArrayList<SenseMatch> sks;
    public KeywordMatches kws;
  }

  public static class MatchResult {
    public boolean match;
    public ArrayList<FskMatchResult> matches;
  }

  public static class WSD {
    public String mime;
    public String data;
  }

  public MatchResult result;
  public WSD wsd;
  
  /**
   * Creates an empty object. Normally not used by application code.
   */
  public MatchResponse() {}

  /**
   * Creates an object with an error condition. Normally not used by application code.
   */
  public MatchResponse(int status, String errorMsg) {
    super(status, errorMsg);
  }
}
