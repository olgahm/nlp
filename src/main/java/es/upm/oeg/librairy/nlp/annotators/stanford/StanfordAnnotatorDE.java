package es.upm.oeg.librairy.nlp.annotators.stanford;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordAnnotatorDE extends CoreNLPAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(StanfordAnnotatorDE.class);

    protected StanfordCoreNLP pipeline;

    public StanfordAnnotatorDE() {
        props.put("tokenize.language","de");

        props.put("pos.model","edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger");

        props.put("parse.model","edu/stanford/nlp/models/lexparser/germanFactored.ser.gz");

        props.put("depparse.model","edu/stanford/nlp/models/parser/nndep/UD_German.gz");
        props.put("depparse.language","german");
    }

    // STTS Tag: https://www.ims.uni-stuttgart.de/forschung/ressourcen/lexika/TagSets/stts-table.html
    protected PoS translateFrom(String posTag){
        switch(posTag){
            case "adjective": return PoS.ADJECTIVE;
            case "proper_noun": return PoS.NOUN;
            case "adverb": return PoS.ADVERB;
            case "verb": return PoS.VERB;
            default:
                // Ancora annotation
                if (posTag.startsWith("adja")) return PoS.ADJECTIVE;
                if (posTag.startsWith("adjd")) return PoS.ADJECTIVE;

                if (posTag.startsWith("adv")) return PoS.ADVERB;

                if (posTag.startsWith("ap")) return PoS.PREPOSITION;

                if (posTag.startsWith("art")) return PoS.ARTICLE;

                if (posTag.startsWith("card")) return PoS.NUMBER;

                if (posTag.startsWith("it")) return PoS.INTERJECTION;

                if (posTag.startsWith("ko")) return PoS.CONJUNCTION;

                if (posTag.startsWith("nn")) return PoS.NOUN;
                if (posTag.startsWith("ne")) return PoS.PROPER_NOUN;


                if (posTag.startsWith("p")) return PoS.PRONOUN;


                if (posTag.startsWith("v")) return PoS.VERB;

                return PoS.SYMBOL;
        }
    }
}
