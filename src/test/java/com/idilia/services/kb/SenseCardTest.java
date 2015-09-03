package com.idilia.services.kb;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;

public class SenseCardTest extends TestBase  {

  protected URL getSvcUrl() {
    return Configuration.INSTANCE.getKbApiUrl();
  }

  // Request the sensecard for a sensekey
  @Test
  public void testSenseCardForFsk() throws Exception {
    SenseCardRequest req = new SenseCardRequest("dog/N1");
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      String card = client.senseCard(req).card;
      Assert.assertNotNull(card);
    } finally {
    }
  }
  
  // Request the sensecard for "other" sense
  @Test
  public void testSenseCardForOther() throws Exception {
    SenseCardRequest req = new SenseCardRequest(SenseCardRequest.Special.other, "dog");
    req.setTemplate("image_v2");
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      String card = client.senseCard(req).card;
      Assert.assertNotNull(card);
    } finally {
    }
  }
  
  // Request the sensecard for "any" sense
  @Test
  public void testSenseCardForAny() throws Exception {
    SenseCardRequest req = new SenseCardRequest(SenseCardRequest.Special.any, "dog");
    req.setTemplate("image_v2");
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      String card = client.senseCard(req).card;
      Assert.assertNotNull(card);
    } finally {
    }
  }

}
