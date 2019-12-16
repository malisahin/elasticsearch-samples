package com.mali.elasticsamples.wordfilter.dao;

import com.mali.elasticsamples.wordfilter.model.Sentence;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author sahin
 * @since 13.12.2019
 */

@Repository
public interface SentenceRepository extends ElasticsearchRepository<Sentence, String> {

}
