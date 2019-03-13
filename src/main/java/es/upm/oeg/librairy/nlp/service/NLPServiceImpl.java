package es.upm.oeg.librairy.nlp.service;

import com.google.common.base.CharMatcher;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.Group;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.utils.AnnotationUtils;
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

        List<Annotation> initialAnnotations = annotate(text, filter, multigrams, references, lang);

        List<Annotation> annotations = references? addDBpediaReferences(initialAnnotations, text, multigrams, references, lang) : initialAnnotations;

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
        List<Annotation> annotations;
        if (multigrams){
            switch(lang.toLowerCase()){
                case "en":
                    annotations = serviceManager.getWordnetService(Thread.currentThread()).annotations(text, filter, lang);
                    break;
                case "es":
                    return addDBpediaReferences(serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter, lang), text, multigrams, references, lang);
                case "fr":
                    return addDBpediaReferences(serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter, lang), text, multigrams, references, lang);
                case "de":
                    return addDBpediaReferences(serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter, lang), text, multigrams, references, lang);
                default: throw new RuntimeException("Language '" + lang+"' not supported");
            }
        }else{
            switch(lang.toLowerCase()){
                case "en":
                    annotations = serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter, lang);
                    break;
                case "es":
                    annotations = serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter, lang);
                    break;
                case "fr":
                    annotations = serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter, lang);
                    break;
                case "de":
                    annotations = serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter, lang);
                    break;
                default: throw new RuntimeException("Language '" + lang+"' not supported");
            }
        }

        return references? addDBpediaReferences(annotations, text, multigrams, references, lang) : annotations;
    }

    private List<Annotation> addDBpediaReferences(List<Annotation> annotations, String text, Boolean multigrams, Boolean references, String lang){
        if (multigrams || references){
            List<Annotation> refAnnotations = serviceManager.getDBpediaService(Thread.currentThread()).annotations(text, lang);
            if (multigrams && references){
                //nothing to filter
            }else if (multigrams){
                refAnnotations = refAnnotations.stream().filter(a -> a.getToken().getTarget().contains(" ")).map(a -> {
                    a.setUri(null);
                    return a;
                }).collect(Collectors.toList());
            }else if (references){
                refAnnotations = refAnnotations.stream().filter(a -> !a.getToken().getTarget().contains(" ")).collect(Collectors.toList());
            }
            return AnnotationUtils.merge(annotations, refAnnotations).stream().filter(a -> a.getToken().getPos() != null).collect(Collectors.toList());
        }
        return annotations;
    }
}