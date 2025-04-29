// package com.example;

// import java.io.*;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.Queue;

// public class crawlAndIndexd{
//      public static void crawlAndIndex(String[] seedUrls) throws IOException {
//     // Queue for managing URLs to crawl
//     Queue<String> crawlQueue = new LinkedList<>();
//     int docId = 0; // Document ID counter

//     // Add seed URLs to the queue
//     for (String url : seedUrls) {
//         crawlQueue.add(url);
//     }

//     // Continue crawling until the queue is empty or max documents are reached
//     while (!crawlQueue.isEmpty() && sources.size() < MAX_DOCS) {
//         String url = crawlQueue.poll(); // Get the next URL
//         // Skip if URL was visited or not a Wikipedia page
//         if (visitedUrls.contains(url) || !url.startsWith(WIKI_BASE)) {
//             continue;
//         }

//         try {
//             // Fetch the page using Jsoup
//             Document doc = Jsoup.connect(url).get();
//             visitedUrls.add(url); // Mark URL as visited

//             // Extract the page title, removing Wikipedia suffix
//             String title = doc.title().replace(" - Wikipedia", "");

//             // Extract text from the main content area
//             Element content = doc.selectFirst("#mw-content-text");
//             if (content == null) {
//                 System.out.println("No content found for URL: " + url);
//                 continue;
//             }
//             String text = content.text();

//             // Store document metadata
//             SourceRecord source = new SourceRecord(docId, url, title, text);
//             sources.put(docId, source);

//             // Index the document's text content
//             int docLength = indexText(text, docId);
//             sources.get(docId).length = docLength;

//             // Extract links for further crawling
//             Elements links = content.select("a[href]");
//             for (Element link : links) {
//                 String nextUrl = link.absUrl("href");
//                 // Filter links: must be Wikipedia, unvisited, and not a special page
//                 if (nextUrl.startsWith(WIKI_BASE) &&
//                     !visitedUrls.contains(nextUrl) &&
//                     !nextUrl.contains("#") &&
//                     !nextUrl.contains("Special:") &&
//                     !nextUrl.contains("File:") &&
//                     !nextUrl.contains("Category:") &&
//                     !nextUrl.contains("Template:")) {
//                     crawlQueue.add(nextUrl);
//                 }
//             }

//             docId++; // Increment document ID
//             // Delay to avoid overloading the server
//             Thread.sleep(1000);

//         } catch (IOException e) {
//             System.out.println("Error crawling URL " + url + ": " + e.getMessage());
//         } catch (InterruptedException e) {
//             System.out.println("Crawling interrupted: " + e.getMessage());
//         }
//     }

//     // Update total number of documents
//     setN(sources.size());

//     // Abort if no documents were indexed
//     if (sources.isEmpty()) {
//         throw new IOException("Failed to index any documents. Crawling aborted.");
//     }

//     // Build vocabulary and TF-IDF vectors
//     buildVocabularyAndVectors();
// }
// }