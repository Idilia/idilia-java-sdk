/*
 * Definition for elements returned by properties "neInfo" and "neInfos"
 */
package com.idilia.services.kb.objects;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class NeInfo {
  private String neT;
  private String neST;
  
  public NeInfo() {
  }
  
  public NeInfo(JsonParser jp) throws JsonParseException, IOException {
    while (jp.nextToken() != JsonToken.END_OBJECT) {
      char c = jp.getTextCharacters()[jp.getTextOffset() + 2];
      jp.nextToken();
      if (c == 'T')
        neT = jp.getText();
      else if (c == 'S')
        neST = jp.getText();
    }
  }
  
  public String getNeT() {
    return neT;
  }
  public void setNeT(String neT) {
    this.neT = neT;
  }
  public String getNeST() {
    return neST;
  }
  public void setNeST(String neST) {
    this.neST = neST;
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof NeInfo)) return false;
    NeInfo other = (NeInfo) o;
    return neT.equals(other.neT) && Objects.equals(neST, other.neST);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(neT, neST);
  }
  
  @Override
  public String toString() {
    return neST == null ? neT : neST;
  }
}
