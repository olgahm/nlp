package es.upm.oeg.librairy.nlp.service.annotator;

import es.upm.oeg.librairy.nlp.annotators.dbpedia.DBpediaAnnotator;
import es.upm.oeg.librairy.nlp.annotators.dbpedia.DBpediaRestAnnotator;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DBpediaService  implements AnnotatorService {

    private static final Logger LOG = LoggerFactory.getLogger(DBpediaService.class);

    private final String lang;

    DBpediaAnnotator annotator;

    public DBpediaService(String endpoint, Double threshold, String lang, Boolean multigrams, Boolean references, AnnotatorService baseAnnotator) {

        this.lang = lang.toLowerCase();

        annotator = new DBpediaRestAnnotator(endpoint.replace("%%",this.lang), threshold, multigrams, references, baseAnnotator);

        LOG.debug("DBpedia Service initialized");
    }

    @Override
    public String tokens(String text, List<PoS> filter, Form form) {
        return text;
    }

    @Override
    public List<Annotation> annotations(String text, List<PoS> filter) {

        List<Annotation> annotations = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(\\.|.$)",Pattern.MULTILINE).matcher(text);
        int groupIndex = 0;
        while (matcher.find()){
            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            List<Annotation> partialAnnotations = annotator.annotate(partialContent, filter);
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
