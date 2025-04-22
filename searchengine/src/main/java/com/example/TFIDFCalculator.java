package com.example;

import java.util.*;
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

    // you will need it in cosine similarity (for comparing each doc to the query.)
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
