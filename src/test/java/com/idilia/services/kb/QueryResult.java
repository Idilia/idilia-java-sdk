package com.idilia.services.kb;

import java.util.ArrayList;

import com.idilia.services.kb.objects.NeInfo;

public class QueryResult {
  public String getFs() {
    return fs;
  }

  public void setFs(String fs) {
    this.fs = fs;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public String fs;
  public String definition;
  public ArrayList<NeInfo> neInfos;

  public QueryResult() {
  }
}