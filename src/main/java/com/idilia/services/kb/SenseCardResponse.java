package com.idilia.services.kb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idilia.services.base.ResponseBase;

/**
 * Response for a SenseCardRequest.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SenseCardResponse  extends ResponseBase {
  
  public String card;
  
  /**
   * Returns the HTML string computed by the KB server for a sense card.
   * @return HTML for the card
   */
  public String getCard() {
    return card;
  }
}
