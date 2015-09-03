package com.idilia.services.kb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idilia.services.base.ResponseBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SenseMenuResponse extends ResponseBase {

  public String menu;
}
