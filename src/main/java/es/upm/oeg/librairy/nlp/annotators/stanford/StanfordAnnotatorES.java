package es.upm.oeg.librairy.nlp.annotators.stanford;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordAnnotatorES extends CoreNLPAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(StanfordAnnotatorES.class);

    protected StanfordCoreNLP pipeline;

    public StanfordAnnotatorES() {
        props.put("tokenize.language","es");

        props.put("pos.model","edu/stanford/nlp/models/pos-tagger/spanish/spanish.tagger");

        props.put("ner.model","edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz");
        props.put("ner.language","es");
        props.put("sutime.language","spanish");
        props.put("parse.model","edu/stanford/nlp/models/lexparser/spanishPCFG.ser.gz");

        props.put("depparse.model","edu/stanford/nlp/models/parser/nndep/UD_Spanish.gz");
        props.put("depparse.language","spanish");

        props.put("ner.fine.regexner.mapping","edu/stanford/nlp/models/kbp/spanish/gazetteers/kbp_regexner_mapping_sp.tag");

        props.put("kbp.semgrex","edu/stanford/nlp/models/kbp/spanish/semgrex");
        props.put("kbp.tokensregex","edu/stanford/nlp/models/kbp/spanish/tokensregex");
        props.put("kbp.language","es");

        props.put("entitylink.wikidict","edu/stanford/nlp/models/kbp/spanish/wikidict_spanish.tsv");
    }

    // Ancora Treebank: https://nlp.stanford.edu/software/spanish-faq.shtml#tagset
    protected PoS translateFrom(String posTag){
        switch(posTag){
            case "adjective": return PoS.ADJECTIVE;
            case "proper_noun": return PoS.NOUN;
            case "adverb": return PoS.ADVERB;
            case "verb": return PoS.VERB;
            default:
                // Ancora annotation
                if (posTag.startsWith("aq")) return PoS.ADJECTIVE;
                if (posTag.startsWith("ao")) return PoS.ADJECTIVE;

                if (posTag.startsWith("rg")) return PoS.ADVERB;
                if (posTag.startsWith("rn")) return PoS.ADVERB;

                if (posTag.startsWith("dd")) return PoS.ARTICLE;
                if (posTag.startsWith("dp")) return PoS.ARTICLE;
                if (posTag.startsWith("dt")) return PoS.ARTICLE;
                if (posTag.startsWith("de")) return PoS.ARTICLE;
                if (posTag.startsWith("di")) return PoS.ARTICLE;
                if (posTag.startsWith("da")) return PoS.ARTICLE;

                if (posTag.startsWith("nc")) return PoS.NOUN;
                if (posTag.startsWith("np")) return PoS.PROPER_NOUN;

                if (posTag.startsWith("v")) return PoS.VERB;

                if (posTag.startsWith("p")) return PoS.PRONOUN;

                if (posTag.startsWith("cc")) return PoS.CONJUNCTION;
                if (posTag.startsWith("cs")) return PoS.CONJUNCTION;

                if (posTag.startsWith("i")) return PoS.INTERJECTION;

                if (posTag.startsWith("sp")) return PoS.PREPOSITION;

                if (posTag.startsWith("f")) return PoS.PUNCTUATION_MARK;

                if (posTag.startsWith("z")) return PoS.NUMBER;
                if (posTag.startsWith("dn")) return PoS.NUMBER;
                if (posTag.startsWith("do")) return PoS.NUMBER;

                if (posTag.startsWith("w")) return PoS.DATE;
                return PoS.SYMBOL;
        }
    }
}
