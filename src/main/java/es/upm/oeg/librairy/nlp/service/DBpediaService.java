package es.upm.oeg.librairy.nlp.service;

import es.upm.oeg.librairy.nlp.annotators.dbpedia.DBpediaAnnotator;
import es.upm.oeg.librairy.nlp.annotators.dbpedia.DBpediaRestAnnotator;
import org.librairy.service.nlp.facade.model.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DBpediaService  {

    private static final Logger LOG = LoggerFactory.getLogger(DBpediaService.class);

    Map<String, DBpediaAnnotator> annotators = new ConcurrentHashMap<>();

    public DBpediaService(String endpoint, Double threshold) {

        this.annotators.put("es", new DBpediaRestAnnotator(endpoint.replace("%%","es"), threshold));
        this.annotators.put("en", new DBpediaRestAnnotator(endpoint.replace("%%","en"), threshold));

        LOG.debug("DBpedia Service initialized");
    }

    public List<Annotation> annotations (String text, String lang){

        if (!annotators.containsKey(lang.toLowerCase())){
            LOG.warn("language '" + lang + "' not supported");
            return Collections.emptyList();
        }

        DBpediaAnnotator annotator = annotators.get(lang.toLowerCase());

        List<Annotation> annotations = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(\\.|.$)",Pattern.MULTILINE).matcher(text);
        int groupIndex = 0;
        while (matcher.find()){
            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            List<Annotation> partialAnnotations = annotator.annotate(partialContent);
            for(Annotation annotation : partialAnnotations){
                annotation.setOffset((groupIndex*1000)+annotation.getOffset());
            }
            annotations.addAll(partialAnnotations);
            Instant endAnnotation = Instant.now();
            LOG.debug("Annotated  in: " +
                    ChronoUnit.MINUTES.between(startAnnotation,endAnnotation) + "min " +
                    (ChronoUnit.SECONDS.between(startAnnotation,endAnnotation)%60) + "secs");
            groupIndex++;
        }
        return annotations;
    }

}
