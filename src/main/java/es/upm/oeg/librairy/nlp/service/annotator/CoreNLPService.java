package es.upm.oeg.librairy.nlp.service.annotator;

import com.google.common.base.Strings;
import edu.stanford.nlp.pipeline.Annotation;
import es.upm.oeg.librairy.nlp.annotators.stanford.*;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CoreNLPService implements AnnotatorService{

    private static final Logger LOG = LoggerFactory.getLogger(CoreNLPService.class);
    private final String lang;

    protected StanfordAnnotator annotator;

    public CoreNLPService(String lang) {
        this.lang = lang.toLowerCase();
        switch (this.lang){
            case "en":
                annotator = new StanfordAnnotatorEN();
                break;
            case "es":
                annotator = new StanfordAnnotatorES();
                break;
            case "de":
                annotator = new StanfordAnnotatorDE();
                break;
            case "fr":
                annotator = new StanfordAnnotatorFR();
                break;
        }
    }

    public String tokens(String text, List<PoS> filter, Form form) {
        if (Strings.isNullOrEmpty(text)) return "";

        return annotations(text, filter).stream()
                .map(annotation -> (form.equals(Form.LEMMA) ? annotation.getToken().getLemma() : annotation.getToken().getTarget()))
                .collect(Collectors.joining(" "));
    }

    public List<org.librairy.service.nlp.facade.model.Annotation> annotations(String text, List<PoS> filter){
        if (Strings.isNullOrEmpty(text)) return Collections.emptyList();

        List<org.librairy.service.nlp.facade.model.Annotation> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(\\.|.$)", Pattern.MULTILINE).matcher(text);
        int groupIndex = 0;
        while (matcher.find()){

            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            Annotation annotation = annotator.annotate(partialContent);
            Instant endAnnotation = Instant.now();
            LOG.debug("Annotated by CoreNLP in: " +
                    ChronoUnit.MINUTES.between(startAnnotation,endAnnotation) + "min " +
                    (ChronoUnit.SECONDS.between(startAnnotation,endAnnotation)%60) + "secs '" + partialContent + "'");

            try{
                Instant startTokenizer = Instant.now();
                List<org.librairy.service.nlp.facade.model.Annotation> annotations = annotator.tokenize(annotation);
                Instant endTokenizer = Instant.now();
                LOG.debug("Parsed  into " + annotations.size() + " annotations  in: " +
                        ChronoUnit.MINUTES.between(startTokenizer,endTokenizer) + "min " + (ChronoUnit.SECONDS.between(startTokenizer,endTokenizer)%60) + "secs");
                for(org.librairy.service.nlp.facade.model.Annotation a: annotations){
                    a.setOffset((groupIndex*1000)+a.getOffset());
                }
                tokens.addAll(annotations);
            }catch (Exception e){
                LOG.error("Error tokenizing", e);
            }
            groupIndex++;

        }
        if (filter.isEmpty()) return tokens;
        return tokens.stream()
                .filter(annotation -> filter.contains(annotation.getToken().getPos())).collect(Collectors.toList());
    }
}
