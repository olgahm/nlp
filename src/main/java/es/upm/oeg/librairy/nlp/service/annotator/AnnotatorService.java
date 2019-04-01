package es.upm.oeg.librairy.nlp.service.annotator;

import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface AnnotatorService {

    String tokens(String text, List<PoS> filter, Form form);

    List<Annotation> annotations(String text, List<PoS> filter);
}
