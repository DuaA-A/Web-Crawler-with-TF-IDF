package com.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        WebCrawler crawler = new WebCrawler(10);
        crawler.crawl("https://en.wikipedia.org/wiki/Pharaoh", 2);
    }
}