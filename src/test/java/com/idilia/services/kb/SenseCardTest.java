package com.idilia.services.kb;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;

public class SenseCardTest extends TestBase  {

  static final String cardTmplt = "image_v2";
  
  protected URL getSvcUrl() {
    return Configuration.INSTANCE.getKbApiUrl();
  }

  // Request the sensecard for a normal sensekey
  @Test
  public void testSenseCardForFsk() throws Exception {
    SenseCardRequest req = new SenseCardRequest("dog/N1").setTemplate(cardTmplt);
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      String card = client.senseCard(req).card;
      Assert.assertNotNull(card);
    } finally {
    }
  }
  
  // Request the sensecard for "inapplicable" sense
  @Test
  public void testSenseCardForIna() throws Exception {
    SenseCardRequest req = new SenseCardRequest("for/_INA_").setTemplate(cardTmplt);
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      String card = client.senseCard(req).card;
      Assert.assertNotNull(card);
    } finally {
    }
  }
  
  // Request the sensecard for "other" sense
  @Test
  public void testSenseCardForOther() throws Exception {
    SenseCardRequest req = new SenseCardRequest("dog/_UNK_").setTemplate(cardTmplt);
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      String card = client.senseCard(req).card;
      Assert.assertNotNull(card);
    } finally {
    }
  }
  
  // Request the sensecard for "any" sense
  @Test
  public void testSenseCardForAny() throws Exception {
    SenseCardRequest req = new SenseCardRequest("dog/_WC_").setTemplate(cardTmplt);
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      String card = client.senseCard(req).card;
      Assert.assertNotNull(card);
    } finally {
    }
  }

}
