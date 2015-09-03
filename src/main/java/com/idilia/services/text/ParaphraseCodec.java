package com.idilia.services.text;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;

class ParaphraseCodec {
  
  // Decode the entity received from HTTP into a ParaphraseResponse object
  static ParaphraseResponse decode(
      ObjectMapper jsonMapper,
      HttpEntity rxEntity) throws IdiliaClientException, JsonParseException, JsonProcessingException, IOException, MessagingException {
    
    if (rxEntity == null)
      throw new IdiliaClientException("Did not received a response from the server");
    
    String ct = rxEntity.getContentType().getValue();
    if (ct.startsWith("application/json"))
    {
      // Single part json message.
      JsonParser jp = jsonMapper.getFactory().createParser(rxEntity.getContent());
      jp.nextToken();
      return new ParaphraseResponse(jp);
    }
    else if (ct.contains("multipart/mixed"))
    {
      // Use java mail here because the HTTP client classes do not parse a response.
      
      // First recover the content of the HTTP response into a byte array that we can use
      // to build a DataSource as expected by the constructor of the MimeMultipart.
      byte[] bytes = EntityUtils.toByteArray(rxEntity);
      ByteArrayDataSource ds = new ByteArrayDataSource(bytes, rxEntity.getContentType().getValue());
      MimeMultipart mmp = new MimeMultipart(ds);
      
      // Recover the JSON object from the first body part
      BodyPart body = mmp.getBodyPart(0);
      String bodyCt = body.getContentType();
      if (!bodyCt.startsWith("application/json"))
        throw new IOException("Unexpected mime type from server: " + body.getContentType());
      
      // Parse using the stream api
      JsonParser jp = jsonMapper.getFactory().createParser(body.getInputStream());
      jp.nextToken();
      ParaphraseResponse response = new ParaphraseResponse(jp);
      
      // Add the WSD result if present
      if (response.getStatus() == HttpURLConnection.HTTP_OK && mmp.getCount() > 1)
        response.setWsdResult(DisambiguateCodec.extractIndex(mmp, 1));
      return response;
    } else {
      throw new IdiliaClientException("Unexpected content type: " + ct);
    }
  }
}
