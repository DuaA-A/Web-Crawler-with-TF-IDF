package com.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
        public static void main(String[] args) throws IOException {
                WebCrawler crawler = new WebCrawler();
                String[] seedUrls = {
                "https://en.wikipedia.org/wiki/List_of_pharaohs",
                "https://en.wikipedia.org/wiki/Pharaoh"
                };
                crawler.crawlAndStore(seedUrls);
                InvertedIndex invertedIndex = new InvertedIndex();
                //print inverted index
                invertedIndex.BuildInvertedIndx("pages");
                //invertedIndex.printIndex();
                // Accept user query
                Scanner scanner = new Scanner(System.in);

                QueryProcessor queryProcessor = new QueryProcessor();
                TFIDFCalculator tfidf = new TFIDFCalculator(invertedIndex);

                while (true) {
                System.out.print("Enter search query (e.g., 'egypt deshret tomb') or 'e' to exit: ");
                String query = scanner.nextLine();
                if (query.equalsIgnoreCase("e")) {
                        System.out.println("Exiting the program.");
                        break;
                }

                System.out.println(invertedIndex.Print_DF("wikidata"));
                List<String> processedQuery = queryProcessor.processQuery(query);
                String expandedQuery = String.join(" ", processedQuery);
                //print query processing
                //        queryProcessor.printProcessedQuery(query);
                //         those functions are supposed to be used in cosine similarity

                // Print TF-IDF weights for a specific document
                //        String docId = "en_wikipedia_org_wiki_Pharaoh.txt";
                //        Map<String, Double> docVector = tfidf.computeDocumentVector(docId);
                //        System.out.println("TF-IDF for document: " + docId);
                //        docVector.forEach((term, weight) -> System.out.printf("%s: %.4f\n", term, weight));

                // Print TF-IDF for a user query
                //        Map<String, Double> queryVector = tfidf.computeQueryVector("egypt pharaoh tomb");
                //        System.out.println("\nTF-IDF for query:");
                //        queryVector.forEach((term, weight) -> System.out.printf("%s: %.4f\n", term, weight));
                //

                //3. Define the user's search query
                //String query = "egypt pharaoh tomb";
                //print the query after text processing
                System.out.println("Tokenized query:");
                //        TextProcessing.printTokens(query);

                // 4. Rank the documents based on cosine similarity
                List<String> topDocuments = tfidf.rankDocuments(query);

                // 5. Print the top matching documents
                System.out.println("Top 10 similar documents to the query: \"" + query + "\"");
                for (int i = 0; i < topDocuments.size(); i++) {
                        System.out.println((i + 1) + ". " + topDocuments.get(i));
                }

                // 6. Print cosine similarity values for top documents
                Map<String, Double> fullQueryVector = tfidf.computeQueryVector(query);
                Map<String, Double> similarities = new HashMap<>();
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

                scanner.close();
        }
}
