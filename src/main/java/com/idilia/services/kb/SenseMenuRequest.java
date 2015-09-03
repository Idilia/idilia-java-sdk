package com.idilia.services.kb;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class SenseMenuRequest extends MenuRequest {
  
  public final String getText() {
    return text;
  }
  public final SenseMenuRequest setText(String text) {
    this.text = text;
    return this;
  }

  public String getSelectedFsk() {
    return selectedFsk;
  }
  public SenseMenuRequest setSelectedFsk(String s) {
    this.selectedFsk = s;
    return this;
  }
  
  @Override
  public SenseMenuRequest setFskInfos(String fskInfos) {
    super.setFskInfos(fskInfos);
    return this;
  }
  @Override
  public SenseMenuRequest setFilters(String filters) {
    super.setFilters(filters);
    return this;
  }
  @Override
  public SenseMenuRequest setCollapsing(String collapsing) {
    super.setCollapsing(collapsing);
    return this;
  }
  @Override
  public SenseMenuRequest setTemplate(String t) {
    super.setTemplate(t);
    return this;
  }
  
 @Override
  final public String requestPath() {
    return new String("/1/kb/sense_menu.json");
  }
  
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IllegalStateException {
    if (text == null || text.isEmpty())
      throw new IllegalStateException("No text provided.");
    parms.add(new BasicNameValuePair("text", text));
    if (selectedFsk != null)
      parms.add(new BasicNameValuePair("selectedFsk", selectedFsk));
    super.getHttpQueryParms(parms);
  }
  
  // Return the content to sign when creating the authentication information
  @Override
  final public byte[] toSign() throws IOException {
    return text.getBytes();
  }

  private String text;                    // when no supplying a tf
  private String selectedFsk; // sense to mark as selected in 
}
