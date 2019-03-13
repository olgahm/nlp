package es.upm.oeg.librairy.nlp.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class NlpEsServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(NlpEsServiceTest.class);

    CoreNLPService service;

    @Before
    public void setup(){
        service = new CoreNLPService();
    }

    @Test
    public void annotation() throws IOException {

        String text = "Este es mi primer ejemplo";

        List<PoS> filter = Collections.emptyList();

        List<Annotation> annotations = service.annotations(text, filter, "es");

        Assert.assertEquals(5, annotations.size());

        annotations.forEach(annotation -> System.out.println("Annotation: " + annotation));

        List<String> pos = annotations.stream().map(a -> a.getToken().getPos().name()).collect(Collectors.toList());

        Assert.assertArrayEquals(new String[]{PoS.PRONOUN.name(),PoS.VERB.name(),PoS.ARTICLE.name(),PoS.ADJECTIVE.name(),PoS.NOUN.name()}, pos.toArray());

    }

}
