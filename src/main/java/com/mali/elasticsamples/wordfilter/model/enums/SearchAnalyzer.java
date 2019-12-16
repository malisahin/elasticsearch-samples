package com.mali.elasticsamples.wordfilter.model.enums;

import org.apache.lucene.analysis.tr.TurkishAnalyzer;

/**
 * @author sahin
 * @since 13.12.2019
 */

public enum SearchAnalyzer {
  NGRAM("NGRAM"),
  TURKISH_ANALYZER("turkish_analyzer");

  private final String type;

  SearchAnalyzer(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
