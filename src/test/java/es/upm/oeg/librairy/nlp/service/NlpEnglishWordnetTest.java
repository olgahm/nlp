package es.upm.oeg.librairy.nlp.service;

import es.upm.oeg.librairy.nlp.service.annotator.CoreNLPService;
import es.upm.oeg.librairy.nlp.service.annotator.WordnetService;
import org.junit.Before;
import org.junit.Test;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class NlpEnglishWordnetTest {

    private static final Logger LOG = LoggerFactory.getLogger(NlpEnglishWordnetTest.class);

    CoreNLPService service;

    @Before
    public void setup(){
        service = new WordnetService("src/main/bin","en");
    }

    @Test
    public void annotation() throws IOException {

        String text = "Barack Hussein Obama II is an American attorney and politician who served as the 44th president of the United States from 2009 to 2017.";

        List<PoS> filter = Collections.emptyList();

        List<Annotation> annotations = service.annotations(text, filter);

        annotations.forEach(annotation -> System.out.println("Annotation: " + annotation));

    }

}
