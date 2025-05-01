package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryProcessor {
    private final TextProcessing textProcessor;
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        SYNONYMS.put("pharaoh", "king");
        SYNONYMS.put("tomb", "burial");
        SYNONYMS.put("egypt", "nile");
        SYNONYMS.put("pyramid", "monument");

        SYNONYMS.put("king", "monarch");
        SYNONYMS.put("queen", "consort");
        SYNONYMS.put("dynasty", "lineage");
        SYNONYMS.put("ruler", "sovereign");
        SYNONYMS.put("succession", "inheritance");

        SYNONYMS.put("mummy", "embalmed");
        SYNONYMS.put("sarcophagus", "coffin");
        SYNONYMS.put("necropolis", "cemetery");
        SYNONYMS.put("canopic", "jar");

        SYNONYMS.put("delta", "floodplain");
        SYNONYMS.put("oasis", "spring");
                SYNONYMS.put("giza", "plateau");
        SYNONYMS.put("luxor", "thebes");

        SYNONYMS.put("sphynx", "statue");
        SYNONYMS.put("obelisk", "pillar");
        SYNONYMS.put("temple", "shrine");
        SYNONYMS.put("pylon", "gateway");
        SYNONYMS.put("colonnade", "arcade");

        SYNONYMS.put("god", "deity");
        SYNONYMS.put("goddess", "divinity");
        SYNONYMS.put("ra", "sun");
        SYNONYMS.put("isis", "mother");
        SYNONYMS.put("horus", "falcon");
        SYNONYMS.put("amulet", "charm");

        SYNONYMS.put("hieroglyph", "script");
        SYNONYMS.put("papyrus", "scroll");
        SYNONYMS.put("scribe", "scholar");
        SYNONYMS.put("cartouche", "oval");
        SYNONYMS.put("ankh", "cross");
        SYNONYMS.put("scarab", "beetle");

        // Historical and Social Terms
        SYNONYMS.put("civilization", "society");
        SYNONYMS.put("reign", "tenure");
        SYNONYMS.put("noble", "aristocrat");
        SYNONYMS.put("peasant", "laborer");
        SYNONYMS.put("vizier", "advisor");
        SYNONYMS.put("nomarch", "governor");

        // Time and Periods
        SYNONYMS.put("kingdom", "era");
        SYNONYMS.put("pharaonic", "ancient");
        SYNONYMS.put("predynastic", "early");
        SYNONYMS.put("ptolemaic", "greek");
        SYNONYMS.put("ramesside", "new");

        // Economy and Resources
        SYNONYMS.put("trade", "commerce");
        SYNONYMS.put("grain", "crop");
        SYNONYMS.put("gold", "metal");
        SYNONYMS.put("quarry", "mine");
        SYNONYMS.put("labor", "work");
        SYNONYMS.put("tribute", "tax");

        // Warfare and Military
        SYNONYMS.put("chariot", "cart");
        SYNONYMS.put("soldier", "warrior");
        SYNONYMS.put("battle", "conflict");
        SYNONYMS.put("army", "force");
        SYNONYMS.put("fortress", "citadel");

    }

    public QueryProcessor() {
        this.textProcessor = new TextProcessing();
    }

    /**
     * Processes a query by tokenizing and expanding terms.
     * @param query Input query string
     * @return List of processed and expanded terms
     */
    public List<String> processQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> terms = textProcessor.processText(query);
        List<String> expanded = new ArrayList<>(terms);

        // Expand plurals and synonyms
        terms.forEach(term -> {
            if (term.endsWith("s")) {
                expanded.add(term.substring(0, term.length() - 1));
            }
            String synonym = SYNONYMS.get(term);
            if (synonym != null) {
                expanded.add(synonym);
            }
        });

        return expanded;
    }

    /**
     * Prints the processed and expanded query terms.
     * @param query Input query string
     */
    public void printProcessedQuery(String query) {
        List<String> processedTerms = processQuery(query);
        System.out.println("Original query: \"" + query + "\"");
        if (processedTerms.isEmpty()) {
            System.out.println("No valid terms after processing.");
        } else {
            System.out.println("Processed and expanded terms:");
            for (int i = 0; i < processedTerms.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, processedTerms.get(i));
            }
            System.out.println("Total terms: " + processedTerms.size());
        }
    }
}