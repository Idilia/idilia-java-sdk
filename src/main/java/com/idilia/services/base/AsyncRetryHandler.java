package com.idilia.services.base;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;

public class AsyncRetryHandler {

  /**
   * Returns an integer that indicates whether we should retry and when
   * @param exception exception encountered or null if response was received
   * @param executionCount current number of attempts performed
   * @param context
   * @return -1 to not retry, 0 to retry immediately, n to wait "n" seconds before retrying
   */
  public int retryRequest(IOException exception, int executionCount, HttpContext context) {
    if (executionCount >= 3) {
      // Do not retry if over max retry count
      return -1;
    }
    if (exception instanceof InterruptedIOException) {
      // Timeout
      return -1;
    }
    if (exception instanceof UnknownHostException) {
      // Unknown host
      return -1;
    }
    if (exception instanceof ConnectException) {
      // Connection refused
      return -1;
    }
    if (exception instanceof SSLException) {
      // SSL handshake exception
      return -1;
    }
    HttpClientContext clientContext = HttpClientContext.adapt(context);
    HttpResponse response = clientContext.getResponse();

    // On unavailable with Retry-after, wait for the time and then retry
    if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
      Header ra = response.getFirstHeader("Retry-After");
      if (ra != null && ra.getValue() != null) {
        try {
          int secs = Integer.parseInt(ra.getValue());
          return secs;
        } catch (NumberFormatException e) {
        }
      }
    }

    // Retry immediately
    return 0;
  }

}
