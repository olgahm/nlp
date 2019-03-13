package es.upm.oeg.librairy.nlp.annotators.stanford;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordAnnotatorEN extends CoreNLPAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(StanfordAnnotatorEN.class);

    protected StanfordCoreNLP pipeline;

    public StanfordAnnotatorEN() {
        props.put("tokenize.language","en");
        props.put("ner.language","en");
        props.put("sutime.language","english");
        props.put("depparse.language","english");
        props.put("kbp.language","en");
    }

    protected PoS translateFrom(String posTag){
        switch(posTag){
            case "adjective": return PoS.ADJECTIVE;
            case "proper_noun": return PoS.PROPER_NOUN;
            case "adverb": return PoS.ADVERB;
            case "verb": return PoS.VERB;
            case "noun": return PoS.NOUN;
            default:
                // Treebank annotation
                if (posTag.startsWith("cc")) return PoS.CONJUNCTION;

                if (posTag.startsWith("cd")) return PoS.NUMBER;

                if (posTag.startsWith("dt")) return PoS.ARTICLE;

                if (posTag.startsWith("in")) return PoS.PREPOSITION;

                if (posTag.startsWith("jj")) return PoS.ADJECTIVE;

                if (posTag.startsWith("md")) return PoS.VERB;

                if (posTag.startsWith("nnp")) return PoS.PROPER_NOUN;
                if (posTag.startsWith("nn")) return PoS.NOUN;

                if (posTag.startsWith("pdt")) return PoS.ARTICLE;

                if (posTag.startsWith("po")) return PoS.PRONOUN;
                if (posTag.startsWith("pr")) return PoS.PRONOUN;

                if (posTag.startsWith("rb")) return PoS.ADVERB;

                if (posTag.startsWith("uh")) return PoS.INTERJECTION;

                if (posTag.startsWith("vb")) return PoS.VERB;

                return PoS.SYMBOL;
        }
    }
}
