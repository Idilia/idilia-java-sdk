/*
 * Definition of elements returned for property "extRefs"
 */
package com.idilia.services.kb.objects;

public class ExtRefs extends ExtRef {
  private String url;  // the url to obtain the entity

  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
}
