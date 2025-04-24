package com.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        WebCrawler crawler = new WebCrawler(10);
        crawler.crawl("https://en.wikipedia.org/wiki/Pharaoh", 2);
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.BuildInvertedIndx("pages");
       invertedIndex.printIndex();

        System.out.println(invertedIndex.Print_DF("wikidata") );


//         those functions are supposed to be used in cosine similarity

        TFIDFCalculator tfidf = new TFIDFCalculator(invertedIndex);

// Print TF-IDF weights for a specific document
        String docId = "en_wikipedia_org_wiki_Pharaoh.txt";
        Map<String, Double> docVector = tfidf.computeDocumentVector(docId);
        System.out.println("TF-IDF for document: " + docId);
        docVector.forEach((term, weight) -> System.out.printf("%s: %.4f\n", term, weight));

// Print TF-IDF for a user query
        Map<String, Double> queryVector = tfidf.computeQueryVector("egypt pharaoh tomb");
        System.out.println("\nTF-IDF for query:");
        queryVector.forEach((term, weight) -> System.out.printf("%s: %.4f\n", term, weight));



//         3. Define the user's search query
String query = "egypt pharaoh tomb";

// 4. Rank the documents based on cosine similarity
List<String> topDocuments = tfidf.rankDocuments(query);

// 5. Print the top matching documents
System.out.println("Top 10 similar documents to the query: \"" + query + "\"");
for (int i = 0; i < topDocuments.size(); i++) {
    System.out.println((i + 1) + ". " + topDocuments.get(i));
}

        // 6. Print cosine similarity values for top documents
Map<String, Double> fullQueryVector = tfidf.computeQueryVector(query);
Map<String, Double> similarities = new java.util.HashMap<>();
for (String id : tfidf.getAllDocumentIds()) {
    Map<String, Double> fullDocVector = tfidf.computeDocumentVector(id);
    double sim = tfidf.cosineSimilarity(fullDocVector, fullQueryVector);
    similarities.put(id, sim);
}

System.out.println("\nTop 10 similar documents with cosine similarity:");
topDocuments.stream()
        .limit(10)
        .forEach(doc -> {
            double simValue = similarities.getOrDefault(doc, 0.0);
            System.out.printf("- %s: Cosine Similarity = %.4f\n", doc, simValue);
        });

    }
}
