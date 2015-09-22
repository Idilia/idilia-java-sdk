package com.idilia.services.kb;

import java.util.Collections;
import java.util.List;

import com.idilia.services.kb.objects.NeInfo;

public class KbQuery {

  public String fs;
  public String definition;
  public List<NeInfo> neInfos;

  
  static KbQuery build(String fs) {
    KbQuery q = new KbQuery();
    q.fs = fs;
    q.neInfos = Collections.emptyList();
    return q;
  }
  
}