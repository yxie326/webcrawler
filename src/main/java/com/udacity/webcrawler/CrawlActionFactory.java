package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser;

import java.util.Set;

/**
 * A factory interface that supplies instances of {@link CrawlAction} that have common parameters
 * (such as the timeout and ignored words) preset from injected values.
 */
public interface CrawlActionFactory {

    /**
     * Returns a {@link PageParser} that parses the given {@link url}.
     */
    CrawlAction get(String url);

}
