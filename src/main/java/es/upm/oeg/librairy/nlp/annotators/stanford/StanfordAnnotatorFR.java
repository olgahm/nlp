package es.upm.oeg.librairy.nlp.annotators.stanford;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordAnnotatorFR extends CoreNLPAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(StanfordAnnotatorFR.class);

    protected StanfordCoreNLP pipeline;

    public StanfordAnnotatorFR() {
        props.put("tokenize.language","fr");

        props.put("pos.model","edu/stanford/nlp/models/pos-tagger/french/french.tagger");

        props.put("parse.model","edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");

        props.put("depparse.model","edu/stanford/nlp/models/parser/nndep/UD_French.gz");
        props.put("depparse.language","french");
    }

    // French Treebank: http://www.llf.cnrs.fr/Gens/Abeille/French-Treebank-fr.php
    protected PoS translateFrom(String posTag){
        switch(posTag){
            case "adjective": return PoS.ADJECTIVE;
            case "proper_noun": return PoS.NOUN;
            case "adverb": return PoS.ADVERB;
            case "verb": return PoS.VERB;
            default:
                // Ancora annotation
                if (posTag.startsWith("a")) return PoS.ADJECTIVE;

                if (posTag.startsWith("adv")) return PoS.ADVERB;

                if (posTag.startsWith("cc")) return PoS.CONJUNCTION;
                if (posTag.startsWith("cs")) return PoS.CONJUNCTION;

                if (posTag.startsWith("ci")) return PoS.PRONOUN;
                if (posTag.startsWith("pro")) return PoS.PRONOUN;

                if (posTag.startsWith("d")) return PoS.ARTICLE;

                if (posTag.startsWith("i")) return PoS.INTERJECTION;

                if (posTag.startsWith("nc")) return PoS.NOUN;
                if (posTag.startsWith("np")) return PoS.PROPER_NOUN;

                if (posTag.startsWith("p")) return PoS.PREPOSITION;

                if (posTag.startsWith("v")) return PoS.VERB;

                if (posTag.startsWith("ponct")) return PoS.PUNCTUATION_MARK;


                return PoS.SYMBOL;
        }
    }
}
