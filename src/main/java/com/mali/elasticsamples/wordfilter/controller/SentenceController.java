package com.mali.elasticsamples.wordfilter.controller;

import com.mali.elasticsamples.wordfilter.model.Sentence;
import com.mali.elasticsamples.wordfilter.service.SentenceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sahin
 * @since 13.12.2019
 */

@RestController
@RequestMapping("/sentence")
public class SentenceController {

  private final SentenceService sentenceService;

  public SentenceController(SentenceService sentenceService) {
    this.sentenceService = sentenceService;
  }

  @PutMapping
  public void setDefaultMessages() {
    this.sentenceService.addDefaultSampleMessages();
  }

  @DeleteMapping
  public void deleteAllRecords() {
    this.sentenceService.deleteAllRecords();
  }

  @GetMapping
  public List<Sentence> getMessages() {
    return this.sentenceService.getSearchList();
  }
}
