# News Classification
With the rapid growth of unstructured data throughout the internet, the need for a strong and swift classifier can be felt more and more. These data have different formats like sounds, pictures, and texts. Among all of these forms, texts are more popular among users and they use them for different purposes. Reviewing news is one of these uses. However, human tagging for categorizing news can have some errors. Furthermore, the human workplace can not classify all of the news that is added to the internet everyday.
This research evaluate different classifications methods on BBC News dataset from Stanford university.

## Dataset
The BBC NEWS dataset is broken into 1490 records for training and 735 for testing. The news should be classified into one of the five categories: sport, tech, business, entertainment or politics. For training classifiers there are 543 data for business, 473 for tech, 316 for politics, 508 for sport, and 332 for entertainment.

## Preprocessing
First and foremost step before classification is preprocessing which means cleaning and purifying data into a format that can be analyzed.

### Remove Punctuations
This is the basic preprocessing that should be done for every text.

### Use Lowercase Letters
The majority of languages have several forms for each letter. Using one form for those does not change the meaning of texts. In this research all of the letters are converting lowercase format.

### Remove Stopwords
Stopwords are the most common words that are used in every language. These words do not add specific meaning to the text. For reducing dataset size and training time, it is more beneficial to remove stopwords.
For this purpose, I used All English Stopwords from kaggle which contains 733 words.

## Feature Selection
For predicting a suitable model for training, it is crucial to select proper features.

### Unigrams
This feature calculates the frequency count of a single term in dataset.

### TF-IDF
TF-IDF score represents the relative importance of a term in the document as well as the corpus. The score consists of two parts:
* __TF (Term Frequency):__ (Number of repetitions of term in a document) / (Number of term in a document)
* __IDF (inverse document frequency):__ Log([(Number of documents) / (Number of documents containing the term)]

## Classification models
Classification algorithms most commonly used for news classification are Naive Bayes, Logistic regression, Support Vector Machine (Linear kernel), Decision tree and Random forest

<p align="center" width="100%">
    <img  src="https://github.com/moloudayat/NewsClassification/blob/master/img/UI.PNG?raw=true&style=centerme"> 
</p>

|Train Accuracy%|Naive Bayes|Logistic|  SVM   |Decision tree| Random forest|
|--------|-----------|--------|--------|-------------|--------------|
|unigrams|   98.17%  | 97.35% | 100% |    97.50%   |100%|
| TF-IDF |98.37%|92.51%|100%|97.60%|100%|
#####
|Test Accuracy%|Naive Bayes|Logistic|  SVM   |Decision tree| Random forest|
|--------|-----------|--------|--------|-------------|--------------|
|unigrams|   94.40%  | 95.50% | 96.86% |    84.56%   |93.95%|
| TF-IDF |93.06%| 95.12%|94.18%|84.34%|90.60%
