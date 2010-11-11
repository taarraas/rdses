/*
 * MakeStopList.java
 */
package ua.kiev.univ.linguistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author taras
 */
public class MakeKeywordList {

    public static class WordCnt implements Comparable<WordCnt> {

        String word;
        int cnt;
        int totalWeight;

        public String getWord() {
            return word;
        }

        public int getTotalWeight() {
            return totalWeight;
        }

        public int getCnt() {
            return cnt;
        }

        public WordCnt(String word, int cnt, int totalWeight) {
            this.word = word;
            this.cnt = cnt;
            this.totalWeight = totalWeight;
        }

        public int goodness() {
            double averWeight = (double) totalWeight / cnt;
            return (int) (averWeight * 10);
        }

        public int compareTo(WordCnt o) {
            return o.goodness() - this.goodness();
        }
    }

    public static ArrayList<WordCnt> generateKeywords(Map<String, Integer>[] texts, Collection<String> stopList) {
        Set<String> all = WordFrequencyMap.getAllKeys(texts);
        ArrayList<WordCnt> counts = new ArrayList<WordCnt>();
        for (String string : all) {
            if (stopList.contains(string)) {
                continue;
            }
            int cnt = 0, sum = 0;
            for (Map<String, Integer> map : texts) {
                if (map.containsKey(string)) {
                    cnt++;
                    sum += map.get(string);
                }
            }
            if (cnt > Config.getInstance().getCoefForKeywordList() * texts.length) {
                counts.add(new WordCnt(string, cnt, sum));
            }
        }
        Collections.sort(counts);
        return counts;
    }
}
