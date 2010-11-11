/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.kiev.univ.linguistics;

/**
 *
 * @author taras
 */
public class Config {
    static Config instance = new Config();
    public static Config getInstance() {
        return instance;
    }
    public double getCoefForKeywordList() {
        return 0.5;
    }
    public double getCoefForStopList() {
        return 0.5;
    }
}
