package com.idilia.services.kb;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;
import com.idilia.services.text.DisambiguateRequest;
import com.idilia.services.text.DisambiguateResponse;

public class SenseMenuTest extends TestBase {
  protected URL getSvcUrl() {
    return Configuration.INSTANCE.getKbApiUrl();
  }
  
  @Test
  public void testSenseMenuFromText() throws Exception {
    SenseMenuRequest menuReq = new SenseMenuRequest();
    menuReq.setText("dog");
    menuReq.setRequestId("textTest");
    menuReq.setTemplate(menuTmplt);
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
    
      SenseMenuResponse menuResp = client.senseMenu(menuReq);
      Assert.assertEquals(HttpURLConnection.HTTP_OK, menuResp.getStatus());
      Assert.assertTrue(menuResp.getErrorMsg() == null);
      Assert.assertEquals("textTest", menuResp.getRequestId());
      Assert.assertNotNull(menuResp.menu);
    } finally {
    }
  }
  
  @Test
  public void testTaggingMenuFromTF() throws Exception {
    // First we need to obtain a TF
    DisambiguateResponse response = null;
    {
      DisambiguateRequest req = new DisambiguateRequest();
      req.setText("light bulb", "text/plain", StandardCharsets.UTF_8);
      req.setResultMime("application/x-tf+xml+gz");
      com.idilia.services.text.Client client = 
          new com.idilia.services.text.Client(
              getDefaultCreds(), Configuration.INSTANCE.getDisambiguateApiUrl());
      response = client.disambiguate(req);
      Assert.assertTrue(response.getErrorMsg() == null);
      client.close();
    }
    
    // Now we can obtain the menu
    TaggingMenuRequest menuReq = new TaggingMenuRequest();
    menuReq.setTf(response.getResult());
    menuReq.setRequestId("tfTest");
    menuReq.setTemplate(menuTmplt);
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      
      TaggingMenuResponse menuResp = client.taggingMenu(menuReq);
      Assert.assertEquals(HttpURLConnection.HTTP_OK, menuResp.getStatus());
      Assert.assertTrue(menuResp.getErrorMsg() == null);
      Assert.assertEquals("tfTest", menuResp.getRequestId());
      Assert.assertNotNull(menuResp.menu);
      Assert.assertNotNull(menuResp.text);
    } finally {
    }
  }

  static final String menuTmplt = "image_v3";
}
