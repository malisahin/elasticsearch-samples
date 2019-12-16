package com.mali.elasticsamples.wordfilter.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

/**
 * @author sahin
 * @since 13.12.2019
 */

@Getter
@Setter
@NoArgsConstructor

@Document(indexName = "random", type = "sentence")
public class Sentence {

  @Id
  @Field(type = FieldType.String)
  private String id = UUID.randomUUID().toString();

  @Field(type = FieldType.String, analyzer = "turkish_analyzer")
  private String text;

  public Sentence(String text) {
    this.text = text;
  }

}
