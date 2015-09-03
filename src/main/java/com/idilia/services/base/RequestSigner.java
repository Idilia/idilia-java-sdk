/*
 * Class that implements a RequestInterceptor for the HTTP Client.
 * This interceptor adds headers required for authentication by the Idilia servers.
 */
package com.idilia.services.base;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

class RequestSigner implements HttpRequestInterceptor {

  // Class for recording into the HTTPContext the information required to sign
  // the request
  static class SignatureData {
    final public String signTail;
    final public URL url;
    final public IdiliaCredentials credentials;

    SignatureData(String signTail, URL url, IdiliaCredentials creds) {
      this.signTail = signTail;
      this.url = url;
      this.credentials = creds;
    }
  }

  // The runtime method called as the request is dispatched
  // Add the headers that implement the authentication of the request
  public void process(final HttpRequest request, final HttpContext context)
      throws HttpException, IOException {

    // Retrieve from the context the signature information
    SignatureData signData = (SignatureData) context
        .getAttribute("idlSignData");

    // Format a date as per RFC 2616
    final Date now = new Date();
    final DateFormat dateFmt = new SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss z");
    dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    final String dateS = dateFmt.format(now);

    String toSign = dateS + signData.signTail;

    // Compute the signature
    SecretKeySpec signingKey = new SecretKeySpec(signData.credentials
        .getSecretKey().getBytes(), ClientBase.HMAC_SHA_ALGORITHM);
    try {
      Mac mac = Mac.getInstance(ClientBase.HMAC_SHA_ALGORITHM);
      mac.init(signingKey);
      byte[] rawHmac = mac.doFinal(toSign.getBytes());
      byte[] signature = Base64.encodeBase64(rawHmac);
      request.setHeader("Authorization",
          "IDILIA " + signData.credentials.getAccessKey() + ":"
              + new String(signature));
    } catch (NoSuchAlgorithmException e) {
      throw new HttpException("Caught when trying to sign request", e);
    } catch (InvalidKeyException e) {
      throw new HttpException("Caught when trying to sign request", e);
    }

    // Add the authentication headers
    request.setHeader("Date", dateS);
  }
}
