package es.upm.oeg.librairy.nlp.service;

import es.upm.oeg.librairy.nlp.annotators.wordnet.WordnetAnnotatorEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class WordnetService extends CoreNLPService{

    private static final Logger LOG = LoggerFactory.getLogger(WordnetService.class);

    public WordnetService(String resourceFolder) {
        this.annotators.put("en", new WordnetAnnotatorEN(resourceFolder));
    }

}
