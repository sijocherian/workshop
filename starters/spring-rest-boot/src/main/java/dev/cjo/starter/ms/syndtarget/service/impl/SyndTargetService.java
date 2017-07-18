package dev.cjo.starter.ms.syndtarget.service.impl;

import dev.cjo.starter.ms.syndtarget.config.PersistenceConfig;
import dev.cjo.starter.ms.syndtarget.dao.SyndicationTargetRepository;
import dev.cjo.starter.ms.syndtarget.dao.model.IndexSchema;
import dev.cjo.starter.ms.syndtarget.dao.model.SyndTargetDoc;
import dev.cjo.starter.ms.syndtarget.service.model.SyndicationTarget;
import dev.cjo.starter.ms.syndtarget.service.model.Status;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Syndication Service main entry point
 *
 * @author sijocherian
 */

@Service
public class SyndTargetService {

    /* Exception handling though Rest controller.  Json output seen while testing:
    - Using @ExceptionHandler(InvalidInputException.class) annotation in controller
         Response HTTP/1.1 400  {"error":"Bad Request","detail":"nvalid input value for attribute name :  xyz "}

    - Runtime framework exception in enum translation
        Response HTTP/1.1 400  {"timestamp":"2017-07-14T19:26:43.480+0000","status":400,"error":"Bad Request","exception":"org.springframework.http.converter.HttpMessageNotReadableException","message":"Could not read JSON document: Can not deserialize value of type dev.cjo.starter.ms.syndtarget.dao.model.SyndTargetDoc$SyndicationType from String \"FACEBOOKd\": value not one of declared Enum instance names: [TWITTER, FACEBOOK]\n at [Source: java.io.PushbackInputStream@18095af9; line: 5, column: 9] (through reference chain: dev.cjo.starter.ms.syndtarget.service.model.SyndicationTarget[\"type\"]); nested exception is com.fasterxml.jackson.databind.exc.InvalidFormatException: Can not deserialize value of type dev.cjo.starter.ms.syndtarget.dao.model.SyndTargetDoc$SyndicationType from String \"FACEBOOKd\": value not one of declared Enum instance names: [TWITTER, FACEBOOK]\n at [Source: java.io.PushbackInputStream@18095af9; line: 5, column: 9] (through reference chain: dev.cjo.starter.ms.syndtarget.service.model.SyndicationTarget[\"type\"])","path":"/v1/syndication/dev-static/targets/AV1B6PbeGeENTs8NVt0L"}

     - Explicit throwing exception class with @ResponseStatus on exception
        Response  HTTP/1.1 404 {"timestamp":"2017-07-14T19:24:00.004+0000","status":404,"error":"Not Found","exception":"dev.cjo.starter.ms.syndtarget.service.impl.TargetNotFoundException","message":"Existing Target not found, id:AV1B6PbeGeENTs8NVt0Lamol","path":"/v1/syndication/dev-static/targets/AV1B6PbeGeENTs8NVt0Lamol"}
      */

    public List<SyndicationTarget> listAllTargets(String collectionName) {
        List<SyndicationTarget> ret  = new ArrayList<>();

        //todo authorize the site & user
        List<SyndTargetDoc> found = syndTargetRepository.findAllByCollectionName(collectionName);
        StringBuilder sb = new StringBuilder(found.size() + " " );
        for(SyndTargetDoc d : found) {
            ret.add( d.toServiceObj() );
            sb.append(d.getName()).append(", ");
        }
        LOGGER.info(collectionName + "; Returning targets: "+ sb.toString() );



        return ret;
    }

    public SyndicationTarget findTarget(String collectionName, String targetId) throws PermissionDeniedException { //todo ServiceException.NotFoundException
        SyndTargetDoc found = syndTargetRepository.findOne(targetId);
        if(found==null)
            return null;
        else {
            if(!found.getCollectionName().equalsIgnoreCase( collectionName))
                throw new PermissionDeniedException(" unauthorized");

            return found.toServiceObj();
        }

    }

    public List<SyndicationTarget> findAllBySyndicationType(String collectionName, IndexSchema.SyndicationType type) throws PermissionDeniedException { //todo ServiceException.NotFoundException

        List<SyndicationTarget> ret  = new ArrayList<>();

        //Query being derived from the method names
        List<SyndTargetDoc> found = syndTargetRepository.queryByCollectionNameAndType(collectionName, type);

        StringBuilder sb = new StringBuilder(found.size() + " " );
        for(SyndTargetDoc d : found) {
            ret.add( d.toServiceObj() );
            sb.append(d.getName()).append(", ");
        }
        LOGGER.info(collectionName + "; Returning targets: "+ sb.toString() );



        // naming: The mechanism strips the prefixes find…By, read…By, query…By, count…By, and get…By from the method. At a very basic level you can define conditions on entity properties and concatenate them with And and Or.
        //findByNameOrPrice         {"bool" : {"should" : [ {"field" : {"name" : "?"}}, {"field" : {"price" : "?"}} ]}}
        //findDistinctPeopleByLastnameOrFirstname
        // findByAddress_ZipCode(ZipCode zipCode) : for property traversal x.address.zipCode , Address containing Zipcode
        //Limiting the result size of a query with Top (or First) and a number:
           //Slice<User> findTop3ByLastname(String lastname, Pageable pageable);
           // List<User> findFirst10ByLastname(String lastname, Sort sort);
        //findByNameContaining      {"bool" : {"must" : {"field" : {"name" : {"query" : "?","analyze_wildcard" : true}}}}}
        //findByNameLike {"bool" : {"must" : {"field" : {"name" : {"query" : "?*","analyze_wildcard" : true}}}}}
        //findByNameIn(Collection<String>names)        {"bool" : {"must" : {"bool" : {"should" : [ {"field" : {"name" : "?"}}, {"field" : {"name" : "?"}} ]}}}}
        //findByAvailableTrue        {"bool" : {"must" : {"field" : {"available" : true}}}}
        //custom @Query

        //http://www.baeldung.com/spring-data-elasticsearch-queries
        //SearchQuery searchQuery =new NativeSearchQueryBuilder()
        //        .withQuery(matchQuery("name", testname)).build();


        //    if(!found.getSiteName().equalsIgnoreCase( collectionName))
        //        throw new ServiceException.PermissionDeniedException();

        return ret;


    }

    public SyndicationTarget addTarget(String collectionName, SyndicationTarget newTarget) {

        newTarget.setCollectionName( collectionName); //explicit set
        SyndTargetDoc r =syndTargetRepository.index( SyndTargetDoc.from(newTarget));


        return r.toServiceObj();
    }


    public SyndicationTarget updateTarget(String collectionName, String targetId, SyndicationTarget target) throws TargetNotFoundException {

        validateString("targetId", targetId);
        target.setCollectionName( collectionName); //explicit set
        //todo testing
        SyndTargetDoc found = syndTargetRepository.findOne(targetId);
        if(found==null)
            throw new TargetNotFoundException("Existing Target not found, id:"+targetId);

        target.setTargetId( targetId);  //use the param value

        SyndTargetDoc r =syndTargetRepository.index( SyndTargetDoc.from(target));

        return r.toServiceObj();
        //return null;
    }

    public void deleteTarget(String collectionName, String targetId) {
        syndTargetRepository.delete( targetId);


        //return null;
    }

    public Status connectTargetToAccount(String collectionName, String targetId, Map<String, String> connectParams) {
        return null;
    }




    @PostConstruct
    public void afterInit() throws Exception {
        printElasticSearchInfo();
    }


        //useful for debug, print elastic search details
    private void printElasticSearchInfo() {

        LOGGER.info("-- ElasticSearch Store info --");

        Client client = persistenceConfig.elasticsearchTemplate().getClient();
        Map<String, String> asMap = client.settings().getAsMap();

        asMap.forEach((k, v) -> {
            LOGGER.info("  " +k + " = " + v);
        });
        LOGGER.info("-- --");
    }


    public static void validateString(String k, String value) {
        if( StringUtils.isBlank(value))
            throw new InvalidInputException("Invalid input value for attribute " + k
                   + " : "+value);
    }


    @Autowired
    private SyndicationTargetRepository syndTargetRepository;
    @Autowired
    private PersistenceConfig persistenceConfig;

    private final  static Logger LOGGER = LoggerFactory.getLogger(SyndTargetService.class);

}
