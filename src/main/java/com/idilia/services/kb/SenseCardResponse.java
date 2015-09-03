package com.idilia.services.kb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idilia.services.base.ResponseBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SenseCardResponse  extends ResponseBase {
  
  public String card;
}
