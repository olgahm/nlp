package es.upm.oeg.librairy.nlp.avro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.AvroRemoteException;
import org.junit.Test;
import org.junit.runner.RunWith;
import es.upm.oeg.librairy.nlp.controllers.AvroController;
import es.upm.oeg.librairy.nlp.service.NLPServiceImpl;
import es.upm.oeg.librairy.nlp.service.ServiceManager;
import org.librairy.service.nlp.facade.AvroClient;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.rest.model.TokensRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AvroController.class,NLPServiceImpl.class, ServiceManager.class})
@WebAppConfiguration
public class AvroIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(AvroIntTest.class);

    @Test
    public void processTest() throws InterruptedException, IOException {

        AvroClient client = new AvroClient();


        String host     = "localhost";
        Integer port    = 65111;

        client.open(host,port);

        String text = "texto de prueba";

        String result = client.tokens(text, Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB, PoS.ADVERB, PoS.ADJECTIVE}), Form.LEMMA, false, "es");

        LOG.info("Result: " + result);

        client.close();

        System.out.println("Done!");
    }

    @Test
    public void annotateTest() throws InterruptedException, IOException {


        AvroClient client = new AvroClient();


        String host     = "localhost";
        Integer port    = 65111;

        client.open(host,port);

        List<String> texts = Arrays.asList(new String[]{
                "este sería un primer ejemplo",
                "aquí ya estamos tratando un segundo ejemplo",
                "con este terminamos. Es el último ejemplo"});


        texts.forEach(text -> {
            try {
                client.annotations(text, Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB, PoS.ADVERB, PoS.ADJECTIVE}), false, false, "es");
            } catch (AvroRemoteException e) {
                e.printStackTrace();
            }
        });

        client.close();
    }

    @Test
    public void jsonFormat() throws JsonProcessingException {

        List<PoS> types = Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB});
        String text = "texto";
        Form form = Form.LEMMA;
        TokensRequest req = new TokensRequest(text,types,form, false, "es");

        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper.writeValueAsString(req));
    }
}
