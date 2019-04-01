package es.upm.oeg.librairy.nlp.annotators.ixa;

import com.google.common.io.Files;
import es.upm.oeg.librairy.nlp.service.annotator.IXAService;
import eus.ixa.ixa.pipe.pos.Annotate;
import ixa.kaflib.KAFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class IXAAnnotatorES implements IXAAnnotator{

    private static final Logger LOG = LoggerFactory.getLogger(IXAAnnotatorES.class);

    String model              ;
    String lemmatizerModel    ;
    String language           ;
    String multiwords         ;
    String dictag             ;
    String kafVersion         ;
    String normalize          ;
    String untokenizable      ;
    String hardParagraph      ;
    String noseg              ;

    private Properties annotateProperties;

    private Annotate posAnnotator;

    public IXAAnnotatorES(String resourceFolder) {
        model              = Paths.get(resourceFolder,"morph-models-"+ IXAService.kafVersion+"/es/es-pos-perceptron-autodict01-ancora-2.0.bin").toFile().getAbsolutePath();
        lemmatizerModel    = Paths.get(resourceFolder,"morph-models-"+ IXAService.kafVersion+"/es/es-lemma-perceptron-ancora-2.0.bin").toFile().getAbsolutePath();
        language           = "es";
        multiwords         = "false"; // false
        dictag             = Paths.get(resourceFolder,"tag","es").toFile().getAbsolutePath();
        normalize          = "false"; // true
        untokenizable      = "false";
        hardParagraph      = "false";
        noseg              = "false"; // false


        this.annotateProperties = new Properties();

        annotateProperties.setProperty("normalize", normalize);
        annotateProperties.setProperty("untokenizable", untokenizable);
        annotateProperties.setProperty("hardParagraph", hardParagraph);
        annotateProperties.setProperty("noseg",noseg);
        annotateProperties.setProperty("model", model);
        annotateProperties.setProperty("lemmatizerModel", lemmatizerModel);
        annotateProperties.setProperty("language", language);
        annotateProperties.setProperty("multiwords", multiwords);
        annotateProperties.setProperty("dictTag", dictag);
        annotateProperties.setProperty("dictPath", dictag);
        annotateProperties.setProperty("ruleBasedOption", dictag);

        try {
            this.posAnnotator    = new Annotate(annotateProperties);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing ixa service",e);
        }
    }

    @Override
    public Properties getConf() {
        return annotateProperties;
    }

    @Override
    public String getModel() {
        return Files.getNameWithoutExtension(model);
    }

    @Override
    public void annotate(KAFDocument kafDocument) {
        LOG.debug("annotating document...");
        posAnnotator.annotatePOSToKAF(kafDocument);
    }
}
