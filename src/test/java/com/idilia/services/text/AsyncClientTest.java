package com.idilia.services.text;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;
import com.idilia.tagging.Sense;

public class AsyncClientTest extends TestBase {

  
  @Test
  public void testMatch() throws InterruptedException {
    // Perform a REST request to the paraphrase server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getMatchApiUrl())) {
      MatchRequest req = new MatchRequest();
      req.setText("I like my Tide detergent!", "text/tweet", StandardCharsets.UTF_8);
      req.setRequestId("testing-services-match-" + (new Random()).nextInt(1000));
      req.setWsdMime("application/x-semdoc+xml");
      req.setFilter("[{\"fsk\":\"Tide/N8\",\"keywords\":{\"positive\":[\"detergent\"],\"negative\":[\"crimson\"]}},{\"fsk\":\"like/V3\",\"keywords\":{\"negative\":[\"is like\"]}}]");

      MatchResponse response = client.matchAsync(req).join();
      Assert.assertTrue(response.result.match);
    } finally {
    }
  }

  @Test
  public void testMatchingEval() throws InterruptedException {
    // Perform a REST request to the paraphrase server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getMatchApiUrl())) {
      
      {
        MatchingEvalRequest req = new MatchingEvalRequest();
        req.setDocuments(Arrays.asList("I love to do the laundry with tide.", "tide", "high", "detergent", "The tide was high when he was at the beach"));
        req.setRequestId("testing-services-match-" + (new Random()).nextInt(1000));
        req.setExpression(Collections.singletonList(new Sense(0, 1, "tide", "Tide/N8")));

        MatchingEvalResponse response = client.matchingEvalAsync(req).join();
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(5, response.getResult().size());
        Assert.assertTrue(response.getResult().get(0) > 0);
        Assert.assertTrue(response.getResult().get(1) == 0);
        Assert.assertTrue(response.getResult().get(2) < 0);
        Assert.assertTrue(response.getResult().get(3) < 0);
        Assert.assertTrue(response.getResult().get(4) < 0);
      }

      {
        MatchingEvalRequest req = new MatchingEvalRequest();
        req.setDocuments(Arrays.asList("I love to do the laundry with tide.", "tide", "high", "detergent", "The tide was high when he was at the beach"));
        req.setRequestId("testing-services-match-" + (new Random()).nextInt(1000));
        req.setExpression(Collections.singletonList(new Sense(0, 1, "tide", "Tide/N8")));
        req.setRequireTerm(MatchingEvalRequest.RequireTerm.no);
        
        MatchingEvalResponse response = client.matchingEvalAsync(req).join();
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(5, response.getResult().size());
        Assert.assertTrue(response.getResult().get(0) > 0);
        Assert.assertTrue(response.getResult().get(1) == 0);
        Assert.assertTrue(response.getResult().get(2) < 0);
        Assert.assertTrue(response.getResult().get(3) > 0);
        Assert.assertTrue(response.getResult().get(4) < 0);
      }
    } finally {
    }
  }

  @Test
  public void testParaphrase() throws InterruptedException, IOException {
    // Perform a REST request to the paraphrase server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getParaphraseApiUrl())) {
      ParaphraseRequest req = new ParaphraseRequest();
      req.setText("my testing query", "text/query", StandardCharsets.UTF_8);
      req.setRequestId("testing-services-para-" + (new Random()).nextInt(1000));
      req.setWsdMime("application/x-semdoc+xml");

      ParaphraseResponse response = client.paraphraseAsync(req).join();
      Assert.assertFalse(response.getParaphrases().size() <= 1);
      InputStream is = response.getWsdResult().getInputStream();
      Scanner scanner = new Scanner(is);
      String theResult = scanner.useDelimiter("\\A").next();
      scanner.close();
      Assert.assertTrue(theResult.indexOf("query/N1") >= 0);
    } finally {
    }
  }
  
  
  @Test
  public void testDisambiguate() {
    // Perform a REST request to the documentation server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getDisambiguateApiUrl())) {

      DisambiguateRequest req = new DisambiguateRequest();
      req.setText("my testing query", "text/query", StandardCharsets.UTF_8);
      DisambiguateResponse r = client.disambiguateAsync(req).join();
      Assert.assertEquals(HttpStatus.SC_OK, r.getStatus());
    } finally {
    }
  }
  
  
  @Test  
  public void testEmptyTextASync() throws InterruptedException {
    final CountDownLatch counter = new CountDownLatch(1);
    DisambiguateRequest disReq = new DisambiguateRequest();
    disReq.setText("", "text/plain", StandardCharsets.UTF_8);
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), Configuration.INSTANCE.getDisambiguateApiUrl())) {
      client.disambiguateAsync(disReq).whenComplete((disResp, ex) -> {
        Assert.assertNotNull(ex);
        counter.countDown();
      });
    } finally {
    }
    counter.await();
  }
  

}
