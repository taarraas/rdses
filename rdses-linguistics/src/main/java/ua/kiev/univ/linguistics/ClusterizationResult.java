/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.kiev.univ.linguistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 *
 * @author taras
 */
public class ClusterizationResult {
    private Map<String, Double> percentages = new TreeMap<String, Double>();

    public Map<String, Double> getPercentages() {
        return percentages;
    }

    public static class PSD implements Comparable<PSD>{

        public PSD(String theme, double intersectionRate) {
            this.theme = theme;
            this.intersectionRate = intersectionRate;
        }

        String theme;
        double intersectionRate;

        @Override
        public int compareTo(PSD o) {
            return Double.valueOf(o.intersectionRate).compareTo(intersectionRate);
        }
    }
    @Override
    public String toString() {
        ArrayList<PSD> list = new ArrayList<ClusterizationResult.PSD>();
        for (Entry<String, Double> entry : percentages.entrySet()) {
            list.add(new PSD(entry.getKey(), entry.getValue()));
        }
        Collections.sort(list);
        StringBuffer buffer = new StringBuffer();
        for (PSD psd : list) {
            buffer.append(psd.theme+":"+psd.intersectionRate+" ");
        }
        return buffer.toString();
    }

}
