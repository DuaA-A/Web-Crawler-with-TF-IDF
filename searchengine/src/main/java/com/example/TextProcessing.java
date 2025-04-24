package com.example;

import java.util.*;
import java.util.regex.Pattern;

public class TextProcessing {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "have", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with", "this", "which", "not", "he",
            "she", "his", "her", "their", "they", "them", "or", "but", "what",
            "when", "where", "who", "whom", "whose", "how", "why", "if", "else",
            "there", "here", "been", "being", "having", "had", "do", "does",
            "did", "done", "would", "could", "should", "can", "may", "might"
    ));

    private static final Pattern WIKI_CITATION = Pattern.compile("\\[\\d+\\]");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^\\p{L}\\p{Nd}'-]+");
    private static final Pattern APOSTROPHE = Pattern.compile("(\\w)'s\\b");
    private static final Stemmer STEMMER = new Stemmer();

    public static List<String> processText(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        // Remove Wikipedia citations
        text = WIKI_CITATION.matcher(text).replaceAll("");

        // Normalize apostrophes
        text = APOSTROPHE.matcher(text).replaceAll("$1");

        // Tokenize
        String[] tokens = SPECIAL_CHARS.matcher(text.toLowerCase())
                .replaceAll(" ")
                .trim()
                .split("\\s+");

        List<String> processedTokens = new ArrayList<>();
        for (String token : tokens) {
            if (isValidToken(token)) {
                processedTokens.add(stemToken(token));
            }
        }

        return processedTokens;
    }

    private static boolean isValidToken(String token) {
        return token.length() > 2 &&
                !STOP_WORDS.contains(token) &&
                !token.matches("^\\d+$") && // exclude pure numbers
                !token.matches("^\\W+$");   // exclude tokens with only special chars
    }

    private static String stemToken(String token) {
        STEMMER.addString(token);
        STEMMER.stem();
        return STEMMER.toString();
    }
}