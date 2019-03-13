package es.upm.oeg.librairy.nlp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Before;
import org.junit.Test;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.rest.model.AnnotationsRequest;
import org.librairy.service.nlp.facade.rest.model.GroupsRequest;
import org.librairy.service.nlp.facade.rest.model.TokensRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class RestIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(RestIntTest.class);

    @Before
    public void setup(){
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void tokens() throws UnirestException {

        List<PoS> types = Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB});
        String text = "este es el texto que va a ser analizado";
        Form form = Form.LEMMA;
        TokensRequest req = new TokensRequest(text,types,form,false,"es");

        HttpResponse<JsonNode> response = Unirest.post("http://librairy.linkeddata.es/es/tokens")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(req)
                .asJson();

        LOG.info("Response: " + response.getBody());


    }

    @Test
    public void annotations() throws UnirestException {

        List<PoS> types = Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB});
        String text = "este es el texto que va a ser analizado";
        AnnotationsRequest req = new AnnotationsRequest(text,types, false,false,"es");

        HttpResponse<JsonNode> response = Unirest.post("http://librairy.linkeddata.es/es/annotations")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(req)
                .asJson();

        LOG.info("Response: " + response.getBody());


    }

    @Test
    public void groups() throws UnirestException {

        List<PoS> types = Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB});
        String text = "este es el texto que va a ser analizado";
        GroupsRequest req = new GroupsRequest(text,types, false,false,"es");

        HttpResponse<JsonNode> response = Unirest.post("http://librairy.linkeddata.es/es/groups")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(req)
                .asJson();

        LOG.info("Response: " + response.getBody());


    }
}
