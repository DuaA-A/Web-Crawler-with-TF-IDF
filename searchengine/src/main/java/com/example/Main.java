package com.example;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        WebCrawler crawler = new WebCrawler(10);
//        crawler.crawl("https://en.wikipedia.org/wiki/Pharaoh", 2);
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.BuildInvertedIndx("pages");
     //  invertedIndex.printIndex();

     //   System.out.println(invertedIndex.Print_DF("wikidata") );


        // those functions are supposed to be used in cosine similarity

//        TFIDFCalculator tfidf = new TFIDFCalculator(invertedIndex);
//
//// Print TF-IDF weights for a specific document
//        String docId = "en_wikipedia_org_wiki_Pharaoh.txt";
//        Map<String, Double> docVector = tfidf.computeDocumentVector(docId);
//        System.out.println("TF-IDF for document: " + docId);
//        docVector.forEach((term, weight) -> System.out.printf("%s: %.4f\n", term, weight));
//
//// Print TF-IDF for a user query
//        Map<String, Double> queryVector = tfidf.computeQueryVector("egypt pharaoh tomb");
//        System.out.println("\nTF-IDF for query:");
//        queryVector.forEach((term, weight) -> System.out.printf("%s: %.4f\n", term, weight));

    }
}