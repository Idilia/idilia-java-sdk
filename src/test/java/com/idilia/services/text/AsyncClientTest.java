package com.idilia.services.text;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;
import com.idilia.tagging.Sense;

public class AsyncClientTest extends TestBase {

  
  @Test
  public void testMatch() {
    // Perform a REST request to the paraphrase server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getMatchApiUrl())) {
      final AtomicBoolean failed = new AtomicBoolean(false);
      final int numReqs = 10;

      final CountDownLatch counter = new CountDownLatch(numReqs);
      for (int i = 0; i < numReqs; ++i) {
        MatchRequest req = new MatchRequest();
        req.setText("I like my Tide detergent!", "text/tweet", StandardCharsets.UTF_8);
        req.setRequestId("testing-services-match-" + (new Random()).nextInt(1000));
        req.setWsdMime("application/x-semdoc+xml");
        req.setFilter("[{\"fsk\":\"Tide/N8\",\"keywords\":{\"positive\":[\"detergent\"],\"negative\":[\"crimson\"]}},{\"fsk\":\"like/V3\",\"keywords\":{\"negative\":[\"is like\"]}}]");

        CompletableFuture<MatchResponse> future = client.matchAsync(req);
        future.handle((response, ex) -> {
          try {
            if (ex != null) {
              logger_.error("encountered exception", ex);
              failed.set(true);
            } else if (response.getStatus() == 200) {
              failed.set(failed.get() || response.getErrorMsg() != null);
              failed.set(failed.get() || !response.result.match);
            } else {
              logger_.error("Failed request: " + response.getStatus() + " " + response.getErrorMsg());
              failed.set(true);
            }
          } catch (Exception e) {
            logger_.error("Caught exception", e);
            failed.set(true);
            Assert.assertTrue(false);
          } finally {
            counter.countDown();
          }
          return client;
        });
      }
      
      counter.await();
      Assert.assertFalse(failed.get());

    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testMatchingEval() {
    // Perform a REST request to the paraphrase server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getMatchApiUrl())) {
      final AtomicBoolean failed = new AtomicBoolean(false);
      final int numReqs = 10;

      final CountDownLatch counter = new CountDownLatch(numReqs);
      for (int i = 0; i < numReqs; ++i) {
        MatchingEvalRequest req = new MatchingEvalRequest();
        req.setDocuments(Arrays.asList("I love to do the laundry with tide.", "tide", "The tide was high when he was at the beach"));
        req.setRequestId("testing-services-match-" + (new Random()).nextInt(1000));
        req.setExpression(Collections.singletonList(new Sense(0, 1, "tide", "Tide/N8")));

        CompletableFuture<MatchingEvalResponse> future = client.matchingEvalAsync(req);
        future.handle((response, ex) -> {
          try {
            if (ex != null) {
              logger_.error("encountered exception", ex);
              failed.set(true);
            } else if (response.getStatus() == 200) {
              failed.set(failed.get() || response.getErrorMsg() != null);
              failed.set(failed.get() || 
                  response.getResult() == null || 
                  response.getResult().size() != 3 || 
                  response.getResult().get(0) <= 0 || 
                  response.getResult().get(1) != 0 || 
                  response.getResult().get(2) >= 0 );
            } else {
              logger_.error("Failed request: " + response.getStatus() + " " + response.getErrorMsg());
              failed.set(true);
            }
          } catch (Exception e) {
            logger_.error("Caught exception", e);
            Assert.assertTrue(false);
            failed.set(true);
          } finally {
            counter.countDown();
          }
          return response;
        });
      }
      
      counter.await();
      Assert.assertFalse(failed.get());

    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testParaphrase() {
    // Perform a REST request to the paraphrase server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getParaphraseApiUrl())) {
      final AtomicBoolean failed = new AtomicBoolean(false);
      final int numReqs = 10;

      final CountDownLatch counter = new CountDownLatch(numReqs);
      for (int i = 0; i < numReqs; ++i) {
        ParaphraseRequest req = new ParaphraseRequest();
        req.setText("my testing query", "text/query", StandardCharsets.UTF_8);
        req.setRequestId("testing-services-para-" + (new Random()).nextInt(1000));
        req.setWsdMime("application/x-semdoc+xml");
  
        CompletableFuture<ParaphraseResponse> future = client.paraphraseAsync(req);
        future.handle((response, ex) -> {
          try {
            if (ex != null) {
              logger_.error("encountered exception", ex);
              failed.set(true);
          } else if (response.getStatus() == 200) {
              failed.set(failed.get() || response.getErrorMsg() != null);
              failed.set(failed.get() || response.getParaphrases().size() <= 1);
              
              InputStream is = response.getWsdResult().getInputStream();
              Scanner scanner = new Scanner(is);
              String theResult = scanner.useDelimiter("\\A").next();
              scanner.close();
              failed.set(failed.get() || theResult.indexOf("query/N1") <= 0);
            } else {
              logger_.error("Failed request: " + response.getStatus() + " " + response.getErrorMsg());
              failed.set(true);
            }
          } catch (Exception e) {
            logger_.error("Caught exception", e);
            Assert.assertTrue(false);
            failed.set(true);
          } finally {
            counter.countDown();
          }
          return response;
        });
      }
      
      counter.await();
      Assert.assertFalse(failed.get());

    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
      Assert.assertTrue(false);
    }
  }
  
  
  @Test
  public void testDisambiguate() {
    // Perform a REST request to the documentation server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getDisambiguateApiUrl())) {
      final AtomicBoolean failed = new AtomicBoolean(false);
      final int numReqs = 10;

      final CountDownLatch counter = new CountDownLatch(numReqs);
      for (int i = 0; i < numReqs; ++i) {
        DisambiguateRequest req = new DisambiguateRequest();
        req.setText("my testing query", "text/query", StandardCharsets.UTF_8);
        req.setRequestId("testing-services-doc-" + (new Random()).nextInt(1000));
        req.setMaxTokens(200);
  
        CompletableFuture<DisambiguateResponse> future = client.disambiguateAsync(req);
        future.handle((response, ex) -> {
          try {
            if (ex != null) {
              logger_.error("encountered exception", ex);
              failed.set(true);
            } else if (response.getStatus() == 200) {
              failed.set(failed.get() || response.getErrorMsg() != null);
              InputStream is = response.getResult().getInputStream();
              Scanner scanner = new Scanner(is);
              String theResult = scanner.useDelimiter("\\A").next();
              scanner.close();
              failed.set(failed.get() || theResult.indexOf("query/N1") <= 0);
            } else {
              logger_.error("Failed request: " + response.getStatus() + " " + response.getErrorMsg());
              failed.set(true);
            }
          } catch (Exception e) {
            logger_.error("Caught exception", e);
            Assert.assertTrue(false);
            failed.set(true);
          } finally {
            counter.countDown();
          }
          return response;
        });
      }
      counter.await();
      Assert.assertFalse(failed.get());
      
    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
      Assert.assertTrue(false);
    }
  }
  
  
  @Test  
  public void testEmptyTextASync() throws InvalidKeyException, InterruptedException {
    final CountDownLatch counter = new CountDownLatch(1);
    DisambiguateRequest disReq = new DisambiguateRequest();
    disReq.setText("", "text/plain", StandardCharsets.UTF_8);
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getDisambiguateApiUrl())) {
      client.disambiguateAsync(disReq).handle((disResp, ex) -> {
        Assert.assertNotNull(ex);
        counter.countDown();
        return null;
      });
    } finally {
    }
    counter.await();
  }
  

}
