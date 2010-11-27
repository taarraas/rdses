/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.kiev.univ.linguistics;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import ua.kiev.univ.linguistics.MakeKeywordList.WordCnt;

/**
 *
 * @author taras
 */
public class Clusterizer {
    private static final Logger logger = Logger.getLogger(Main.class);
    public static double compare(Map<String, Integer> other, ArrayList<WordCnt> words, Set<String> stopList) {
        int cnt=0, sum=0;
        for (WordCnt wordCnt : words) {
            if (stopList.contains(wordCnt.word)) {
                continue;
            }
            if (other.containsKey(wordCnt.word)) {
                cnt++;
                sum += wordCnt.getTotalWeight();
            }
        }
        return cnt;
    }
    public String process(File file) throws IOException{
        Map<String, String> thisText = MSWordDocumentExtractor.extract(file.toString());
        String otherText = thisText.get(MSWordDocumentExtractor.OTHER);
        Map<String, Integer> other = WordFrequencyMap.getWordFrequencyMap(otherText);
        double max = 0;
        String best = null;
        for (Map.Entry<String, ArrayList<WordCnt>> entry : keywords.entrySet()) {
            double cur = compare(other, entry.getValue(), stopList);
            if (cur > max) {
                max = cur;
                best = entry.getKey();
            }
        }
        return best;
    }
    Set<String> stopList;
    Map<String, ArrayList<WordCnt> > keywords;
    private void load(String directory) throws IOException {
        File rootDir = new File(directory);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IOException("Directory not exists " + rootDir);
        }
        logger.info("Directory found. Processing...");
        // groupName->docName->paragraph->text
        Map<String, Map<String, Map<String, Map<String, Integer>>>> texts = new TreeMap();
        for (File dir : rootDir.listFiles()) {
            if (!dir.isDirectory()) {
                continue;
            }
            logger.debug(">" + dir.getName());
            Map<String, Map<String, Map<String, Integer>>> forDir = new TreeMap();
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.toString().endsWith(".doc")) {
                    //String extracted[] = ;
                    Map<String, String> thisText = MSWordDocumentExtractor.extract(file.toString());
                    if (!thisText.containsKey(MSWordDocumentExtractor.TITLE)) {
                        logger.debug("[!] " + file.getName());
                    } else {
                        logger.debug("[+] " + file.getName() + "\t" + thisText.get(MSWordDocumentExtractor.TITLE));
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
        stopList = MakeStopList.generateStopList(othersForBlacklist.toArray(new Map[othersForBlacklist.size()]));
        logger.debug("\n----- Stop list:");
        for (String string : stopList) {
            System.out.print(string+" ");
        }

        //KEYWORDS
        logger.debug("\n\n----- Keywords:");
        keywords = new TreeMap<String, ArrayList<WordCnt>>();
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

    public Clusterizer(String directory) throws IOException {
        load(directory);
    }

}
