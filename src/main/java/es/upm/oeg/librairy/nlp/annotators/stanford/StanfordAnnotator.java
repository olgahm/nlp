package es.upm.oeg.librairy.nlp.annotators.stanford;

import edu.stanford.nlp.pipeline.Annotation;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface StanfordAnnotator {

    Annotation annotate(String text);

    List<org.librairy.service.nlp.facade.model.Annotation> tokenize(Annotation annotation);
}
