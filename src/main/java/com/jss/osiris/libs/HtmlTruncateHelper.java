package com.jss.osiris.libs;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.stereotype.Service;

@Service
public class HtmlTruncateHelper {

    public String truncateHtml(String html, int percentage) {
        Document document = Jsoup.parse(html);
        Element body = document.body();

        // Compte le nombre total de mots
        int totalWords = countWords(body);
        int targetWords = (int) Math.ceil(totalWords * (percentage / 100.0));

        // Liste pour stocker les éléments de sortie
        List<Node> truncatedContent = new ArrayList<>();
        int currentWordCount = 0;

        for (Node node : body.childNodes()) {
            if (currentWordCount >= targetWords) {
                break;
            }
            Node clone = node.clone();
            currentWordCount += truncateNode(clone, targetWords - currentWordCount);
            truncatedContent.add(clone);
        }

        // Remplace le contenu du body par le contenu tronqué
        body.empty();
        for (Node node : truncatedContent) {
            body.appendChild(node);
        }

        return body.html();
    }

    // Compte les mots visibles dans un nœud
    private int countWords(Node node) {
        if (node instanceof TextNode) {
            return ((TextNode) node).text().trim().split("\\s+").length;
        } else if (node instanceof Element) {
            int wordCount = 0;
            for (Node child : node.childNodes()) {
                wordCount += countWords(child);
            }
            return wordCount;
        }
        return 0;
    }

    // Tronque un nœud jusqu'à atteindre le nombre de mots cible
    private int truncateNode(Node node, int targetWords) {
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            String[] words = textNode.text().trim().split("\\s+");
            if (words.length <= targetWords) {
                return words.length;
            } else {
                // Tronquer le texte au nombre de mots requis
                String truncatedText = String.join(" ", java.util.Arrays.copyOfRange(words, 0, targetWords));
                textNode.text(truncatedText);
                return targetWords;
            }
        } else if (node instanceof Element) {
            Element element = (Element) node;
            int wordCount = 0;
            List<Node> childNodes = new ArrayList<>(element.childNodes());
            for (Node child : childNodes) {
                if (wordCount >= targetWords) {
                    child.remove();
                } else {
                    wordCount += truncateNode(child, targetWords - wordCount);
                }
            }
            return wordCount;
        }
        return 0;
    }
}
