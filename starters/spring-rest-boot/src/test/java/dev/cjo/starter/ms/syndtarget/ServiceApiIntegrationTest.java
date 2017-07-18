package dev.cjo.starter.ms.syndtarget;

import dev.cjo.starter.ms.syndtarget.dao.model.IndexSchema;
import dev.cjo.starter.ms.syndtarget.dao.model.SyndTargetDoc;
import dev.cjo.starter.ms.syndtarget.service.model.SyndicationTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import dev.cjo.starter.ms.syndtarget.dao.model.IndexSchema.SyndicationType;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import io.restassured.RestAssured;
import io.restassured.response.Response;


//import org.springframework.boot.test.mock.mockito.*;
//import static org.mockito.BDDMockito.*;
/*
import com.jayway.restassured.matcher.RestAssuredMatchers.*;
import com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
*/
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Rest service test via http client
 *
 * @author scherian
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ServiceApplication.class)

//@ContextConfiguration(classes = PersistenceConfig.class)
//@WebAppConfiguration
/*@TestPropertySource(properties = {
        "elasticsearch.datadir=/tmp/syndicate-data",
        "elasticsearch.home=/tmp/es",
        "server.port=9091"
})*/
public class ServiceApiIntegrationTest {

    private String collectionName = "dev-static-intg"; //foir test

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Before
    public void before() {
        elasticsearchTemplate.deleteIndex(SyndTargetDoc.class);
        elasticsearchTemplate.createIndex(SyndTargetDoc.class);
        elasticsearchTemplate.putMapping(SyndTargetDoc.class);

        elasticsearchTemplate.deleteIndex(SyndTargetDoc.class);
        elasticsearchTemplate.createIndex(SyndTargetDoc.class);
        elasticsearchTemplate.putMapping(SyndTargetDoc.class);

        SyndTargetDoc s1 = new SyndTargetDoc(collectionName, SyndicationType.TWITTER,  "SeedD1");

        elasticsearchTemplate.index(new IndexQueryBuilder()
                //.withId(employee.getId())
                .withIndexName(IndexSchema.indexName).withObject(s1)
                .withType(IndexSchema.SyndTarget.type).build()
        );
        SyndTargetDoc s2 = new SyndTargetDoc(collectionName, SyndicationType.FACEBOOK,  "SeedD2");

        elasticsearchTemplate.index(new IndexQueryBuilder()
                //.withId(employee.getId())
                .withIndexName(IndexSchema.indexName).withObject(s2)
                .withType(IndexSchema.SyndTarget.type).build()
        );


    }





    @Test
    public void whenTargetSaved_thenRetrieved() {

        //test
        List<SyndicationTarget> ret = new ArrayList<SyndicationTarget>();
        SyndicationTarget s1 = new SyndicationTarget(null,  SyndicationType.FACEBOOK , collectionName, "Name1");
        s1.setDisabled( true);
        //ZonedDateTime now = ZonedDateTime.now();
        // s1.setUpdateDate(now);
        SyndicationTarget s2 = new SyndicationTarget(null, SyndicationType.FACEBOOK ,collectionName, "Name2");


        SyndicationTarget resp1 = this.restTemplate.postForObject("/v1/synd/" + collectionName+"/targets", s1, SyndicationTarget.class);
        LOGGER.info("POSTed resp: "+ resp1.getName());
        SyndicationTarget resp2 = this.restTemplate.postForObject("/v1/synd/" + collectionName+"/targets", s2, SyndicationTarget.class);
        LOGGER.info("POSTed resp: "+ resp2.getName());

        SyndicationTarget  testS1 = this.restTemplate.getForObject("/v1/synd/" + collectionName+"/targets/"+resp1.getTargetId(), SyndicationTarget.class);
        assertEquals(testS1.getTargetId(), resp1.getTargetId());

        ResponseEntity<SyndicationTarget[]> responseEntity = restTemplate.getForEntity("/v1/synd/" + collectionName+"/targets", SyndicationTarget[].class);
        SyndicationTarget[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        boolean found1=false,found2=false;

        for(SyndicationTarget t : objects) {
            if(resp1.getTargetId().equals(t.getTargetId()) )
                found1=true;
            if(resp2.getTargetId().equals(t.getTargetId()))
                found2=true;
        }

        assertTrue( found1 && found2);


    }



    @Test
    public void whenVerifyApidocsIsWorking_thenOK() {


        String URL_PREFIX = "http://localhost:" + servicePort;
        Response response = RestAssured.get(URL_PREFIX + "/swagger-ui.html#/");
        assertEquals(200, response.statusCode());



        RestAssured.get(URL_PREFIX + "/v2/api-docs").
                then().
                    statusCode(200).
                    body("definitions.SyndicationTarget.required.size()", greaterThan(0));



        //String jsonString = response.getBody().asString();
        //JsonPath jsonPath = JsonPath.from(jsonString);

        //List<Integer> winnerIds = from(json).get("lotto.winners.winnerId");
        //jsonPath("$.content").value("Hello, World!")

    }

    protected final  static Logger LOGGER = LoggerFactory.getLogger(ServiceApiIntegrationTest.class);



    @LocalServerPort
    int servicePort;

}

