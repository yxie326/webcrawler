package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

public class CrawlActionFactoryImpl implements CrawlActionFactory {
    private final int maxDepth;
    private final Instant deadline;
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final List<Pattern> ignoredUrls;
    private final ForkJoinPool pool;

    public CrawlActionFactoryImpl(
            int maxDepth,
            Instant deadline,
            Clock clock,
            PageParserFactory parserFactory,
            List<Pattern> ignoredUrls,
            ForkJoinPool pool) {
        this.maxDepth = maxDepth;
        this.deadline = deadline;
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.ignoredUrls = ignoredUrls;
        this.pool = pool;
    }
    @Override
    public CrawlAction get(String url) {
        return new CrawlAction(url, maxDepth, deadline, clock, parserFactory, ignoredUrls, pool);
    }
}
