package es.upm.oeg.librairy.nlp.service;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import es.upm.oeg.librairy.nlp.annotators.ixa.*;
import eus.ixa.ixa.pipe.pos.CLI;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
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
public class IXAService  {

    private static final Logger LOG = LoggerFactory.getLogger(IXAService.class);

    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    public static final String kafVersion         = "1.5.0";

    Map<String,IXAAnnotator> annotators = new ConcurrentHashMap<>();

    public IXAService(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    public void setup() {

        annotators.put("es", new IXAAnnotatorES(resourceFolder));
        annotators.put("en", new IXAAnnotatorEN(resourceFolder));
        annotators.put("fr", new IXAAnnotatorFR(resourceFolder));
        annotators.put("de", new IXAAnnotatorDE(resourceFolder));

    }

    public String tokens(String text, List<PoS> filter, Form form, String lang) throws AvroRemoteException {

        return analyze(text,filter, lang).stream()
                .map(term-> {
                    switch (form){
                        case LEMMA: return Strings.isNullOrEmpty(term.getLemma())? term.getStr() : term.getLemma().toLowerCase();
                        default: return term.getStr().toLowerCase();
                    }
                })
                .collect(Collectors.joining(" "));
    }


    public List<Annotation> annotations(String text, List<PoS> filter, String lang) throws AvroRemoteException {
        List<Term> terms = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(\\.|.$)",Pattern.MULTILINE).matcher(text);
        while (matcher.find()){
            terms.addAll(analyze(text, filter, lang));
        }
        return terms.stream()
                .map(term -> {

                    Token token = new Token();
                    token.setTarget(term.getStr());
                    token.setLemma(!Strings.isNullOrEmpty(term.getLemma())?term.getLemma():"");

                    if ((CharMatcher.javaLetter().matchesAllOf(term.getStr())) && CharMatcher.javaDigit().matchesAllOf(term.getLemma())){
                        // special case:
                        // target = first
                        // lemma = 1
                        token.setLemma(term.getStr());
                    }

                    token.setMorphoFeat(!Strings.isNullOrEmpty(term.getMorphofeat())?term.getMorphofeat():"");
                    token.setPos(!Strings.isNullOrEmpty(term.getPos())? IXAPoSTranslator.toPoSTag(term.getPos()):PoS.NOUN);
                    token.setType(!Strings.isNullOrEmpty(term.getType())?term.getType():"");

                    Annotation annotation = new Annotation();
                    if (term.getSentiment() != null) annotation.setSentiment(term.getSentiment().getPolarity());
                    annotation.setToken(token);
                    annotation.setOffset(Long.valueOf(term.getSpan().getTargets().get(0).getOffset()));

                    return annotation;
                })
                .collect(Collectors.toList());
    }

    private List<Term> analyze(String text, List<PoS> filter, String lang){
        List<Term> terms = Collections.emptyList();

        if (!annotators.containsKey(lang.toLowerCase())){
            LOG.warn("language '" + lang + "' not supported");
            return terms;
        }
        final KAFDocument kaf;
        try {

            InputStream is = new ByteArrayInputStream(text.getBytes());

            BufferedReader breader = new BufferedReader(new InputStreamReader(is));

            kaf = new KAFDocument(lang, kafVersion);

            IXAAnnotator annotator = annotators.get(lang.toLowerCase());


            final String version        = CLI.class.getPackage().getImplementationVersion();
            final String commit         = CLI.class.getPackage().getSpecificationVersion();

            final eus.ixa.ixa.pipe.tok.Annotate tokAnnotator = new eus.ixa.ixa.pipe.tok.Annotate(breader, annotator.getConf());

            // Tokenization
            tokAnnotator.tokenizeToKAF(kaf);


            // PosTagging

            final KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + annotator.getModel(), version + "-" + commit);
            newLp.setBeginTimestamp();
            annotator.annotate(kaf);

            // Filtering
            List<String> postags = filter.stream().flatMap( type -> IXAPoSTranslator.toTermPoS(type).stream()).collect(Collectors.toList());

            terms = kaf.getAnnotations(KAFDocument.AnnotationType.TERM).stream()
                    .map(annotation -> (Term) annotation)
                    .filter(term -> postags.isEmpty() || postags.contains(term.getPos()))
                    .collect(Collectors.toList());

            breader.close();
        } catch (IOException e) {
            LOG.error("Error analyzing text", e);
        }

        return terms;
    }
}
