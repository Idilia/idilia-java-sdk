package com.idilia.services.kb;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.idilia.services.base.RequestBase;

public abstract class MenuRequest extends RequestBase {
  
  
  //
  // Accessors
  //
  
  public final String getFskInfos() {
    return fskInfos;
  }
  public MenuRequest setFskInfos(String fskInfos) {
    this.fskInfos = fskInfos;
    return this;
  }


  public final String getFilters() {
    return filters;
  }
  public MenuRequest setFilters(String filters) {
    this.filters = filters;
    return this;
  }


  public final String getCollapsing() {
    return collapsing;
  }
  public MenuRequest setCollapsing(String collapsing) {
    this.collapsing = collapsing;
    return this;
  }

  public final String getAddAnySense() {
    return addAnySense;
  }
  
  public MenuRequest setAddAnySense()
  {
    this.addAnySense = "true";
    return this;
  }

  public final String getAddCreateSense() {
    return addCreateSense;
  }
  
  public MenuRequest setAddCreateSense()
  {
    this.addCreateSense = "true";
    return this;
  }

  public final String getTemplate() {
    return this.tmplt;
  }
  public MenuRequest setTemplate(String t) {
    this.tmplt = t;
    return this;
  }

  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IllegalStateException {
    if (fskInfos != null)
      parms.add(new BasicNameValuePair("fskInfos", fskInfos));
    if (filters != null)
      parms.add(new BasicNameValuePair("filters", filters));
    if (collapsing != null)
      parms.add(new BasicNameValuePair("collapsing", collapsing));
    if (tmplt != null)
      parms.add(new BasicNameValuePair("template", tmplt));
    if (addAnySense != null)
      parms.add(new BasicNameValuePair("addAnySense", addAnySense));
    if (addCreateSense != null)
      parms.add(new BasicNameValuePair("addCreateSense", addCreateSense));
    super.getHttpQueryParms(parms);
  }
  
  // Policy elements
  private String fskInfos;
  private String filters;
  private String collapsing;
  private String tmplt = "image_v2";
  private String addAnySense;
  private String addCreateSense;
}
