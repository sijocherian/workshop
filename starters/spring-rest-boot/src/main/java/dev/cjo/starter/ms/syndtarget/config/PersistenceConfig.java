package dev.cjo.starter.ms.syndtarget.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "dev.cjo.starter.ms.syndtarget.dao")
public class PersistenceConfig {


    /*
    Transport client
    @Bean
    public Client client() throws Exception {

        Settings esSettings = Settings.settingsBuilder()
                .put("cluster.name", EsClusterName)
                .build();

        //https://www.elastic.co/guide/en/elasticsearch/guide/current/_transport_client_versus_node_client.html
        return TransportClient.builder()
                .settings(esSettings)
                .build()
                .addTransportAddress(
				  new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
    }

    * */



    @Bean
    public Client client() {

            //final Path tmpDir = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "elasticsearch_data");
          
            
            /*if (logger.isDebugEnabled()) {
            	  logger.debug("Creating " + tmpDir.toAbsolutePath().toString());  // this is compliant, because it will not evaluate if log level is above debug.
            	}*/

            

            final Settings.Builder elasticsearchSettings =
                    Settings.settingsBuilder().put("http.enabled", "false")
                        .put("path.data",elasticsearchDatadir)
                        .put("path.home", elasticsearchHome)
                                              ;
            //Embedded Elasticsearch Server
        Client c= new NodeBuilder()
                    .local(true)
                    .settings(elasticsearchSettings)
                    .node()
                    .client();

        return c;

    }

    //Embedded Elasticsearch Server
    /*@Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
    }*/

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {

        return new ElasticsearchTemplate(client() , getDefaultConverter());

    }


    @Bean
    public MappingElasticsearchConverter getDefaultConverter()  {

        MappingElasticsearchConverter converter = new MappingElasticsearchConverter(
                new SimpleElasticsearchMappingContext());

        //CALL THIS MANULLY, so that all the default convertors will be registered!
       // converter.afterPropertiesSet();

        return converter;
    }





    @Value("${elasticsearch.datadir}")
    private String elasticsearchDatadir;

    @Value("${elasticsearch.home}")
    private String elasticsearchHome;


    private static Logger logger = LoggerFactory.getLogger(PersistenceConfig.class);

}
