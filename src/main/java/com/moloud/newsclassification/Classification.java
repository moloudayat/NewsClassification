/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moloud.newsclassification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

/**
 *
 * @author mabas
 */
public class Classification {

    private final String DATASET = "F:\\AI\\nlp\\NewsClassification\\BBCNews.json";
    private String[] classes = {"business", "tech", "politics", "sport", "entertainment"};
    private ArrayList features = new ArrayList();
    private ArrayList<String> stopwords = new ArrayList<String>();
    private String[][] trainSet;
    private String[][] testSet;

    public Classification() {
    }

    private String tokenization(String str) {
        return str.replaceAll("\\p{Punct}", "").replaceAll("( )+", " ").toLowerCase();
    }

    public void getDataSet() {
        getStopwords();
        try {
            JSONParser parser = new JSONParser();
            Object object = parser.parse(new FileReader(DATASET));
            JSONArray data = (JSONArray) object;
            int trainSize = (int) (data.size() * 0.7);
            trainSet = new String[trainSize][2];
            testSet = new String[data.size() - trainSize][2];
            for (int index = 0; index < trainSize; index++) {
                JSONObject section = (JSONObject) data.get(index);
                String text = (String) section.get("Text");
                String category = (String) section.get("Category");
                trainSet[index][0] = text;
                trainSet[index][1] = category;
            }
            int index2 = 0;
            for (int index = trainSize; index < data.size(); index++) {
                JSONObject section = (JSONObject) data.get(index);
                String text = (String) section.get("Text");
                String category = (String) section.get("Category");
                testSet[index2][0] = text;
                testSet[index2][1] = category;
                index2++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getStopwords() {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("stopwords.txt"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stopwords.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Classification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Classification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tokenizeDataSet() {
        for (int index = 0; index < trainSet.length; index++) {
            trainSet[index][0] = tokenization(trainSet[index][0]);
        }
        for (int index = 0; index < testSet.length; index++) {
            testSet[index][0] = tokenization(testSet[index][0]);
        }
    }

    public void removeStopwords() {
        for (int index = 0; index < trainSet.length; index++) {
            String news = trainSet[index][0];
            for (int index2 = 0; index2 < stopwords.size(); index2++) {
                news = news.replaceAll(" " + stopwords.get(index2) + " ", " ");
            }
            trainSet[index][0] = news;
        }
        for (int index = 0; index < testSet.length; index++) {
            String news = testSet[index][0];
            for (int index2 = 0; index2 < stopwords.size(); index2++) {
                news = news.replaceAll(" " + stopwords.get(index2) + " ", " ");
            }
            testSet[index][0] = news;
        }
    }
    
    public void buildUnigarm(){
        features.clear();
        for (int index = 0; index < trainSet.length; index++) {
            String[] words = trainSet[index][0].split(" ");
            for (int index2 = 0; index2 < words.length; index2++) {
                boolean exist = false;
                for (int index3 = 0; index3 < features.size(); index3++) {
                    if (features.get(index3).equals(words[index2])) {
                        exist = true;
                    }
                }
                if (!exist && words[index2].indexOf(" ") == -1 && !stopwords.contains(words[index2])) {
                    features.add(words[index2]);
                }
            }
        }
        
         // train.arff
        try {
            BufferedWriter trainFile = new BufferedWriter(new FileWriter("train.arff"));
            trainFile.write("@relation train");
            trainFile.write(System.getProperty("line.separator"));
            trainFile.write(System.getProperty("line.separator"));

            for (int index = 0; index < features.size(); index++) {
                trainFile.write("@attribute " + features.get(index) + " numeric");
                trainFile.write(System.getProperty("line.separator"));
            }
            trainFile.write("@attribute class {");
            for (int iterator = 0; iterator < classes.length - 1; iterator++) {
                trainFile.write(classes[iterator] + " ,");
            }
            trainFile.write(classes[classes.length - 1] + "}");
            trainFile.write(System.getProperty("line.separator"));
            trainFile.write(System.getProperty("line.separator"));
            trainFile.write("@data");
            trainFile.write(System.getProperty("line.separator"));
            for (int index = 0; index < trainSet.length; index++) {
                String news = trainSet[index][0];
                String[] words = news.split(" ");
                for (int index2 = 0; index2 < features.size(); index2++) {
                    int count = 0;
                    for (int index3 = 0; index3 < words.length; index3++) {
                        if (features.get(index2).equals(words[index3])) {
                            count++;
                        }
                    }
                    trainFile.write(count + ",");
                }
                trainFile.write(trainSet[index][1]);
                trainFile.write(System.getProperty("line.separator"));
            }

            trainFile.close();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        // test.arff
        try {
            BufferedWriter testFile = new BufferedWriter(new FileWriter("test.arff"));
            testFile.write("@relation test");
            testFile.write(System.getProperty("line.separator"));
            testFile.write(System.getProperty("line.separator"));

            for (int index = 0; index < features.size(); index++) {
                testFile.write("@attribute " + features.get(index) + " numeric");
                testFile.write(System.getProperty("line.separator"));
            }
            testFile.write("@attribute class {");
            for (int iterator = 0; iterator < classes.length - 1; iterator++) {
                testFile.write(classes[iterator] + " ,");
            }
            testFile.write(classes[classes.length - 1] + "}");
            testFile.write(System.getProperty("line.separator"));
            testFile.write(System.getProperty("line.separator"));
            testFile.write("@data");
            testFile.write(System.getProperty("line.separator"));
            for (int index = 0; index < testSet.length; index++) {
                String news = testSet[index][0];
                String[] words = news.split(" ");
                for (int index2 = 0; index2 < features.size(); index2++) {
                    int count = 0;
                    for (int index3 = 0; index3 < words.length; index3++) {
                        if (features.get(index2).equals(words[index3])) {
                            count++;
                        }
                    }
                    testFile.write(count + ",");
                }
                testFile.write(testSet[index][1]);
                testFile.write(System.getProperty("line.separator"));
            }
            testFile.close();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
       public String naiveBaseClassify() {
           String percision="";
            try {
                Instances trainSet = new Instances(new BufferedReader(new FileReader("F:/AI/nlp/NewsClassification/train.arff")));
                Instances testSet = new Instances(new BufferedReader(new FileReader("F:/AI/nlp/NewsClassification/test.arff")));
                trainSet.setClassIndex(trainSet.numAttributes() - 1);
                testSet.setClassIndex(testSet.numAttributes() - 1);
                NaiveBayes naiveBayes = new NaiveBayes();
                System.out.print("Training on " + trainSet.size() + " examples... ");
                naiveBayes.buildClassifier(trainSet);
                System.out.println("done.");
                Evaluation eval = new Evaluation(testSet);
                eval.evaluateModel(naiveBayes, testSet);
                percision=String.format("%.2f", eval.pctCorrect());
            } catch (Exception ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Unable to train classifier.");
                System.err.println("\t" + ex.getMessage());
            }
            return percision;
       }
                      
}
