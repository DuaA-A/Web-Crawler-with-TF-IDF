package com.example;

import java.io.*;
import java.util.Arrays;

/**
 * A Porter Stemmer implementation that reduces English words to their root form.
 * This class processes words by removing common morphological endings following
 * the Porter Stemming Algorithm.
 */
public class Stemmer {
    private static final int INITIAL_BUFFER_SIZE = 50;
    private static final int MAX_WORD_LENGTH = 500;

    private char[] buffer;
    private int bufferIndex; // Current position in buffer
    private int stemEnd; // End of stemmed word
    private int suffixCheckPoint; // Point to check suffixes

    /**
     * Initializes a new Stemmer instance with default buffer size.
     */
    public Stemmer() {
        buffer = new char[INITIAL_BUFFER_SIZE];
        bufferIndex = 0;
        stemEnd = 0;
    }

    /**
     * Adds a string to be stemmed.
     *
     * @param input String to be stemmed
     * @throws IllegalArgumentException if input is null or empty
     */
    public void addString(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }
        add(input.toCharArray(), input.length());
    }

    /**
     * Adds a single character to the word being stemmed.
     *
     * @param ch Character to add
     */
    public void add(char ch) {
        ensureBufferCapacity(1);
        buffer[bufferIndex++] = ch;
    }

    /**
     * Adds an array of characters to the word being stemmed.
     *
     * @param chars Array of characters to add
     * @param length Number of characters to add
     */
    public void add(char[] chars, int length) {
        ensureBufferCapacity(length);
        System.arraycopy(chars, 0, buffer, bufferIndex, length);
        bufferIndex += length;
    }

    /**
     * Returns the stemmed word as a String.
     *
     * @return The stemmed word
     */
    @Override
    public String toString() {
        return new String(buffer, 0, stemEnd);
    }

    /**
     * Returns the length of the stemmed word.
     *
     * @return Length of the stemmed result
     */
    public int getResultLength() {
        return stemEnd;
    }

    /**
     * Returns the internal buffer containing the stemmed word.
     *
     * @return Character buffer with stemmed word
     */
    public char[] getResultBuffer() {
        return buffer;
    }

    private void ensureBufferCapacity(int additionalLength) {
        if (bufferIndex + additionalLength >= buffer.length) {
            buffer = Arrays.copyOf(buffer, bufferIndex + additionalLength + INITIAL_BUFFER_SIZE);
        }
    }

    private boolean isConsonant(int index) {
        if (index >= buffer.length || index < 0) {
            return false;
        }
        switch (buffer[index]) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return false;
            case 'y':
                return index == 0 || !isConsonant(index - 1);
            default:
                return true;
        }
    }

    private int countConsonantSequences() {
        int count = 0;
        int index = 0;
        while (index <= suffixCheckPoint) {
            if (isConsonant(index)) {
                index++;
                continue;
            }
            index++;
            while (index <= suffixCheckPoint && !isConsonant(index)) {
                index++;
            }
            if (index > suffixCheckPoint) {
                break;
            }
            count++;
            while (index <= suffixCheckPoint && isConsonant(index)) {
                index++;
            }
        }
        return count;
    }

    private boolean hasVowelInStem() {
        for (int i = 0; i <= suffixCheckPoint; i++) {
            if (!isConsonant(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDoubleConsonant(int index) {
        return index >= 1 && buffer[index] == buffer[index - 1] && isConsonant(index);
    }

    private boolean isCvcPattern(int index) {
        if (index < 2 || !isConsonant(index) || isConsonant(index - 1) || !isConsonant(index - 2)) {
            return false;
        }
        char ch = buffer[index];
        return ch != 'w' && ch != 'x' && ch != 'y';
    }

    private boolean endsWith(String suffix) {
        int suffixLength = suffix.length();
        int offset = stemEnd - suffixLength + 1;
        if (offset < 0) {
            return false;
        }
        for (int i = 0; i < suffixLength; i++) {
            if (buffer[offset + i] != suffix.charAt(i)) {
                return false;
            }
        }
        suffixCheckPoint = stemEnd - suffixLength;
        return true;
    }

    private void replaceEnding(String newEnding) {
        int length = newEnding.length();
        int offset = suffixCheckPoint + 1;
        for (int i = 0; i < length; i++) {
            buffer[offset + i] = newEnding.charAt(i);
        }
        stemEnd = suffixCheckPoint + length;
    }

    private void replaceIfConsonantSequence(String suffix, String replacement) {
        if (countConsonantSequences() > 0) {
            replaceEnding(replacement);
        }
    }

    private void step1RemovePluralsAndEndings() {
        if (buffer[stemEnd] == 's') {
            if (endsWith("sses")) {
                stemEnd -= 2;
            } else if (endsWith("ies")) {
                replaceEnding("i");
            } else if (buffer[stemEnd - 1] != 's') {
                stemEnd--;
            }
        }
        if (endsWith("eed")) {
            if (countConsonantSequences() > 0) {
                stemEnd--;
            }
        } else if ((endsWith("ed") || endsWith("ing")) && hasVowelInStem()) {
            stemEnd = suffixCheckPoint;
            if (endsWith("at")) {
                replaceEnding("ate");
            } else if (endsWith("bl")) {
                replaceEnding("ble");
            } else if (endsWith("iz")) {
                replaceEnding("ize");
            } else if (hasDoubleConsonant(stemEnd)) {
                stemEnd--;
                char ch = buffer[stemEnd];
                if (ch == 'l' || ch == 's' || ch == 'z') {
                    stemEnd++;
                }
            } else if (countConsonantSequences() == 1 && isCvcPattern(stemEnd)) {
                replaceEnding("e");
            }
        }
    }

    private void step2HandleTerminalY() {
        if (endsWith("y") && hasVowelInStem()) {
            buffer[stemEnd] = 'i';
        }
    }

    private void step3MapDoubleSuffixes() {
        if (stemEnd == 0) {
            return;
        }
        switch (buffer[stemEnd - 1]) {
            case 'a':
                if (endsWith("ational")) replaceIfConsonantSequence("ational", "ate");
                else if (endsWith("tional")) replaceIfConsonantSequence("tional", "tion");
                break;
            case 'c':
                if (endsWith("enci")) replaceIfConsonantSequence("enci", "ence");
                else if (endsWith("anci")) replaceIfConsonantSequence("anci", "ance");
                break;
            case 'e':
                if (endsWith("izer")) replaceIfConsonantSequence("izer", "ize");
                break;
            case 'l':
                if (endsWith("bli")) replaceIfConsonantSequence("bli", "ble");
                else if (endsWith("alli")) replaceIfConsonantSequence("alli", "al");
                else if (endsWith("entli")) replaceIfConsonantSequence("entli", "ent");
                else if (endsWith("eli")) replaceIfConsonantSequence("eli", "e");
                else if (endsWith("ousli")) replaceIfConsonantSequence("ousli", "ous");
                break;
            case 'o':
                if (endsWith("ization")) replaceIfConsonantSequence("ization", "ize");
                else if (endsWith("ation")) replaceIfConsonantSequence("ation", "ate");
                else if (endsWith("ator")) replaceIfConsonantSequence("ator", "ate");
                break;
            case 's':
                if (endsWith("alism")) replaceIfConsonantSequence("alism", "al");
                else if (endsWith("iveness")) replaceIfConsonantSequence("iveness", "ive");
                else if (endsWith("fulness")) replaceIfConsonantSequence("fulness", "ful");
                else if (endsWith("ousness")) replaceIfConsonantSequence("ousness", "ous");
                break;
            case 't':
                if (endsWith("aliti")) replaceIfConsonantSequence("aliti", "al");
                else if (endsWith("iviti")) replaceIfConsonantSequence("iviti", "ive");
                else if (endsWith("biliti")) replaceIfConsonantSequence("biliti", "ble");
                break;
            case 'g':
                if (endsWith("logi")) replaceIfConsonantSequence("logi", "log");
                break;
        }
    }

    private void step4HandleSpecificEndings() {
        switch (buffer[stemEnd]) {
            case 'e':
                if (endsWith("icate")) replaceIfConsonantSequence("icate", "ic");
                else if (endsWith("ative")) replaceIfConsonantSequence("ative", "");
                else if (endsWith("alize")) replaceIfConsonantSequence("alize", "al");
                break;
            case 'i':
                if (endsWith("iciti")) replaceIfConsonantSequence("iciti", "ic");
                break;
            case 'l':
                if (endsWith("ical")) replaceIfConsonantSequence("ical", "ic");
                else if (endsWith("ful")) replaceIfConsonantSequence("ful", "");
                break;
            case 's':
                if (endsWith("ness")) replaceIfConsonantSequence("ness", "");
                break;
        }
    }

    private void step5RemoveEndings() {
        if (stemEnd == 0) {
            return;
        }
        switch (buffer[stemEnd - 1]) {
            case 'a':
                if (endsWith("al") && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'c':
                if ((endsWith("ance") || endsWith("ence")) && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'e':
                if (endsWith("er") && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'i':
                if (endsWith("ic") && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'l':
                if ((endsWith("able") || endsWith("ible")) && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'n':
                if ((endsWith("ant") || endsWith("ement") || endsWith("ment") || endsWith("ent")) && countConsonantSequences() > 1)
                    stemEnd = suffixCheckPoint;
                break;
            case 'o':
                if ((endsWith("ion") && suffixCheckPoint >= 0 && (buffer[suffixCheckPoint] == 's' || buffer[suffixCheckPoint] == 't')) || endsWith("ou"))
                    if (countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 's':
                if (endsWith("ism") && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 't':
                if ((endsWith("ate") || endsWith("iti")) && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'u':
                if (endsWith("ous") && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'v':
                if (endsWith("ive") && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
            case 'z':
                if (endsWith("ize") && countConsonantSequences() > 1) stemEnd = suffixCheckPoint;
                break;
        }
    }

    private void step6RemoveFinalE() {
        suffixCheckPoint = stemEnd;
        if (buffer[stemEnd] == 'e') {
            int consonantSequences = countConsonantSequences();
            if (consonantSequences > 1 || (consonantSequences == 1 && !isCvcPattern(stemEnd - 1))) {
                stemEnd--;
            }
        }
        if (buffer[stemEnd] == 'l' && hasDoubleConsonant(stemEnd) && countConsonantSequences() > 1) {
            stemEnd--;
        }
    }

    /**
     * Applies the Porter Stemming Algorithm to the word in the buffer.
     */
    public void stem() {
        if (bufferIndex <= 2) {
            stemEnd = bufferIndex;
            bufferIndex = 0;
            return;
        }
        stemEnd = bufferIndex - 1;
        step1RemovePluralsAndEndings();
        step2HandleTerminalY();
        step3MapDoubleSuffixes();
        step4HandleSpecificEndings();
        step5RemoveEndings();
        step6RemoveFinalE();
        stemEnd = stemEnd + 1;
        bufferIndex = 0;
    }

    /**
     * Processes text files, stems words, and outputs results.
     *
     * @param args File paths to process
     */
    public static void main(String[] args) {
        Stemmer stemmer = new Stemmer();
        char[] wordBuffer = new char[MAX_WORD_LENGTH + 1];

        for (String filePath : args) {
            try (FileInputStream in = new FileInputStream(filePath)) {
                int wordLength = 0;
                while (true) {
                    int ch = in.read();
                    if (ch < 0) {
                        break;
                    }
                    if (Character.isLetter((char) ch)) {
                        if (wordLength < MAX_WORD_LENGTH) {
                            wordBuffer[wordLength++] = Character.toLowerCase((char) ch);
                        }
                    } else {
                        if (wordLength > 0) {
                            stemmer.add(wordBuffer, wordLength);
                            stemmer.stem();
                            System.out.print(stemmer.toString());
                            wordLength = 0;
                        }
                        System.out.print((char) ch);
                    }
                }
                if (wordLength > 0) {
                    stemmer.add(wordBuffer, wordLength);
                    stemmer.stem();
                    System.out.print(stemmer.toString());
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filePath);
            } catch (IOException e) {
                System.out.println("Error reading file: " + filePath);
            }
        }
    }
}
