/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * Simple command-line based search demo.
 */
@Slf4j
public class FileSearcher {

    private FileSearcher() {
    }

    /**
     * Searches for a query in a given index.
     *
     * @param index       The path to the index directory.
     * @param field       The field to search in.
     * @param queryString The query string to search for.
     * @param hitsPerPage The number of hits to display per page.
     * @param raw         A boolean flag to indicate whether to display raw output.
     * @throws Exception If there is an error during the search.
     */
    public static void search(String index, String field, String queryString, int hitsPerPage, boolean raw) throws Exception {
        try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            QueryParser parser = new QueryParser(field, analyzer);
            Query query = parser.parse(queryString);
            log.info("Searching for: {}", query.toString(field));

            doPagingSearch(new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)), searcher, query, hitsPerPage, raw, false);
        }
    }

    /**
     * This demonstrates a typical paging search scenario, where the user clicks to the next page if
     * interested in the next hits.
     *
     * <p>When the query is executed for the first time, then only enough results are collected to
     * fill 5 pages. If the user clicks to the next page, a new search is performed and results are
     * collected starting from the previous page. This prevents storing millions of hits in memory.
     */
    public static void doPagingSearch(
            BufferedReader in,
            IndexSearcher searcher,
            Query query,
            int hitsPerPage,
            boolean raw,
            boolean interactive)
            throws IOException {

        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = Math.toIntExact(results.totalHits.value);
        log.info("{} total matching documents", numTotalHits);

        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);

        while (true) {
            if (end > hits.length) {
                log.info("Only results 1 - {} of {} total matching documents collected.", hits.length, numTotalHits);
                log.info("Collect more (y/n) ?");
                String line = in.readLine();
                if (line.isEmpty() || line.charAt(0) == 'n') {
                    break;
                }

                hits = searcher.search(query, numTotalHits).scoreDocs;
            }

            end = Math.min(hits.length, start + hitsPerPage);

            for (int i = start; i < end; i++) {
                if (raw) { // output raw format
                    log.info("doc={} score={}", hits[i].doc, hits[i].score);
                    continue;
                }

                Document doc = searcher.doc(hits[i].doc);
                String path = doc.get("path");
                if (path != null) {
                    log.info("{}. {}", i + 1, path);
                    String title = doc.get("title");
                    if (title != null) {
                        log.info("   Title: {}", doc.get("title"));
                    }
                } else {
                    log.info("{}. No path for this document", i + 1);
                }
            }

            if (!interactive || end == 0) {
                break;
            }

            if (numTotalHits >= end) {
                boolean quit = false;
                while (true) {
                    log.info("Press ");
                    if (start - hitsPerPage >= 0) {
                        log.info("(p)revious page, ");
                    }
                    if (start + hitsPerPage < numTotalHits) {
                        log.info("(n)ext page, ");
                    }
                    log.info("(q)uit or enter number to jump to a page.");

                    String line = in.readLine();
                    if (line.length() == 0 || line.charAt(0) == 'q') {
                        quit = true;
                        break;
                    }
                    if (line.charAt(0) == 'p') {
                        start = Math.max(0, start - hitsPerPage);
                        break;
                    } else if (line.charAt(0) == 'n') {
                        if (start + hitsPerPage < numTotalHits) {
                            start += hitsPerPage;
                        }
                        break;
                    } else {
                        int page = Integer.parseInt(line);
                        if ((page - 1) * hitsPerPage < numTotalHits) {
                            start = (page - 1) * hitsPerPage;
                            break;
                        } else {
                            //log.info("No such page");
                        }
                    }
                }
                if (quit) break;
                end = Math.min(numTotalHits, start + hitsPerPage);
            }
        }
    }
}
