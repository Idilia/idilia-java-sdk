package com.idilia.services.base;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

public class RetryHandler implements HttpRequestRetryHandler {

  public boolean retryRequest(IOException exception, int executionCount,
      HttpContext context) {
    if (executionCount >= 3) {
      // Do not retry if over max retry count
      return false;
    }
    if (exception instanceof InterruptedIOException) {
      // Timeout
      return false;
    }
    if (exception instanceof UnknownHostException) {
      // Unknown host
      return false;
    }
    if (exception instanceof ConnectTimeoutException) {
      // Connection refused
      return false;
    }
    if (exception instanceof SSLException) {
      // SSL handshake exception
      return false;
    }
    HttpClientContext clientContext = HttpClientContext.adapt(context);
    HttpResponse response = clientContext.getResponse();

    // On unavailable with Retry-after, wait for the time and then retry
    if (response != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
      Header ra = response.getFirstHeader("Retry-After");
      if (ra != null && ra.getValue() != null) {
        try {
          int secs = Integer.parseInt(ra.getValue());
          Thread.sleep(secs * 1000);
          return true;
        } catch (NumberFormatException e) {
        } catch (InterruptedException e) {
          return false;
        }
      }
    }

    // Always retries
    return true;
  }
}
