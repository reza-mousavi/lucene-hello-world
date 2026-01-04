package com.reza.learning.service;

import com.reza.learning.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.demo.FileSearcher;
import org.springframework.stereotype.Component;
import org.apache.lucene.demo.FileIndexer;

@Slf4j
@Component
@RequiredArgsConstructor
public class LuceneService {

    private final AppProperties appProperties;

    public void index() {
        var lucene = appProperties.lucene();
        log.info("Creating index at {}", lucene.indexPath());
        log.info("Indexing docs at {}", lucene.docsPath());
        log.info("Create flag is {}", lucene.create());

        FileIndexer.index(lucene.docsPath(),
                lucene.indexPath(),
                lucene.create());
    }

    public void query(String query) throws Exception {
        var lucene = appProperties.lucene();

        log.info("Using index as {}", lucene.indexPath());
        log.info("Using field as {}", lucene.field());
        log.info("Create flag is {}", lucene.create());
        FileSearcher.search(lucene.indexPath(),
                lucene.field(),
                query,
                lucene.paging(),
                lucene.raw());
    }
}

