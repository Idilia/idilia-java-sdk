package com.idilia.services.kb;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;

class QueryCodec {

  static <T> QueryResponse<T> decode(
      ObjectMapper jsonMapper, Class<T> tpRef,
      HttpEntity rxEntity) throws IdiliaClientException, JsonParseException, UnsupportedOperationException, IOException {
    
    String ct = rxEntity.getContentType().getValue();
    if (!ct.startsWith("application/json"))
      throw new IdiliaClientException("Unexpected mime type from server: " + ct);

    // Parse the JSON response where we cast the "result" member into the supplied Result
    QueryResponse<T> resp = new QueryResponse<T>();
    JsonParser jp = jsonMapper.getFactory().createParser(rxEntity.getContent());
    jp.nextToken(); // skip object boundary
    while (jp.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = jp.getCurrentName();
      jp.nextToken();
      if (fieldName.contentEquals("status"))
        resp.setStatus(jp.getIntValue());
      else if (fieldName.contentEquals("requestId"))
        resp.setRequestId(jp.getText());
      else if (fieldName.contentEquals("errorMsg"))
        resp.setErrorMsg(jp.getText());
      else if (fieldName.contentEquals("result"))
      {
        jp.nextToken(); // skip over array start
        resp.setResult(new ArrayList<>());
        while (jp.getCurrentToken() != JsonToken.END_ARRAY) {
          T r = (T) jp.readValueAs(tpRef);
          resp.addResult(r);
          jp.nextToken(); // skip end of object
        }
      }
    }

    return resp;
  }
}
