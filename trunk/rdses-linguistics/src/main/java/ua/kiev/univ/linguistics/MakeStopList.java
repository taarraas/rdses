/*
 * MakeStopList.java
 */

package ua.kiev.univ.linguistics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author taras
 */
public class MakeStopList {
    public static Set<String> generateStopList(Map<String, Integer>[] texts) {
        Set<String> all=WordFrequencyMap.getAllKeys(texts);
        Set<String> stopList=new HashSet<String>();
        for (String string : all) {
            int cnt=0;
            for (Map<String, Integer> map : texts) {
                if (map.containsKey(string)) {
                    cnt++;
                }
            }
            if (cnt>=Config.getInstance().getCoefForStopList() * texts.length) {
                stopList.add(string);
            }
        }
        return stopList;
    }
}