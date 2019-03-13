package es.upm.oeg.librairy.nlp.service;

import com.google.common.base.Strings;
import edu.stanford.nlp.pipeline.Annotation;
import es.upm.oeg.librairy.nlp.annotators.stanford.*;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CoreNLPService {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNLPService.class);

    protected Map<String,StanfordAnnotator> annotators = new ConcurrentHashMap<>();

    public CoreNLPService() {
        this.annotators.put("es", new StanfordAnnotatorES());
        this.annotators.put("en", new StanfordAnnotatorEN());
        this.annotators.put("fr", new StanfordAnnotatorFR());
        this.annotators.put("de", new StanfordAnnotatorDE());
    }

    public String tokens(String text, List<PoS> filter, Form form, String lang) throws AvroRemoteException {
        if (Strings.isNullOrEmpty(text)) return "";

        return annotations(text, filter, lang).stream()
                .map(annotation -> (form.equals(Form.LEMMA) ? annotation.getToken().getLemma() : annotation.getToken().getTarget()))
                .collect(Collectors.joining(" "));
    }

    public List<org.librairy.service.nlp.facade.model.Annotation> annotations(String text, List<PoS> filter, String lang) throws AvroRemoteException {
        if (Strings.isNullOrEmpty(text)) return Collections.emptyList();

        if (!annotators.containsKey(lang.toLowerCase())){
            LOG.warn("language '"+ lang + "' not supported");
            return Collections.emptyList();
        }

        StanfordAnnotator annotator = annotators.get(lang.toLowerCase());

        List<org.librairy.service.nlp.facade.model.Annotation> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(\\.|.$)", Pattern.MULTILINE).matcher(text);
        int groupIndex = 0;
        while (matcher.find()){

            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            Annotation annotation = annotator.annotate(partialContent);
            Instant endAnnotation = Instant.now();
            LOG.debug("Annotated  in: " +
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
