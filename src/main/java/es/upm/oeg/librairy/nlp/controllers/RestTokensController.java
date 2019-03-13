package es.upm.oeg.librairy.nlp.controllers;

import com.google.common.base.Strings;
import es.upm.oeg.librairy.nlp.service.LanguageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.NlpService;
import org.librairy.service.nlp.facade.rest.model.TokensRequest;
import org.librairy.service.nlp.facade.rest.model.TokensResult;
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

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RestController
@RequestMapping("/tokens")
@Api(tags = "/tokens", description = "handle tokens from a text")
public class RestTokensController {

    private static final Logger LOG = LoggerFactory.getLogger(RestTokensController.class);

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

    @ApiOperation(value = "create tokens from a given text", nickname = "postProcess", response=TokensResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = TokensResult.class),
    })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<TokensResult> analyze(@RequestBody TokensRequest request)  {
        try {

            if (Strings.isNullOrEmpty(request.getText())) return new ResponseEntity("Empty text", HttpStatus.BAD_REQUEST);

            String lang = Strings.isNullOrEmpty(request.getLang())? languageService.getLanguage(request.getText()) : request.getLang();

            return new ResponseEntity(new TokensResult(service.tokens(request.getText(), request.getFilter(), request.getForm(), request.getMultigrams(), lang)), HttpStatus.OK);
        } catch (AvroRemoteException e) {
            return new ResponseEntity("internal service seems down", HttpStatus.FAILED_DEPENDENCY);
        } catch (Exception e){
            return new ResponseEntity("internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}