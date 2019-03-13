package es.upm.oeg.librairy.nlp.annotators.dbpedia;

import es.upm.oeg.librairy.nlp.client.SpotlightClient;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DBpediaRestAnnotator implements DBpediaAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(DBpediaRestAnnotator.class);

    private final String endpoint;
    private final Double threshold;

    private SpotlightClient client;

    public DBpediaRestAnnotator(String endpoint, Double threshold) {
        this.endpoint = endpoint;
        this.threshold = threshold;
        this.client = new SpotlightClient();
        LOG.debug("DBpedia Service linked to '" + endpoint + "'");
    }

    @Override
    public List<Annotation> annotate(String text){
        try{
            List<Annotation> annotations = new ArrayList<>();

            Map<String,String> params = new HashMap<>();
            params.put("policy","whitelist");
            params.put("text", text);
            params.put("confidence","0.2");
            params.put("support","20");
            String url = endpoint + "/annotate";

            Document response = client.request(url,params);

            NodeList resources = response.getElementsByTagName("Resource");

            for(int i = 0; i < resources.getLength(); i++){

                Node resource = resources.item(i);

                if (resource.getNodeType() == Node.ELEMENT_NODE){

                    Element element = (Element) resource;

                    String score    = element.getAttribute("similarityScore");

                    if (Double.valueOf(score) > threshold){
                        String uri      = element.getAttribute("URI");
                        String target   = element.getAttribute("surfaceForm");
                        String offset   = element.getAttribute("offset");

                        Token token = new Token();
                        token.setTarget(target);
                        token.setLemma(target.toLowerCase().replace(" ","_"));
                        if (target.contains(" ")) token.setPos(PoS.NOUN);

                        Annotation annotation = new Annotation();
                        annotation.setToken(token);
                        annotation.setUri(uri);
                        annotation.setOffset(Long.valueOf(offset));
                        annotations.add(annotation);
                    }

                }
            }
            return annotations;
        }catch (Exception e){
            LOG.error("Unexpected error from DBpedia Spotlight service",e);
            return Collections.emptyList();
        }


    }

}

