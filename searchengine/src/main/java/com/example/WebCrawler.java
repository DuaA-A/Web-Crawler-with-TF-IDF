package com.example;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
    private HashSet<String> visited = new HashSet<>();
    private int maxPages;
    private int currentCount = 0;

    public WebCrawler(int maxPages) {
        this.maxPages = maxPages;
    }

    public void crawl(String url, int depth) {
        if (visited.contains(url) || currentCount >= maxPages || depth <= 0)
            return;
        try {
            Document doc = Jsoup.connect(url).get();
            visited.add(url);
            currentCount++;
            String fileName = url.replaceAll("https?://", "")
                                .replaceAll("[^a-zA-Z0-9]", "_") + ".txt";
            File dir = new File("pages");
            if (!dir.exists()) 
                dir.mkdirs();
            File file = new File(dir, fileName);
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("URL: " + url + "\n\n");
                fw.write(doc.body().text());
            }
            System.out.println("Saved: " + file.getAbsolutePath());
            Elements links = doc.select("a[href]");
            for (Element l : links) {
                String nextUrl = l.absUrl("href");
                if (nextUrl.isEmpty() || visited.contains(nextUrl) || nextUrl.equals(url))
                    continue;
                else
                    crawl(nextUrl, depth - 1);
            }
        } catch (IOException e) {
            System.err.println("Failed to crawl: " + url + " due to " + e.getMessage());
        }
    }
    
}
