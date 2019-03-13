package es.upm.oeg.librairy.nlp.annotators.ixa;

import ixa.kaflib.KAFDocument;

import java.util.Properties;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface IXAAnnotator {

    Properties getConf();

    String getModel();

    void annotate(KAFDocument kafDocument);
}
