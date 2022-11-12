package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

public class CrawlAction extends RecursiveAction {
    private final String url;
    private final Instant deadline;
    private final int maxDepth;
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final List<Pattern> ignoredUrls;
    private final ForkJoinPool pool;
    private static final Set<String> visitedUrls = new ConcurrentSkipListSet<>();
    private static final Map<String, Integer> counts = new ConcurrentHashMap<>();

    public CrawlAction (
            String url,
            int maxDepth,
            Instant deadline,
            Clock clock,
            PageParserFactory parserFactory,
            List<Pattern> ignoredUrls,
            ForkJoinPool pool) {
        this.url = url;
        this.maxDepth = maxDepth;
        this.deadline = deadline;
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.ignoredUrls = ignoredUrls;
        this.pool = pool;
    }

    public static Map<String, Integer> getCounts() {
        return counts;
    }
    public static void clearCounts() {
        counts.clear();
    }

    public static void clearUrlsVisited() {
        visitedUrls.clear();
    }

    public static int getUrlsVisited() {
        return visitedUrls.size();
    }

    @Override
    protected void compute() {
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }
        if (!visitedUrls.add(url)) {
            return;
        }
        visitedUrls.add(url);
        PageParser.Result result = parserFactory.get(url).parse();
        for (Map.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
            counts.compute(e.getKey(), (k, v) -> (v == null) ? e.getValue() : e.getValue() + v);
        }
        CrawlActionFactory crawlActionFactory = new CrawlActionFactoryImpl(
                maxDepth - 1, deadline, clock, parserFactory, ignoredUrls, pool);
        result.getLinks()
                .parallelStream()
                .map(crawlActionFactory::get)
                .forEach(pool::invoke);
    }

}
