/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.kiev.univ.linguistics;
import java.io.*;
import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ua.kiev.univ.linguistics.MakeKeywordList.WordCnt;

/**
 *
 * @author taras
 */
public class Clusterizer {
    private static final Logger logger = Logger.getLogger(Main.class);
    public static double compare(Map<String, Integer> other, ArrayList<WordCnt> words, Set<String> stopList) {
        int cnt=0, sum=0, possibleCnt = 0;
        for (WordCnt wordCnt : words) {
            if (stopList.contains(wordCnt.word)) {
                continue;
            }
            if (other.containsKey(wordCnt.word)) {
                cnt++;
                sum += wordCnt.getTotalWeight();
            }
            possibleCnt+=wordCnt.cnt;
        }
        return (double)cnt/possibleCnt;
    }
    public ClusterizationResult getClusterizationResult(File file) {
        ClusterizationResult result = new ClusterizationResult();
        Map<String, String> thisText;
        try {
            thisText = MSWordDocumentExtractor.extract(file.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        String otherText = thisText.get(MSWordDocumentExtractor.OTHER);
        Map<String, Integer> other = WordFrequencyMap.getWordFrequencyMap(otherText);
        double max = 0;
        String best = null;
        for (Map.Entry<String, ArrayList<WordCnt>> entry : keywords.entrySet()) {
            double cur = compare(other, entry.getValue(), stopList);
            result.getPercentages().put(entry.getKey(), cur);
        }
        return result;
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
        if (max < 0.3) {
            return "unclassified";
        }
        return best+":"+max;
    }
    Set<String> stopList;
    Map<String, ArrayList<WordCnt> > keywords;
    private void load(String directory) throws IOException {
        logger.setLevel(Level.DEBUG);
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
        StringBuffer sb = new StringBuffer();
        sb.append("\n----- Stop list:");
        for (String string : stopList) {
            sb.append(string+" ");
        }
        logger.debug(sb.toString());
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
            sb = new StringBuffer();
            sb.append("\n>"+group.getKey());
            for (WordCnt wordCnt : keywords.get(group.getKey())) {
                sb.append(wordCnt.getWord()+":"+wordCnt.goodness()+" ");
            }
            logger.debug(sb.toString());
        }

    }

    public Clusterizer(String directory) throws IOException {
        load(directory);
    }
    private static String getTemplatesDir() {
        File dir = new File("templates");
        if (dir.isDirectory())
            return dir.getAbsolutePath();
        dir = new File("build/linguistics/templates");
        if (dir.isDirectory())
            return dir.getAbsolutePath();
        dir = new File("/home/taras/rdses/templates");
        if (dir.isDirectory())
            return dir.getAbsolutePath();


        throw new RuntimeException("Failed to determine templates directory path");
    }
    public Clusterizer() throws IOException {
        load(getTemplatesDir());
    }

}
