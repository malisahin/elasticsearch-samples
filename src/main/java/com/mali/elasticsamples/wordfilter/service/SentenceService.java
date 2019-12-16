package com.mali.elasticsamples.wordfilter.service;

import com.mali.elasticsamples.wordfilter.dao.SentenceRepository;
import com.mali.elasticsamples.wordfilter.model.CustomFilter;
import com.mali.elasticsamples.wordfilter.model.Sentence;
import com.mali.elasticsamples.wordfilter.model.enums.SearchAnalyzer;
import com.mali.elasticsamples.wordfilter.model.enums.SearchType;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sahin
 * @since 13.12.2019
 */

@Service
public class SentenceService {

  private static List<Sentence> defaultSentenceList;
  private static List<CustomFilter> customFilters;
  private static String PHONE_NUMBER_PATTERN = "[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\.0-9]{1,10}\\s*\\D*";

  static {
    defaultSentenceList = new ArrayList<>();
    defaultSentenceList.add(new Sentence("Iyi Günler."));
    defaultSentenceList.add(new Sentence("GÖrüşmek üzere"));
    defaultSentenceList.add(new Sentence("Görüşmek üzere"));
    defaultSentenceList.add(new Sentence("İyi Çalışmalar."));
    defaultSentenceList.add(new Sentence("İyi Çalışmalar Dilerim"));
    defaultSentenceList.add(new Sentence("Telefon Numaram 5394000120"));
    defaultSentenceList.add(new Sentence("Telefon Numaram 539 400 0120"));
    defaultSentenceList.add(new Sentence("Telefon Numaram 539 400 01 20"));
    defaultSentenceList.add(new Sentence("İletişim adresim Bağlarbaşı"));
    defaultSentenceList.add(new Sentence("Beni 905324412525 no'lu numaradan arayabilirsiniz"));
    defaultSentenceList.add(new Sentence("Beni 90532 441 25 25 no'lu numaradan arayabilirsiniz"));
    defaultSentenceList.add(new Sentence("Email adresim mali@test.com "));
    defaultSentenceList.add(new Sentence("Bana ali@gmail.com adresinden ulaşabilirsiniz."));
    defaultSentenceList.add(new Sentence("Bana ali@hemenis.com adresinden ulaşabilirsiniz."));
    defaultSentenceList.add(new Sentence("Bana ali@hotmail.com adresinden ulaşabilirsiniz."));
    defaultSentenceList.add(new Sentence("Merhabalar"));
    defaultSentenceList.add(new Sentence("Bir iş fırsatı için aramıştım"));
    defaultSentenceList.add(new Sentence("İş fırsatlarını değerlendiriyor musunuz?"));
    defaultSentenceList.add(new Sentence("İş arıyor musunuz?"));
    defaultSentenceList.add(new Sentence("İş arıyor musunuz?"));
    defaultSentenceList.add(new Sentence("Hangi pozisyonları değerlendiriyorsunuz?"));
    defaultSentenceList.add(new Sentence("Hangi ali@testx.com değerlendiriyorsunuz?"));
    defaultSentenceList.add(new Sentence("Rastgele bir sayi 53840023424123123123 test"));
    defaultSentenceList.add(new Sentence("Rastgele bir sayi 5384002342 4123123123 test girmesi lazim"));

    customFilters = new ArrayList<>();
    customFilters.add(new CustomFilter("iyi günler", SearchType.SHOULD));
    customFilters.add(new CustomFilter("iyi çalışmalar", SearchType.SHOULD));
    customFilters.add(new CustomFilter("görüşmek üzere", SearchType.SHOULD));

    customFilters.add(CustomFilter.withFuzziness("@gmail.com", SearchType.SHOULD, Fuzziness.ZERO));
    customFilters.add(CustomFilter.withFuzziness("@hotmail.com", SearchType.SHOULD, Fuzziness.ZERO));
    customFilters.add(CustomFilter.withFuzziness("@hemenis.com", SearchType.SHOULD, Fuzziness.ZERO));
    customFilters.add(CustomFilter.withFuzziness("@test.com", SearchType.SHOULD, Fuzziness.ZERO));
    customFilters.add(CustomFilter.withRegex(PHONE_NUMBER_PATTERN));
  }

  private final SearchUtilService searchUtilService;
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final SentenceRepository sentenceRepository;

  public SentenceService(SearchUtilService searchUtilService,
                         SentenceRepository sentenceRepository) {
    this.searchUtilService = searchUtilService;
    this.sentenceRepository = sentenceRepository;
  }

  @Transactional
  public void addDefaultSampleMessages() {
    this.sentenceRepository.deleteAll();
    this.sentenceRepository.save(defaultSentenceList);
  }

  public List<Sentence> getSearchList() {

    final List<Sentence> sentenceList = new ArrayList<>();

    List<CustomFilter> shouldFilters = customFilters.stream()
        .filter(filter -> filter.getSearchType().equals(SearchType.SHOULD))
        .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(shouldFilters))
      sentenceList.addAll(this.shouldSearchList(shouldFilters));

    List<CustomFilter> mustFilter = customFilters.stream()
        .filter(filter -> filter.getSearchType().equals(SearchType.MUST))
        .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(mustFilter))
      sentenceList.addAll(this.mustSearchList(mustFilter));

    List<CustomFilter> regexFilters = customFilters.stream()
        .filter(filter -> filter.getSearchType().equals(SearchType.REGEX))
        .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(regexFilters))
      sentenceList.addAll(this.regexSearchList(regexFilters));

    return sentenceList;

  }

  private List<Sentence> regexSearchList(List<CustomFilter> regexFilters) {
    return regexFilters.stream()
        .map(this::regexSearch)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private List<Sentence> regexSearch(CustomFilter regexFilter) {
    final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    RegexpQueryBuilder phoneNumberRegex = QueryBuilders.regexpQuery("text", regexFilter.getRegexPattern());
    boolQueryBuilder.must(phoneNumberRegex);
    return searchUtilService.queryForList(boolQueryBuilder, Sentence.class);
  }

  private List<Sentence> mustSearchList(List<CustomFilter> mustFilters) {
    final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    mustFilters.forEach(filter -> {
      final MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("text", filter.getText());
      matchQueryBuilder.analyzer(SearchAnalyzer.TURKISH_ANALYZER.getType());
      boolQueryBuilder.must(matchQueryBuilder);
    });
    return searchUtilService.queryForList(boolQueryBuilder, Sentence.class);
  }

  private List<Sentence> shouldSearchList(List<CustomFilter> customFilterList) {
    final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    customFilterList.forEach(filter -> {
      final MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("text", filter.getText());

      if (filter.getFuzziness() != null)
        matchQueryBuilder.fuzziness(filter.getFuzziness());

      matchQueryBuilder.analyzer(SearchAnalyzer.TURKISH_ANALYZER.getType());
      boolQueryBuilder.should(matchQueryBuilder);
    });

    return searchUtilService.queryForList(boolQueryBuilder, Sentence.class);
  }

  public void deleteAllRecords() {
    this.sentenceRepository.deleteAll();
  }


}
