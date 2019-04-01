package es.upm.oeg.librairy.nlp.service;

import es.upm.oeg.librairy.nlp.service.annotator.CoreNLPService;
import es.upm.oeg.librairy.nlp.service.annotator.IXAService;
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

public class NlpSpanishTest {

    private static final Logger LOG = LoggerFactory.getLogger(NlpSpanishTest.class);

    CoreNLPService coreNLPService;
    private IXAService ixaService;

    @Before
    public void setup(){

        coreNLPService = new CoreNLPService("es");
        ixaService      = new IXAService("src/main/bin","es",false);
    }

    @Test
    public void annotation() throws IOException {

        String text = "Este es mi primer ejemplo sobre computadores";

        List<PoS> filter = Collections.emptyList();

        List<Annotation> annotations = coreNLPService.annotations(text, filter);

        annotations.forEach(annotation -> System.out.println("Annotation: " + annotation));


        List<Annotation> annotations2 = ixaService.annotations(text, filter);
        annotations2.forEach(annotation -> System.out.println("Annotation2: " + annotation));


    }

}
