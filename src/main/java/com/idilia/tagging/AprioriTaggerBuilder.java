package com.idilia.tagging;

/**
 * Builder for an AprioriTagger.
 */
public class AprioriTaggerBuilder {

  /* The parameters used when creating the AprioriTagger */
  final private Iterable<Sense> senses;
  private boolean caseInsensitive = false;
  private boolean acceptPlurals = false;
  
  
  /**
   * Create a builder with the one mandatory parameter needed to
   * build the AprioriTagger.
   * @param senses list of senses to find and tag
   */
  public AprioriTaggerBuilder(Iterable<Sense> senses) {
    this.senses = senses;
  }
  
  /**
   * Make the tagger case insensitive when matching surfaces. Default
   * is case sensitive.
   *
   * @param ci true for enabling case insensitive matching
   * @return self
   */
  public AprioriTaggerBuilder setCaseInsensitive(boolean ci) {
    this.caseInsensitive = ci;
    return this;
  }
  
  /**
   * Make the tagger attempt to match pluralized noun forms for the
   * noun common word senses provided. The pluralization rule is very approximate
   * and does not handle exceptions.
   * @param v true for enabling plural noun matching
   * @return
   */
  public AprioriTaggerBuilder setMatchesPluralNouns(boolean v) {
    this.acceptPlurals = v;
    return this;
  }
  
  /**
   * Build an AprioriTagger with the configuration recorded.
   * @return configured AprioriTagger
   */
  public AprioriTagger build() {
    return new AprioriTagger(
        senses,
        caseInsensitive,
        acceptPlurals);
  }
}
