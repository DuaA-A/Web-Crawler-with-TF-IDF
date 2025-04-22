package com.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {

      public static class Posting {
          public   String DocId ;
          public  int TF ;

          public Posting(String docId , int TermF){
              this.DocId = docId ;
              this.TF = TermF ;
          }
          @Override
          public String toString(){
             return "(" + DocId +" , " + TF+ ")" ;
          }
      }
      public Map<String , List<Posting>>InvertedIndx = new HashMap<>();

      public void BuildInvertedIndx(String FolderPath){
          File folder = new File(FolderPath) ;
          File[]files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

          if (files == null) {
              System.out.println("Folder not found or empty.");
              return;
          }
          for (File file : files){
              String docId = file.getName() ;
              String content = ReadContent(file) ;
              BuildIndxDoc(docId , content) ;
          }
      }
      public String ReadContent(File file){
          StringBuilder sb = new StringBuilder() ;
          try(BufferedReader Br = new BufferedReader(new FileReader(file))){
              String line ;
              while ((line = Br.readLine())!= null){
                  sb.append(line) ;
              }
          }
           catch (IOException e) {
              System.err.println("error in reading " + file.getName());
          }
          return sb.toString() ;
      }
      public void BuildIndxDoc(String docID , String Content){
          String[] tokens = Content.toLowerCase().split("\\W+");
          Map<String , Integer> TF = new HashMap<>() ;
          for (String token : tokens){
              if (!token.isEmpty()){ // Make sure the word is not " "
                  TF.put(token, TF.getOrDefault(token, 0) + 1) ;
              }
          }

          for (Map.Entry<String, Integer> entry : TF.entrySet()) {
              String term = entry.getKey();
              int freq = entry.getValue();
              Posting posting = new Posting(docID, freq);
              InvertedIndx.computeIfAbsent(term, k -> new ArrayList<>()).add(posting);
          }
      }
    public void printIndex() {
        System.out.println("Inverted Index:");
        for (Map.Entry<String, List<Posting>> entry : InvertedIndx.entrySet()) {
            System.out.print(entry.getKey() + " → ");
            for (Posting p : entry.getValue()) {
                System.out.print(p + " ");
            }
            System.out.println();
        }
    }
    public int  Print_DF(String term){
        for (Map.Entry<String, List<Posting>> entry : InvertedIndx.entrySet()) {
            if (entry.getKey().equals(term)){
               return entry.getValue().size() ;
            }
        }
        return 0 ;
    }


}
