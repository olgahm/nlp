package es.upm.oeg.librairy.nlp.controllers;

import org.librairy.service.nlp.facade.AvroServer;
import org.librairy.service.nlp.facade.model.NlpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class AvroController {

    private static final Logger LOG = LoggerFactory.getLogger(AvroController.class);

    @Autowired
    NlpService service;

    @Value("#{environment['AVRO_PORT']?:${nlp.avro.port}}")
    Integer port;

    @Value("#{environment['AVRO_HOST']?:'${nlp.avro.host}'}")
    String host;

    private AvroServer server;

    @PostConstruct
    public void setup() throws IOException {
        server = new AvroServer(service);
        server.open(host,port);
    }

    @PreDestroy
    public void destroy(){
        if (server != null) server.close();
    }

}
