package es.upm.oeg.librairy.nlp.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import es.upm.oeg.librairy.nlp.service.annotator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class ServiceManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceManager.class);

    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    @Value("#{environment['SPOTLIGHT_ENDPOINT']?:'${spotlight.endpoint}'}")
    String endpoint;

    @Value("#{environment['SPOTLIGHT_THRESHOLD']?:${spotlight.threshold}}")
    Double threshold;

    @Value("#{environment['SERVER_THREADS']?:${server.tomcat.max-threads}}")
    Integer maxThreads;

    Integer numParallel;

    LoadingCache<Request,AnnotatorService> annotators;

    @PostConstruct
    public void setup(){

        Integer availableThreads = Runtime.getRuntime().availableProcessors();

        numParallel = Math.min(availableThreads, maxThreads);

        annotators = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Request, AnnotatorService>() {
                            public AnnotatorService load(Request req) throws ExecutionException {
                                try{
                                    LOG.info("Initializing Annotator Service for: " + req);
                                    String lang = req.getLang().toLowerCase();
                                    switch (lang){
                                        case "en":
                                            return (req.getMultigram())? new WordnetService(resourceFolder, lang) : new CoreNLPService(lang);
                                        case "es":
                                            return (req.getMultigram())? new DBpediaService(endpoint, threshold, lang, req.getMultigram(), req.references, new IXAService(resourceFolder,lang, req.getMultigram())) : new IXAService(resourceFolder,lang, req.getMultigram());
                                        case "de":
                                            return (req.getMultigram())? new DBpediaService(endpoint, threshold, lang, req.getMultigram(), req.references, new CoreNLPService(lang)) : new CoreNLPService(lang);
                                        case "fr":
                                            return (req.getMultigram())? new DBpediaService(endpoint, threshold, lang, req.getMultigram(), req.references, new CoreNLPService(lang)) : new CoreNLPService(lang);
                                        default:
                                            LOG.error("language '"+ lang + "' not supported");
                                            throw new RuntimeException("language not supported: " + lang);
                                    }
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });

    }

    public AnnotatorService getAnnotator(Thread thread, String language, Boolean multigram, Boolean references) {
        try {
            Long key = getKey(thread.getId());
            String lang = language.toLowerCase();
            return annotators.get(new Request(key,lang, multigram, references));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private Long getKey(Long id){

        return id % numParallel;
    }

    public void setResourceFolder(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public class Request{
        Long id;

        String lang;

        Boolean multigram;

        Boolean references;

        public Request(Long id, String lang, Boolean multigram, Boolean references) {
            this.id = id;
            this.lang = lang;
            this.multigram = multigram;
            this.references = references;
        }

        public Long getId() {
            return id;
        }

        public String getLang() {
            return lang;
        }

        public Boolean getMultigram() {
            return multigram;
        }

        public Boolean getReferences() {
            return references;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Request request = (Request) o;

            if (!id.equals(request.id)) return false;
            if (!lang.equals(request.lang)) return false;
            if (!multigram.equals(request.multigram)) return false;
            return references.equals(request.references);

        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + lang.hashCode();
            result = 31 * result + multigram.hashCode();
            result = 31 * result + references.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "id=" + id +
                    ", lang='" + lang + '\'' +
                    ", multigram=" + multigram +
                    ", references=" + references +
                    '}';
        }
    }

}

