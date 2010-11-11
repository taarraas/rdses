/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.kiev.univ.linguistics;

import ua.kiev.univ.linguistics.MSWordDocumentExtractor;
import ua.kiev.univ.linguistics.MakeKeywordList;
import ua.kiev.univ.linguistics.MakeKeywordList.WordCnt;
import ua.kiev.univ.linguistics.WordFrequencyMap;
import ua.kiev.univ.linguistics.MakeStopList;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author taras
 */
public class MainCLI {

    public static void main(String argv[]) throws Exception {
        final String DIR = "archive";
        File rootDir = new File(DIR);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IOException("Directory not exists " + rootDir);
        }
        System.out.println("---Loading documents");
        // groupName->docName->paragraph->text
        Map<String, Map<String, Map<String, Map<String, Integer>>>> texts = new TreeMap();
        for (File dir : rootDir.listFiles()) {
            if (!dir.isDirectory()) {
                continue;
            }
            System.out.println(">" + dir.getName());
            Map<String, Map<String, Map<String, Integer>>> forDir = new TreeMap();
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.toString().endsWith(".doc")) {
                    //String extracted[] = ;
                    Map<String, String> thisText = MSWordDocumentExtractor.extract(file.toString());
                    if (!thisText.containsKey(MSWordDocumentExtractor.TITLE)) {
                        System.out.println("[!] " + file.getName());
                    } else {
                        System.out.println("[+] " + file.getName() + "\t" + thisText.get(MSWordDocumentExtractor.TITLE));
                    }
                    Map<String, Map<String, Integer>> forDoc = new TreeMap<String, Map<String, Integer>>();
                    for (String string : thisText.keySet()) {
                        forDoc.put(string, WordFrequencyMap.getWordFrequencyMap(thisText.get(string)));
                    }
                    forDir.put(file.getName(), forDoc);
                }
            }
            texts.put(dir.getName(), forDir);
        }

        //BLACK LIST
        ArrayList<Map<String, Integer>> othersForBlacklist = new ArrayList();
        for (Map.Entry<String, Map<String, Map<String, Map<String, Integer>>>> group : texts.entrySet()) {
            for (Map.Entry<String, Map<String, Map<String, Integer>>> doc : group.getValue().entrySet()) {
                if (doc.getValue().containsKey(MSWordDocumentExtractor.OTHER)) {
                    Map<String, Integer> other = doc.getValue().get(MSWordDocumentExtractor.OTHER);
                    othersForBlacklist.add(other);
                }
            }
        }
        Set<String> stopList = MakeStopList.generateStopList(othersForBlacklist.toArray(new Map[othersForBlacklist.size()]));
        System.out.println("\n----- Stop list:");
        for (String string : stopList) {
            System.out.print(string+" ");
        }

        //KEYWORDS
        System.out.print("\n\n----- Keywords:");
        Map<String, ArrayList<WordCnt> > keywords = new TreeMap<String, ArrayList<WordCnt>>();
        for (Map.Entry<String, Map<String, Map<String, Map<String, Integer>>>> group : texts.entrySet()) {
            ArrayList<Map<String, Integer>> docsInGroup = new ArrayList();
            for (Map.Entry<String, Map<String, Map<String, Integer>>> doc : group.getValue().entrySet()) {
                if (doc.getValue().containsKey(MSWordDocumentExtractor.OTHER)) {
                    Map<String, Integer> other = doc.getValue().get(MSWordDocumentExtractor.OTHER);
                    docsInGroup.add(other);
                }
            }
            keywords.put(group.getKey(), MakeKeywordList.generateKeywords(docsInGroup.toArray(new Map[docsInGroup.size()]), stopList));
            System.out.println("\n>"+group.getKey());
            for (WordCnt wordCnt : keywords.get(group.getKey())) {
                System.out.print(wordCnt.getWord()+":"+wordCnt.goodness()+" ");
            }
        }
    }
}
