package com.idilia.services.text;

import java.io.IOException;

import org.apache.http.HttpEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;

class MatchCodec {

  static MatchResponse decodeMatchResponse(
      ObjectMapper jsonMapper, 
      HttpEntity rxEntity) throws IdiliaClientException, JsonParseException, UnsupportedOperationException, IOException {
    
    if (rxEntity == null)
      throw new IdiliaClientException("Did not received a response from the server");
    
    String ct = rxEntity.getContentType().getValue();
    if (ct.startsWith("application/json"))
    {
      // Single part json message.
      JsonParser jp = jsonMapper.getFactory().createParser(rxEntity.getContent());
      return jp.readValueAs(MatchResponse.class);
      
    } else
      throw new IdiliaClientException("Unexpected content type: " + ct);
  }
}
