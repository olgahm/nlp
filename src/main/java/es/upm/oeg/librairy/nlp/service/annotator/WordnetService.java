package es.upm.oeg.librairy.nlp.service.annotator;

import es.upm.oeg.librairy.nlp.annotators.wordnet.WordnetAnnotatorEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class WordnetService extends CoreNLPService{

    private static final Logger LOG = LoggerFactory.getLogger(WordnetService.class);

    private final String lang;

    public WordnetService(String resourceFolder, String lang) {
        super("--");
        this.lang = lang.toLowerCase();
        annotator =  new WordnetAnnotatorEN(resourceFolder);
    }

}
