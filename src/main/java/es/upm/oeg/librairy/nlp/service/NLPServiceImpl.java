package es.upm.oeg.librairy.nlp.service;

import com.google.common.base.CharMatcher;
import es.upm.oeg.librairy.nlp.service.annotator.AnnotatorService;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.Group;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class NLPServiceImpl implements org.librairy.service.nlp.facade.model.NlpService {

    private static final Logger LOG = LoggerFactory.getLogger(NLPServiceImpl.class);

    @Autowired
    ServiceManager serviceManager;

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public String tokens(String text, List<PoS> filter, Form form, boolean multigrams, String lang) throws AvroRemoteException {
        List<Annotation> annotations = annotate(text, filter, multigrams, false, lang);

        return annotations.stream().map(a -> form.equals(Form.LEMMA)? a.getToken().getLemma() : a.getToken().getTarget().contains(" ")? a.getToken().getLemma() : a.getToken().getTarget()).collect(Collectors.joining(" "));
    }

    @Override
    public List<Annotation> annotations(String text, List<PoS> filter, boolean multigrams, boolean references, String lang) throws AvroRemoteException {

        List<Annotation> annotations = annotate(text, filter, multigrams, references, lang);

        return annotations.stream().filter(a -> CharMatcher.javaLetterOrDigit().matchesAnyOf(a.getToken().getLemma())).collect(Collectors.toList());
    }

    @Override
    public List<Group> groups(String text, List<PoS> filter, boolean multigrams, boolean references, String lang) throws AvroRemoteException {

        List<Annotation> annotations = annotate(text, filter, multigrams, references, lang);

        Map<Annotation, Long> grouped = annotations.stream().filter(a -> CharMatcher.javaLetterOrDigit().matchesAnyOf(a.getToken().getLemma())).collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        return grouped.entrySet().stream().map( entry -> {
            org.librairy.service.nlp.facade.model.Annotation annotation = entry.getKey();
            Group group = new Group();
            group.setToken(annotation.getToken().getLemma());
            group.setUri(annotation.getUri());
            group.setPos(annotation.getToken().getPos());
            group.setFreq(entry.getValue());
            return group;
        }).sorted((a,b) -> -a.getFreq().compareTo(b.getFreq())).collect(Collectors.toList());
    }

    private List<Annotation> annotate(String text, List<PoS> filter, boolean multigrams, Boolean references, String lang) throws AvroRemoteException {

        Thread thread = Thread.currentThread();

        AnnotatorService annotator = serviceManager.getAnnotator(thread, lang, multigrams, references);

        List<Annotation> annotations = annotator.annotations(text,filter);

        return annotations;
    }
}