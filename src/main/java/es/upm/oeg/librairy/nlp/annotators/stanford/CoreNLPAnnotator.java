package es.upm.oeg.librairy.nlp.annotators.stanford;

import com.google.common.base.Strings;
import edu.mit.jmwe.data.IMWEDesc;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.JMWEAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.librairy.service.nlp.facade.utils.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public abstract class CoreNLPAnnotator implements StanfordAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNLPAnnotator.class);

    protected StanfordCoreNLP pipeline;

    protected Properties props;

    public CoreNLPAnnotator() {
        props = new Properties();
        //props.put("annotators", "tokenize, ssplit, pos, lemma, stopword, ner"); //"tokenize, ssplit, pos,

        props.put("annotators", "tokenize, ssplit, pos, lemma"); // ner, depparse, kbp

        // The rule-based SUTime and tokensregex NER is actually considerably slower than the statistical CRF NER.
        props.put("ner.applyNumericClassifiers","false"); //true
        props.put("ner.useSUTime","false"); //true

        props.put("ner.fine.regexner.validpospattern","^(NOUN|ADJ|PROPN).*");
        props.put("ner.fine.regexner.ignorecase","true");
        props.put("ner.fine.regexner.noDefaultOverwriteLabels","CITY,COUNTRY,STATE_OR_PROVINCE");

        props.put("kbp.model","none");

        props.put("entitylink.caseless","true");

        // Max length
        props.setProperty("parse.maxlen","100");

        // Custom sentence split
        props.setProperty("ssplit.boundaryTokenRegex", "[.]|[!?]+|[。]|[！？]+");

        // Custom tokenize
        props.setProperty("tokenize.options","untokenizable=noneDelete,normalizeOtherBrackets=false,normalizeParentheses=false");

        // Parallel
//        props.put("threads", ""+Runtime.getRuntime().availableProcessors());
    }

    public void initialize(){
        if (pipeline == null) {
            LOG.info("initializing pipeline");
            pipeline = new StanfordCoreNLP(props);

        }
    }

    abstract PoS translateFrom(String posTag);

    @Override
    public Annotation annotate(String text){
        initialize();
        // Create an empty Annotation just with the given text
        Annotation annotation = new Annotation(text);

        // run all Annotators on this text
        Instant start = Instant.now();
        pipeline.annotate(annotation);
        Instant end = Instant.now();
        LOG.debug("parsing elapsed time: " + Duration.between(start,end).toMillis() + "msecs");

        return annotation;
    }

    @Override
    public List<org.librairy.service.nlp.facade.model.Annotation> tokenize(Annotation annotation)
    {
        List<CoreMap> sentenceAnnotation = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        // Iterate over all of the sentences found
        List<org.librairy.service.nlp.facade.model.Annotation> unigramAnnotations = sentenceAnnotation
                .parallelStream()
                .flatMap(sentence -> sentence.get(CoreAnnotations.TokensAnnotation.class).stream())
                .map(coreLabel -> {
                    Token token = new Token();
                    String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    PoS posValue = translateFrom(pos.toLowerCase());
                    token.setPos(posValue);

                    String raw = coreLabel.originalText();
                    token.setTarget(raw);

                    String lemma = coreLabel.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                    token.setLemma(posValue.equals(PoS.PROPER_NOUN)? raw : lemma);

                    org.librairy.service.nlp.facade.model.Annotation tokenAnnotation = new org.librairy.service.nlp.facade.model.Annotation();
                    tokenAnnotation.setToken(token);
                    tokenAnnotation.setOffset(Long.valueOf(coreLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)));

                    return tokenAnnotation;
                })
                .filter(a -> (!Strings.isNullOrEmpty(a.getToken().getLemma())))
                .collect(Collectors.toList());

        List<org.librairy.service.nlp.facade.model.Annotation> multigramAnnotations = sentenceAnnotation
                .parallelStream()
                .flatMap(sentence -> sentence.containsKey(JMWEAnnotator.JMWEAnnotation.class) ? sentence.get(JMWEAnnotator.JMWEAnnotation.class).stream() : null)
                .filter(t -> t != null)
                .map(coreLabel -> {

                    IMWEDesc entry = coreLabel.getEntry();
                    Token token = new Token();

                    PoS pos = translateFrom(entry.getID().getPOS().name().toLowerCase());
                    token.setPos(pos);

                    String raw = coreLabel.getForm();
                    token.setTarget(raw);

                    String lemma = entry.getID().getForm().toLowerCase();
                    token.setLemma(pos.equals(PoS.PROPER_NOUN)? raw : lemma);

                    org.librairy.service.nlp.facade.model.Annotation tokenAnnotation = new org.librairy.service.nlp.facade.model.Annotation();
                    tokenAnnotation.setToken(token);
                    tokenAnnotation.setOffset(coreLabel.getOffset());

                    return tokenAnnotation;
                })
                .filter(a -> (!Strings.isNullOrEmpty(a.getToken().getLemma())))
                .collect(Collectors.toList());


        List<org.librairy.service.nlp.facade.model.Annotation> annotations = multigramAnnotations.isEmpty()? unigramAnnotations : AnnotationUtils.merge(unigramAnnotations, multigramAnnotations);

        return annotations;
    }


}
