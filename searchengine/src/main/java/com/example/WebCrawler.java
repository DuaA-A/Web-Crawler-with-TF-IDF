package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
    private static final String WIKI_BASE = "https://en.wikipedia.org/wiki/";
    private static final int MAX_DOCS = 10;
    private HashSet<String> visitedUrls = new HashSet<>();
    private Queue<String> crawlQueue = new LinkedList<>();
    private int docId = 0;

    public void crawlAndStore(String[] seedUrls) throws IOException {
        // Add seed URLs to the crawl queue
        for (String url : seedUrls) {
            crawlQueue.add(url);
        }

        // Crawl and store documents until the queue is empty or max docs are reached
        while (!crawlQueue.isEmpty() && docId < MAX_DOCS) {
            String url = crawlQueue.poll(); // Get the next URL
            // Skip if URL was visited or not a Wikipedia page
            if (visitedUrls.contains(url) || !url.startsWith(WIKI_BASE)) {
                continue;
            }

            try {
                // Fetch the page using Jsoup
                Document doc = Jsoup.connect(url).get();
                visitedUrls.add(url); // Mark URL as visited

                // Extract the page title, removing the Wikipedia suffix
                String title = doc.title().replace(" - Wikipedia", "");

                // Extract text from the main content area
                Element content = doc.selectFirst("#mw-content-text");
                if (content == null) {
                    System.out.println("No content found for URL: " + url);
                    continue;
                }
                String text = content.text();

                String fileName = url.replaceAll("https?://", "")
                                    .replaceAll("[^a-zA-Z0-9]", "_") + ".txt";
                File dir = new File("pages");
                if (!dir.exists()) {
                    dir.mkdirs(); 
                }
                File file = new File(dir, fileName);
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write("URL: " + url + "\n\n");
                    fw.write(text);
                }
                System.out.println("Saved: " + file.getAbsolutePath());
                Elements links = content.select("a[href]");
                for (Element link : links) {
                    String nextUrl = link.absUrl("href");
                    if (nextUrl.startsWith(WIKI_BASE) &&
                        !visitedUrls.contains(nextUrl) &&
                        !nextUrl.contains("#") &&
                        !nextUrl.contains("Special:") &&
                        !nextUrl.contains("File:") &&
                        !nextUrl.contains("Category:") &&
                        !nextUrl.contains("Template:")) {
                        crawlQueue.add(nextUrl); 
                    }
                }

                docId++;
                Thread.sleep(1000);

            } catch (IOException e) {
                System.out.println("Error crawling URL " + url + ": " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Crawling interrupted: " + e.getMessage());
            }
        }
    }

    // public static void main(String[] args) throws IOException {
    //     WebCrawler crawler = new WebCrawler();
    //     String[] seedUrls = {
    //         "https://en.wikipedia.org/wiki/List_of_pharaohs",
    //         "https://en.wikipedia.org/wiki/Pharaoh"
    //     };
    //     crawler.crawlAndStore(seedUrls);
    // }
}