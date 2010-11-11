/*
 * MSWordStructureExtractor.java
 * 
 * Copyright (C) 2009 Voznyuk Taras Grigorievych
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ua.kiev.univ.linguistics;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.model.PAPX;

/**
 *
 * @author taras
 */
public class MSWordDocumentExtractor {
    public static String TITLE = "TITLE",
            HEADER = "HEADER",
            OTHER = "OTHER";

    public MSWordDocumentExtractor() {

    }
    private static final double RIGHTCOEF=4./5,
            CENTERCOEF=1./4;
    private static int getWidthSpace(ParagraphProperties pp, char ch) {
        switch (ch) {
            case ' ' : {
               return 5;
            }
            case '\t': {
                return 25;
            }
        }
        return -1;
    }
    /**
     *
     * @param pp
     * @param text
     * @return -2 - free, -1 - left, 0 - tittle, 1 - right heading
     */

    private static int getType(ParagraphProperties pp, String text) {
        if (pp.getJustification()==1) {
            return 0;
        }
        if (pp.getJustification()==2) {
            return 1;
        }
        int leftSpacing=0, rightSpacing=0;
        int i=0, j=text.length()-1;
        while (i<text.length() && (j=getWidthSpace(pp, text.charAt(i)))!=-1) {
            leftSpacing+=j;
            i++;
        }
        if (i==text.length()) {
            return -2;
        }
        while (i>=0 && (j=getWidthSpace(pp, text.charAt(i)))!=-1) {
            rightSpacing+=j;
            i--;
        }

        int width=3000;
        int leftTextBegin=pp.getIndentFromLeft()+leftSpacing;
        if (leftTextBegin>width*RIGHTCOEF) {
            return 1;
        }
        if (leftTextBegin>width*CENTERCOEF) {
            return 0;
        }
        return -1;
    }

    public static Map<String, String > extract(String filename) throws IOException{
        HWPFDocument doc=new HWPFDocument(new FileInputStream(filename));
        WordExtractor extractor = new WordExtractor(doc);
        String texts[] = extractor.getParagraphText();
        List<PAPX> pars = doc.getParagraphTable().getParagraphs();
        ParagraphProperties paragraphs[] = new ParagraphProperties[pars.size()];        

        for (int i = 0; i < paragraphs.length; i++) {
            paragraphs[i] = pars.get(i).getParagraphProperties(doc.getStyleSheet());
        }
        StringBuffer lt=new StringBuffer();
        int stage=0;
        String par[]=new String[3];
        for (int i = 0; i < par.length; i++) {
            par[i]="";
        }
        for (int i = 0; i < Math.min(paragraphs.length, texts.length); i++) {
            ParagraphProperties paragraphProperties = paragraphs[i];
            String text=texts[i];
            if (text.matches("[\\s]*")) {
                continue;
            }
            int type=getType(paragraphProperties, text);
            switch (stage) {
                case 0: {
                    if (type==1) {
                        par[0]+=text+"\n";
                    } else if (type==0){
                        par[1]+=text+"\n";
                        stage=2;
                    } else if (type==-1) {
                        par[2]+=text+"\n";
                        stage=2;
                    }
                    break;
                }
                case 2: {
                    par[2]+=text+"\n";
                }
            }
        }
        Map<String, String > textBlocks = new TreeMap<String, String>();
        if (!par[0].isEmpty()) {
            //par[0]=par[0].substring(0, par[0].length()-2);
            textBlocks.put(HEADER, trim(par[0]));
        }
        if (!par[1].isEmpty()) {
            //par[1]=par[1].substring(0, par[1].length()-2);
            textBlocks.put(TITLE, trim(par[1]));
        }
        if (!par[2].isEmpty()) {
            //par[2]=par[2].substring(0, par[2].length()-2);
            textBlocks.put(OTHER, trim(par[2]));
        }

        return textBlocks;
    }
    static public String trim(String source) {
        String tmp = source.replaceAll("\r\n", "");
        tmp = tmp.replaceAll("[\\ \\t]+", " ");
        if (tmp.endsWith("\n")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        return tmp;
    }
}
