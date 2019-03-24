package es.upm.oeg.librairy.nlp.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

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

    LoadingCache<Long, CoreNLPService> coreServices;

    LoadingCache<Long, IXAService> ixaModels;

    LoadingCache<Long, DBpediaService> dbpediaServices;

    LoadingCache<Long, CoreNLPService> wordnetServices;

    @PostConstruct
    public void setup(){
        ixaModels = CacheBuilder.newBuilder()
                .maximumSize(maxThreads)
                .build(
                        new CacheLoader<Long, IXAService>() {
                            public IXAService load(Long key) throws ExecutionException {
                                try{
                                    LOG.info("Initializing IXA service for thread: " + key);
                                    IXAService ixaService = new IXAService(resourceFolder);
                                    ixaService.setup();
                                    return ixaService;
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });

        coreServices = CacheBuilder.newBuilder()
                .maximumSize(maxThreads)
                .build(
                        new CacheLoader<Long, CoreNLPService>() {
                            public CoreNLPService load(Long key) throws ExecutionException {
                                LOG.info("Initializing CoreNLP service for thread: " + key);
                                try{
                                    return new CoreNLPService();
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });

        wordnetServices = CacheBuilder.newBuilder()
                .maximumSize(maxThreads)
                .build(
                        new CacheLoader<Long, CoreNLPService>() {
                            public CoreNLPService load(Long key) throws ExecutionException {
                                try{
                                    LOG.info("Initializing Wordnet-based CoreNLP service for thread: " + key);
                                    return new WordnetService(resourceFolder);
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });


        dbpediaServices = CacheBuilder.newBuilder()
                .maximumSize(maxThreads)
                .build(
                        new CacheLoader<Long, DBpediaService>() {
                            public DBpediaService load(Long key) throws ExecutionException {
                                try{
                                    LOG.info("Initializing DBpedia service for thread: " + key);
                                    return new DBpediaService(endpoint, threshold);
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });
    }

    public IXAService getIXAService(Thread thread) {
        try {
            Long key = getKey(thread.getId());
            return ixaModels.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CoreNLPService getCoreService(Thread thread) {

        try {
            Long key = getKey(thread.getId());
            return coreServices.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public CoreNLPService getWordnetService(Thread thread) {

        try {
            Long key = getKey(thread.getId());
            return wordnetServices.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public DBpediaService getDBpediaService(Thread thread) {

        try {
            Long key = getKey(thread.getId());
            return dbpediaServices.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Long getKey(Long id){
        return id % maxThreads;
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

}
