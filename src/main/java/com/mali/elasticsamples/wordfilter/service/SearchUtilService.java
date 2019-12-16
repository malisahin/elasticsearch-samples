package com.mali.elasticsamples.wordfilter.service;

import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sahin
 * @since 16.12.2019
 */

@Service
public class SearchUtilService {

  private final ElasticsearchTemplate elasticsearchTemplate;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public SearchUtilService(ElasticsearchTemplate elasticsearchTemplate) {
    this.elasticsearchTemplate = elasticsearchTemplate;
  }


  public <T> List<T> queryForList(QueryBuilder queryBuilder, Class<T> instance) {
    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
    logger.info(searchQuery.getQuery().toString());
    return elasticsearchTemplate.queryForList(searchQuery, instance);
  }
}
