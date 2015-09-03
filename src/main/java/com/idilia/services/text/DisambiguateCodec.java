/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file coding/decoding for the DisambiguateService. Helps keep the DisambiguateResponse/Request
 *     objects lightweight.
 *     
 */
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;

class DisambiguateCodec {

  // Decode the entity received from HTTP into a DisambiguateResponse object
  static DisambiguateResponse decode(ObjectMapper jsonMapper, HttpEntity rxEntity) throws IdiliaClientException, JsonParseException, UnsupportedOperationException, IOException, MessagingException {
    if (rxEntity == null)
      throw new IdiliaClientException("Did not received a response from the server");
    
    String ct = rxEntity.getContentType().getValue();
    if (ct.startsWith("application/json")) {
      // Single part json message.
      JsonParser jp = jsonMapper.getFactory().createParser(rxEntity.getContent());
      return jp.readValueAs(DisambiguateResponse.class);
    } else if (ct.contains("multipart/mixed")) {
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
      JsonParser jp = jsonMapper.getFactory().createParser(body.getInputStream());
      DisambiguateResponse response = jp.readValueAs(DisambiguateResponse.class);
      
      // Recover the WSD results. Can be several when snapshots are enabled.
      if (response.getStatus() == HttpURLConnection.HTTP_OK)
        for (int i = 1; i < mmp.getCount(); ++i)
          response.addResult(extractIndex(mmp, i));
      return response;
    } else {
      throw new IdiliaClientException("Unexpected content type: " + ct);
    }
    
  }
    
  // Helper function to retrieve a DisambiguatedDocument saved as a part.
  // Possibly encoded.
  static DisambiguatedDocument extractIndex(MimeMultipart mmp, int index) throws MessagingException, IOException {
    // Retrieve the body part. If encoded with "gzip", decode it.
    // We want to copy into into the byte buffer stored in the response
    BodyPart doc = mmp.getBodyPart(index);     
    String encoding = null;
    String[] encodings = doc.getHeader("Content-Encoding");
    if (encodings != null && encodings.length == 1 && encodings[0].contentEquals("gzip"))
      encoding = encodings[0];
    return new DisambiguatedDocument(doc.getContentType(), encoding, doc.getInputStream());
  }
}
