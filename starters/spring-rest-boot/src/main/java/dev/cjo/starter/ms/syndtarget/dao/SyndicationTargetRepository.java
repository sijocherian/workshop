package dev.cjo.starter.ms.syndtarget.dao;

import dev.cjo.starter.ms.syndtarget.dao.model.IndexSchema;
import dev.cjo.starter.ms.syndtarget.dao.model.SyndTargetDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * //Dao for synd targets
 */
@Repository
public interface SyndicationTargetRepository //extends ElasticsearchCrudRepository<SyndTargetDoc, String> {
        extends ElasticsearchRepository<SyndTargetDoc,String> {

    List<SyndTargetDoc> findAllByCollectionName(String collectionName);
    List<SyndTargetDoc> queryByCollectionNameAndType(String collectionName, IndexSchema.SyndicationType type );
    //List<SyndTargetDoc> findByNameAndId(String name, String id);
}
