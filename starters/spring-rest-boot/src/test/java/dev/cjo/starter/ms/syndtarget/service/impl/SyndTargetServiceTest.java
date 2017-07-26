package dev.cjo.starter.ms.syndtarget.service.impl;

import dev.cjo.starter.ms.syndtarget.ServiceApplication;
import dev.cjo.starter.ms.syndtarget.config.PersistenceConfig;
import dev.cjo.starter.ms.syndtarget.dao.model.IndexSchema.SyndicationType;

import dev.cjo.starter.ms.syndtarget.dao.model.SyndTargetDoc;
import dev.cjo.starter.ms.syndtarget.service.model.SyndicationTarget;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author scherian
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceApplication.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfig.class)
@WebAppConfiguration
@TestPropertySource(properties = {
        "elasticsearch.datadir=/tmp/syndicate-data",
        "elasticsearch.home=/tmp/es",
        "server.port=9091"
})
public class SyndTargetServiceTest {

    private String collectionName = "dev-static-junit"; //foir test

    @Autowired
    private SyndTargetService myService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Before
    public void before() {
        elasticsearchTemplate.deleteIndex(SyndTargetDoc.class);
        elasticsearchTemplate.createIndex(SyndTargetDoc.class);
        elasticsearchTemplate.putMapping(SyndTargetDoc.class);

        SyndicationTarget s1 = new SyndicationTarget(null, SyndicationType.TWITTER ,collectionName, "Seed1");

        SyndicationTarget s2 = new SyndicationTarget(null, SyndicationType.FACEBOOK ,collectionName, "Seed2");
        myService.addTarget(collectionName, s1);
        myService.addTarget(collectionName, s2);
    }


    @Test
    public void whenTargetSaved_thenIdIsAssigned() {

        //test
        List<SyndicationTarget> ret = new ArrayList<SyndicationTarget>();
        SyndicationTarget s1 = new SyndicationTarget(null,  SyndicationType.FACEBOOK , collectionName, "Name1");
        s1.setDisabled( true);
        //ZonedDateTime now = ZonedDateTime.now();
       // s1.setUpdateDate(now);
        SyndicationTarget s2 = new SyndicationTarget(null, SyndicationType.FACEBOOK ,collectionName, "Name2");



        List<SyndicationTarget> found = myService.listAllTargets(collectionName);
        int initCount = found.size();

        myService.addTarget(collectionName, s1);
        myService.addTarget(collectionName, s2);
        List<SyndicationTarget> foundAfter = myService.listAllTargets(collectionName);
        assertTrue( foundAfter.size() - initCount == 2);

        for(SyndicationTarget t : foundAfter)
            assertNotNull(t.getTargetId());
    }

    @Test
    public void forSite_correctTargetsAreListed() throws PermissionDeniedException {


        String testname= "Name1b";
        List<SyndicationTarget> found = myService.listAllTargets(collectionName);
        int initCount = found.size();
        SyndicationTarget newt = new SyndicationTarget(null, SyndicationType.FACEBOOK, "bad-site", testname);
        myService.addTarget(collectionName, newt);

        found = myService.listAllTargets(collectionName);
        int newCount = found.size();

        boolean siteFound=false;
        for (SyndicationTarget t : found) {
            if(testname.equalsIgnoreCase(t.getName()) ) {
                siteFound = true;
            }
        }

        assertTrue( newCount > initCount);
        assertTrue(siteFound);


        //validate using search q
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("name", testname)).build();
        final List<SyndTargetDoc> nameSearchResults = elasticsearchTemplate.queryForList(searchQuery, SyndTargetDoc.class);
        for (SyndTargetDoc t : nameSearchResults) {
            assertEquals(testname,t.getName()) ;
        }

        final List<SyndicationTarget> typeSearchResults = myService.findAllBySyndicationType(collectionName, SyndicationType.FACEBOOK);
        LOGGER.info("typeSearchResults :" + typeSearchResults.size());
        for (SyndicationTarget t : typeSearchResults) {
            assertEquals(SyndicationType.FACEBOOK,t.getType()) ;
        }

        /*found = syndTargetRepository.findAllBySiteName(siteName);
        LOGGER.info("Found2 :" + found.size());
        for (SyndTargetDoc d : found) {
            SyndicationTarget t = new SyndicationTarget(d.getTargetId(), d.getType(), d.getSiteName(), d.getName());
            if (d.getUpdateDate() != null)
                t.setUpdateDate(
                        ZonedDateTime.ofInstant(d.getUpdateDate().toInstant(),
                                ZoneId.of("UTC")));

            ret.add(t);
            LOGGER.info("\t" + d.getName() +
                    " id " + d.getTargetId());
        }*/


    }


    protected final  static Logger LOGGER = LoggerFactory.getLogger(SyndTargetServiceTest.class);




}
