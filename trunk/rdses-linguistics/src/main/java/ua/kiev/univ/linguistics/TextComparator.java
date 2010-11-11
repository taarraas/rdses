/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.kiev.univ.linguistics;

import ua.kiev.univ.linguistics.MakeKeywordList;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author taras
 */
public class TextComparator {
    public static double compare(ArrayList<MakeKeywordList.WordCnt> keywords, Map<String, Integer> text) {
        int cnt = 0;
        for (MakeKeywordList.WordCnt wordCnt : keywords) {
            if (text.containsKey(wordCnt.getWord())) {
                cnt++;
            }
        }
        return (double)cnt/keywords.size();
    }
}
