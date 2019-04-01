package es.upm.oeg.librairy.nlp.service;

import es.upm.oeg.librairy.nlp.service.annotator.CoreNLPService;
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

public class NlpEnglishTest {

    private static final Logger LOG = LoggerFactory.getLogger(NlpEnglishTest.class);

    CoreNLPService service;

    @Before
    public void setup(){
        service = new CoreNLPService("en");
    }

    @Test
    public void annotation() throws IOException {

        String text = "This is my first example about computers. She looked up the world record and looked up again.";

        List<PoS> filter = Collections.emptyList();

        List<Annotation> annotations = service.annotations(text, filter);

        annotations.forEach(annotation -> System.out.println("Annotation: " + annotation));

    }

}
