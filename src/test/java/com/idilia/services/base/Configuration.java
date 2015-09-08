/*
 * Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * 
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.idilia.services.base;

import java.net.URL;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * A simple class to manage loading the property file containing needed configuration data
 * from the package. Once loaded the configuration is held in memory as a singleton.  Since
 * we already require the simplejpa.properties file to support SimpleJPA, we use that
 * to store additional configuration values.
 */
public enum Configuration {

  INSTANCE;

  Configuration() {
    String file = new String("/services.properties");
    try {
      parms_.load(this.getClass().getResourceAsStream("/services.properties"));
      String defaultApiUrl = getProperty("apiUrl", "http://api.idilia.com");
      disambiguateApiUrl = new URL(getProperty("disambiguateApiUrl", defaultApiUrl));
      kbApiUrl = new URL(getProperty("kbApiUrl", defaultApiUrl));
      matchApiUrl = new URL(getProperty("matchApiUrl", defaultApiUrl));
      paraphraseApiUrl = new URL(getProperty("paraphraseApiUrl", defaultApiUrl));
      textIndexApiUrl = new URL(getProperty("textIndexApiUrl", defaultApiUrl));
      idiliaAccessKey = getProperty("idiliaAccessKey", null);
      idiliaSecretKey = getProperty("idiliaSecretKey", null);
      idiliaCredentials = new IdiliaCredentials(idiliaAccessKey, idiliaSecretKey);

    } catch (Exception e) {
      Logger.getRootLogger().error(
          String.format("Failed to initialize properties from file %s", file),
          e);
      throw new RuntimeException(file + ": " + e.getMessage());
    }
  }

  static public Configuration getInstance() {
    return INSTANCE;
  }

  public URL getKbApiUrl() {
    return kbApiUrl;
  }

  public URL getDisambiguateApiUrl() {
    return disambiguateApiUrl;
  }

  public URL getMatchApiUrl() {
    return matchApiUrl;
  }

  public URL getParaphraseApiUrl() {
    return paraphraseApiUrl;
  }

  public URL getTextIndexApiUrl() {
    return textIndexApiUrl;
  }

  public String getIdiliaAccessKey() {
    return idiliaAccessKey;
  }

  public String getIdiliaSecretKey() {
    return idiliaSecretKey;
  }

  public IdiliaCredentials getIdiliaCredentials() {
    return idiliaCredentials;
  }

  private URL kbApiUrl = null;
  private URL disambiguateApiUrl = null;
  private URL matchApiUrl = null;
  private URL paraphraseApiUrl = null;
  private URL textIndexApiUrl = null;
  private String idiliaAccessKey = null;
  private String idiliaSecretKey = null;
  private IdiliaCredentials idiliaCredentials = null;

  final private Properties parms_ = new Properties();

  private String getProperty(String name, String defaultValue)
  {
    String valueS = null;

    // Check if overwritten by the properties
    Object valueO = parms_.get(name);
    if (valueO != null) {
      if (valueO instanceof String)
        valueS = (String) valueO;
    }

    // Check if we can retrieve a value from the context
    try {
      Context env = (Context) new InitialContext().lookup("java:comp/env");
      valueS = (String) env.lookup(name);
    } catch (NamingException e) {
    }

    // If unknown, try from the environment
    if (valueS == null)
      valueS = System.getProperty(name);

    if (valueS == null)
      valueS = defaultValue;

    if (valueS == null)
      Logger.getRootLogger().error(String.format("Failed to obtain a value for parameter %s", name));
    else
      Logger.getRootLogger().info(String.format("Parameter '%s' is set to value '%s'", name, valueS));

    return valueS;
  }

}
