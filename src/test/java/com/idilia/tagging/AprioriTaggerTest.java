package com.idilia.tagging;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class AprioriTaggerTest {

  @Test
  public void testTag1() {
    AprioriTagger tagger = new AprioriTaggerBuilder(
        Collections.singletonList(new Sense(0, 1, "test", "test/N1"))).build();
    assertEquals("door", tagger.tag("door"));
    assertEquals("tests", tagger.tag("tests"));
    assertEquals("test-test-test", tagger.tag("test-test-test"));
    assertEquals("<span data-idl-fsk=\"test/N1\">test</span>", tagger.tag("test"));
    assertEquals("\"<span data-idl-fsk=\"test/N1\">test</span>\"", tagger.tag("\"test\""));
  }
  
  @Test
  public void testMultiple() {
    AprioriTagger tagger = new AprioriTaggerBuilder(
        Arrays.asList(
            new Sense(0, 1, "a", "a/N1"),
            new Sense(0, 1, "b", "b/N1"))).build();
    
    assertEquals("<span data-idl-fsk=\"a/N1\">a</span> <span data-idl-fsk=\"b/N1\">b</span>", tagger.tag("a b"));
    assertEquals("<span data-idl-fsk=\"b/N1\">b</span> <span data-idl-fsk=\"a/N1\">a</span>", tagger.tag("b a"));
    assertEquals("d <span data-idl-fsk=\"b/N1\">b</span> <span data-idl-fsk=\"a/N1\">a</span> c", tagger.tag("d b a c"));
  }
  
  @Test
  public void testGreedy() {
    AprioriTagger tagger = new AprioriTaggerBuilder(
        Arrays.asList(
            new Sense(0, 1, "a b", "a_b/N1"),
            new Sense(0, 1, "a", "a/N1"),
            new Sense(0, 1, "b", "b/N1"))).build();
    assertEquals("<span data-idl-fsk=\"a_b/N1\">a b</span>", tagger.tag("a b"));
  }
  
  @Test
  public void testEarlierPriority() {
    AprioriTagger tagger = new AprioriTaggerBuilder(
        Arrays.asList(
            new Sense(0, 1, "a", "a/N2"),
            new Sense(0, 1, "a", "a/N1"))).build();
    assertEquals("<span data-idl-fsk=\"a/N2\">a</span>", tagger.tag("a"));
  }
  
  @Test
  public void testCaseSensitivity() {
    
    // Test a case insensitive matcher
    AprioriTagger ciTagger = new AprioriTaggerBuilder(
        Collections.singletonList(new Sense(0, 1, "test", "test/N1"))).
        setCaseInsensitive(true).
        build();
    assertEquals("<span data-idl-fsk=\"test/N1\">Test</span>", ciTagger.tag("Test"));
    assertEquals("<span data-idl-fsk=\"test/N1\">test</span>", ciTagger.tag("test"));

    // Default tagger is case sensitive
    AprioriTagger tagger = new AprioriTaggerBuilder(
        Collections.singletonList(new Sense(0, 1, "test", "test/N1"))).build();
    assertEquals("Test", tagger.tag("Test"));
    assertEquals("<span data-idl-fsk=\"test/N1\">test</span>", tagger.tag("test"));
  }
  
  @Test
  public void testPluralization() {
    
    // Test that we tolerate plurals on a common word noun
    {
      AprioriTagger tagger = new AprioriTaggerBuilder(
          Arrays.asList(
              new Sense(0, 1, "test", "test/N1"),
              new Sense(0, 1, "guess", "guess/N1"))).
          setMatchesPluralNouns(true).
          build();
      
      assertEquals("<span data-idl-fsk=\"test/N1\">tests</span>", tagger.tag("tests"));
      assertEquals("<span data-idl-fsk=\"guess/N1\">guesses</span>", tagger.tag("guesses"));
    }
    
    // Test that we don't trigger on an NE
    {
      AprioriTagger tagger = new AprioriTaggerBuilder(
          Collections.singletonList(new Sense(0, 1, "Apple", "Apple/N8"))).
          setMatchesPluralNouns(true).
          build();
      
      assertEquals("apples", tagger.tag("apples"));
    }
    
    // Test that we don't trigger
  }
}
