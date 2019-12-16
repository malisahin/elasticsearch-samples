package com.mali.elasticsamples.wordfilter.model;

import com.mali.elasticsamples.wordfilter.model.enums.SearchAnalyzer;
import com.mali.elasticsamples.wordfilter.model.enums.SearchType;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.unit.Fuzziness;

/**
 * @author sahin
 * @since 16.12.2019
 */

@Getter
@Setter

public class CustomFilter {

  private String text;

  private SearchType searchType;

  private String regexPattern;

  private Fuzziness fuzziness;

  private SearchAnalyzer searchAnalyzer;

  private CustomFilter() {

  }

  public CustomFilter(String text, SearchType searchType) {
    this.text = text;
    this.searchType = searchType;
  }

  public static CustomFilter withFuzziness(String text, SearchType searchType, Fuzziness fuzziness) {
    final CustomFilter filter = new CustomFilter();
    filter.text = text;
    filter.searchType = searchType;
    filter.fuzziness = fuzziness;
    return filter;
  }

  public static CustomFilter withRegex(String regexPattern) {
    final CustomFilter filter = new CustomFilter();
    filter.setSearchType(SearchType.REGEX);
    filter.regexPattern = regexPattern;
    return filter;
  }
}
