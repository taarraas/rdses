/*
 * MakeWordFrequencyMap.java
 */

package ua.kiev.univ.linguistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author taras
 */
public class WordFrequencyMap {
    public static Map<String, Integer> getWordFrequencyMap(String text) {
        String words[]=text.split("[^а-яА-ЯіІєЄїЇ']+");
        Map<String, Integer> ret=new HashMap<String, Integer>();
        for (String string : words) {
            String cur=Normalizator.normalize(string);
            if (cur.length() < 2) {
                continue;
            }
            if (ret.containsKey(cur)) {
                ret.put(cur, ret.get(cur)+1);
            } else {
                ret.put(cur, 1);
            }
        }
        return ret;
    }
    public static Set<String> getAllKeys(Map<String, Integer>[] texts) {
        Set<String> ret=new HashSet<String>();
        for (Map<String, Integer> map : texts) {
            ret.addAll(map.keySet());
        }
        return ret;
    }

}
