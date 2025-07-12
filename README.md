<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>
<body>

  <h1>Web Crawler With TF-IDF</h1>
  <p>A Java project implements a simple web crawler and search engine using the TF-IDF (Term Frequency - Inverse Document Frequency) algorithm. It processes crawled web pages, builds an inverted index, calculates TF-IDF scores, and supports search queries using a query processor.</p>

  <h2>Features</h2>
  <ul>
    <li><strong>WebCrawler.java</strong>: Crawls web pages to collect data.</li>
    <li><strong>TextProcessing.java</strong>: Tokenizes, filters, and cleans text data.</li>
    <li><strong>Stemmer.java</strong>: Performs word stemming for normalization.</li>
    <li><strong>InvertedIndex.java</strong>: Builds and stores the inverted index for quick lookup.</li>
    <li><strong>TFIDFCalculator.java</strong>: Calculates TF-IDF scores for indexed terms.</li>
    <li><strong>QueryProcessor.java</strong>: Handles user queries and ranks results using TF-IDF.</li>
    <li><strong>Main.java</strong>: Entry point for running the application.</li>
  </ul>

  <h2>Technologies Used</h2>
  <ul>
    <li>Java</li>
    <li>Basic File I/O</li>
    <li>Collections Framework</li>
    <li>String Processing</li>
  </ul>

  <h2>How to Run</h2>
  <pre>
javac *.java
java Main
  </pre>
  <p>Ensure all Java files are in the same directory or set up your project structure accordingly.</p>

  <h2>License</h2>
  <p>This project is for educational purposes.</p>

</body>
</html>
