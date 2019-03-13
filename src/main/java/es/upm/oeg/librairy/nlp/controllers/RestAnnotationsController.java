package es.upm.oeg.librairy.nlp.controllers;

import com.google.common.base.Strings;
import es.upm.oeg.librairy.nlp.service.LanguageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.NlpService;
import org.librairy.service.nlp.facade.rest.model.Annotation;
import org.librairy.service.nlp.facade.rest.model.AnnotationsRequest;
import org.librairy.service.nlp.facade.rest.model.AnnotationsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RestController
@RequestMapping("/annotations")
@Api(tags="/annotations", description="handle annotations from a text")
public class RestAnnotationsController {

    private static final Logger LOG = LoggerFactory.getLogger(RestAnnotationsController.class);

    @Autowired
    NlpService service;

    @Autowired
    LanguageService languageService;

    @PostConstruct
    public void setup(){

    }

    @PreDestroy
    public void destroy(){

    }

    @ApiOperation(value = "create annotations from a given text", nickname = "postAnnotate", response=AnnotationsResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = AnnotationsResult.class),
    })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<AnnotationsResult> analyze(@RequestBody AnnotationsRequest request)  {
        try {
            if (Strings.isNullOrEmpty(request.getText())) return new ResponseEntity("Empty text", HttpStatus.BAD_REQUEST);

            String lang = Strings.isNullOrEmpty(request.getLang())? languageService.getLanguage(request.getText()) : request.getLang();

            List<Annotation> annotations = service.annotations(request.getText(), request.getFilter(), request.getMultigrams(), request.getReferences(), lang).stream().map(a -> new Annotation(a)).collect(Collectors.toList());
            return new ResponseEntity(new AnnotationsResult(annotations), HttpStatus.OK);
        } catch (AvroRemoteException e) {
            return new ResponseEntity("internal service seems down", HttpStatus.FAILED_DEPENDENCY);
        } catch (Exception e){
            return new ResponseEntity("internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

