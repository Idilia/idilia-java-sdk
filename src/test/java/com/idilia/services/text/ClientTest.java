package com.idilia.services.text;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;

public class ClientTest extends TestBase {

  @Test
  public void testMatch() {
    // Perform a REST request to the paraphrase server
    try (Client client = new Client(getDefaultCreds(), Configuration.INSTANCE.getMatchApiUrl())) {
      MatchRequest req = new MatchRequest();
      req.setText("I like my Tide detergent!", "text/tweet", StandardCharsets.UTF_8);
      req.setRequestId("testing-services-match-" + (new Random()).nextInt(1000));
      req.setWsdMime("application/x-semdoc+xml");
      req.setFilter("[{\"fsk\":\"Tide/N8\",\"keywords\":{\"positive\":[\"detergent\"],\"negative\":[\"crimson\"]}},{\"fsk\":\"like/V3\",\"keywords\":{\"negative\":[\"is like\"]}}]");

      MatchResponse response = client.match(req);
      Assert.assertTrue(response.getErrorMsg() == null);
      Assert.assertTrue(response.result.match == true);
    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
    }
  }

  @Test
  public void testParaphrase() {
    // Perform a REST request to the paraphrase server
    try (Client client = new Client(getDefaultCreds(), Configuration.INSTANCE.getParaphraseApiUrl())) {
      ParaphraseRequest req = new ParaphraseRequest();
      req.setText("testing query", "text/query", StandardCharsets.UTF_8);
      req.setRequestId("testing-services-para-" + (new Random()).nextInt(1000));
      req.setWsdMime("application/x-semdoc+xml");

      ParaphraseResponse response = client.paraphrase(req);
      Assert.assertTrue(response.getErrorMsg() == null);
      Assert.assertTrue(response.getParaphrases().size() > 1);
      Assert.assertFalse(response.getParaphrases().get(0).getSenses().isEmpty());
      Assert.assertEquals(0, response.getParaphrases().get(0).getSenses().get(0).getStart());      
      Assert.assertTrue(response.getParaphrases().get(0).getSenses().get(0).getEnd() > 0);
      Assert.assertFalse(response.getParaphrases().get(0).getSenses().get(0).getFsk().isEmpty());
      InputStream is = response.getWsdResult().getInputStream();
     
      Scanner scanner = new Scanner(is);
      String theResult = scanner.useDelimiter("\\A").next();
      scanner.close();
      Assert.assertTrue(theResult.indexOf("query/N1")>0);
      
    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
    }
  }
  
  
  @Test
  public void testDisambiguate() {
    // Perform a REST request to the documentation server
    try (Client client = new Client(getDefaultCreds(), Configuration.INSTANCE.getDisambiguateApiUrl())) {
      DisambiguateRequest req = new DisambiguateRequest();
      req.setText("my testing query", "text/query", StandardCharsets.UTF_8);
      req.setRequestId("testing-services-doc-" + (new Random()).nextInt(1000));
      req.setMaxTokens(200);

      DisambiguateResponse response = client.disambiguate(req);
      Assert.assertTrue(response.getErrorMsg() == null);
      
      InputStream is = response.getResult().getInputStream();
      Scanner scanner = new Scanner(is);
      String theResult = scanner.useDelimiter("\\A").next();
      scanner.close();
      Assert.assertTrue(theResult.indexOf("query/N1")>0);
      
    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
      Assert.assertTrue(false);
    }
  }
  
  @Test
  public void testDisambiguateMultiple() {
    // Perform a REST request to the documentation server but sending multiple documents
    try (Client client = new Client(getDefaultCreds(), Configuration.INSTANCE.getDisambiguateApiUrl())) {
      DisambiguateRequest req = new DisambiguateRequest();
      String texts[] = {"cat", "dog" };
      for (int i = 0; i < texts.length; ++i )
        req.setText(texts[i], "text/query", StandardCharsets.UTF_8);
      req.setRequestId("testing-services-doc-" + (new Random()).nextInt(1000));
      req.setMaxTokens(200);

      DisambiguateResponse response = client.disambiguate(req);
      Assert.assertTrue(response.getErrorMsg() == null);
      Assert.assertEquals(2, response.getResults().size());
      for (int i = texts.length; --i >= 0; ) {
        InputStream is = response.getResults().get(i).getInputStream();
        Scanner scanner = new Scanner(is);
        String theResult = scanner.useDelimiter("\\A").next();
        scanner.close();
        Assert.assertTrue(theResult.indexOf(texts[i])>=0);
      }
    } catch (Exception e) {
      logger_.error("Got unexpected exception when running test", e);
      Assert.assertTrue(false);
    }
  }
  
}
