package dev.cjo.starter.ms.syndtarget;

import dev.cjo.starter.ms.syndtarget.service.impl.InvalidInputException;
import dev.cjo.starter.ms.syndtarget.service.impl.PermissionDeniedException;
import dev.cjo.starter.ms.syndtarget.service.impl.TargetNotFoundException;
import dev.cjo.starter.ms.syndtarget.service.model.*;

import dev.cjo.starter.ms.syndtarget.service.impl.SyndTargetService;
//import dev.cjo.starter.ms.syndtarget.service.model.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/*
 Controller for rest api


Test
curl -v -X POST -H "Content-Type: application/json" http://localhost:9091/v1/synd/dev-static/targets -d '
{
"name":"Curl-1",
"collectionName": "dev-curl",
"type": "FACEBOOK",
"disabled": false
}
authToken: null,
authStatus: null,
authenticatedDate: null,
updateDate: null,
 */

@RestController
@RequestMapping("v1/synd/{collectionName}")
@Api( value = "API for asset syndication to external destination, like social media, http url." )
public class Controller {

    @Value("${application.name:default}")
    private String applicationName;


    @Autowired
    private SyndTargetService myService;

    //todo
    @GetMapping("/targets")
    public List<SyndicationTarget> listAllTargets(
            @PathVariable  String collectionName
            //String sourceId
    )
    {

        return myService.listAllTargets(collectionName);
    }

    @GetMapping("/targets/{targetId}")
    public SyndicationTarget findTarget(
            @PathVariable  String collectionName,
            @PathVariable String targetId) throws PermissionDeniedException {
        //return ResponseEntity.ok("resource  updated");
        return myService.findTarget(collectionName, targetId);


    }

    @PostMapping("/targets")
    @ResponseStatus(HttpStatus.CREATED)
    public SyndicationTarget addTarget(
            @PathVariable  String collectionName,
            @RequestBody SyndicationTarget sTarget) {
        return myService.addTarget(collectionName, sTarget);
    }

    @DeleteMapping("/targets/{targetId}")
    public void deleteTarget(
            @PathVariable  String collectionName,
            @PathVariable String targetId) {
        myService.deleteTarget(collectionName, targetId);
    }

    @PutMapping("/targets/{targetId}")
    public SyndicationTarget updateTarget(
            @PathVariable  String collectionName,
            @PathVariable String targetId,
            @RequestBody SyndicationTarget sTarget) throws TargetNotFoundException {
        return myService.updateTarget(collectionName, targetId, sTarget);
    }






    /*to use custom response fields, ErrorInfo*/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidInputException.class)
    @ResponseBody
    ErrorInfo
    handleInvalidRequest(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }

}

