package com.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TFIDFCalculator {
    public  InvertedIndex index;

    public TFIDFCalculator(InvertedIndex index1) {
        index = index1;
    }

    // tfidf vector for document

    public Map<String, Double> computeDocumentVector(String docId) {

        Map<String, Double> tfidfVector = new HashMap<>();
        int totalDocs = index.InvertedIndx.values().stream()
                .flatMap(List::stream)
                .map(posting -> posting.DocId)
                .collect(Collectors.toSet()).size();

        for (Map.Entry<String, List<InvertedIndex.Posting>> entry : index.InvertedIndx.entrySet()) {
            String term = entry.getKey();
            List<InvertedIndex.Posting> postings = entry.getValue();
            for (InvertedIndex.Posting p : postings) {
                if (p.DocId.equals(docId)) {
                    double tf = 1 + Math.log10(p.TF);
                    double df = postings.size();
                    double idf = Math.log10((double) totalDocs / df);
                    tfidfVector.put(term, tf * idf);
                    break;
                }
            }
        }
        return tfidfVector;
    }

    //  tfidf vector for  user query
    public Map<String, Double> computeQueryVector(String query) {
        String[] tokens = query.toLowerCase().split("\\W+");
        Map<String, Integer> tfMap = new HashMap<>();
        for (String token : tokens) {
            if (!token.isEmpty())
                tfMap.put(token, tfMap.getOrDefault(token, 0) + 1);
        }

        Map<String, Double> queryVector = new HashMap<>();
        int totalDocs = index.InvertedIndx.values().stream()
                .flatMap(List::stream)
                .map(posting -> posting.DocId)
                .collect(Collectors.toSet()).size();

        for (Map.Entry<String, Integer> entry : tfMap.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            double tfWeight = 1 + Math.log10(tf);

            List<InvertedIndex.Posting> postings = index.InvertedIndx.get(term);
            if (postings != null) {
                double idf = Math.log10((double) totalDocs / postings.size());
                queryVector.put(term, tfWeight * idf);
            }
        }
        return queryVector;
    }



     // Cosine similarity between two vectors (document and query)
     public double cosineSimilarity(Map<String, Double> docVector, Map<String, Double> queryVector) {
        Set<String> allTerms = new HashSet<>(docVector.keySet());
        allTerms.addAll(queryVector.keySet());

        double dotProduct = 0.0;
        double docMagnitude = 0.0;
        double queryMagnitude = 0.0;

        for (String term : allTerms) {
            double docValue = docVector.getOrDefault(term, 0.0);
            double queryValue = queryVector.getOrDefault(term, 0.0);
            dotProduct += docValue * queryValue;
            docMagnitude += Math.pow(docValue, 2);
            queryMagnitude += Math.pow(queryValue, 2);
        }

        return dotProduct / (Math.sqrt(docMagnitude) * Math.sqrt(queryMagnitude));
    }
    // Rank documents based on cosine similarity
    public List<String> rankDocuments(String query) {
        Map<String, Double> queryVector = computeQueryVector(query);
        Map<String, Double> documentSimilarities = new HashMap<>();

        // Compute cosine similarity for each document
        for (String docId : getAllDocumentIds()) {
            Map<String, Double> docVector = computeDocumentVector(docId);
            double similarity = cosineSimilarity(docVector, queryVector);
            documentSimilarities.put(docId, similarity);
        }

        // Sort documents by similarity
        return documentSimilarities.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
     // Retrieve all document IDs from the inverted index
     public Set<String> getAllDocumentIds() {
        Set<String> docIds = new HashSet<>();
        for (List<InvertedIndex.Posting> postings : index.InvertedIndx.values()) {
            for (InvertedIndex.Posting p : postings) {
                docIds.add(p.DocId);
            }
        }
        return docIds;
    }


}
