package dev.cjo.starter.ms.syndtarget.dao.model;

import dev.cjo.starter.ms.syndtarget.service.impl.InvalidInputException;
import dev.cjo.starter.ms.syndtarget.service.model.SyndicationTarget;
import dev.cjo.starter.ms.syndtarget.dao.model.IndexSchema.SyndicationType;
import dev.cjo.starter.ms.syndtarget.service.impl.SyndTargetService;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Persistence entity : uses elasticsearch specific annotation
 * A target (social account, customer http url)
 */
//@Entity
@Document(indexName = IndexSchema.indexName, type = IndexSchema.SyndTarget.type, shards = 3, replicas = 1)
public class SyndTargetDoc {

    @Id
    private String targetId; //auto assigned by system
    private String name, collectionName;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private IndexSchema.SyndicationType type;

    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //private LocalDateTime updateDate;


    @LastModifiedDate
    @Field(type = FieldType.Date)
    //private ZonedDateTime updateDate;
    //private Date updateDate;
    private Long updateDate;

    private Boolean disabled;
    @Version
    private Long version;

    public SyndTargetDoc() {
    }

    //public SyndTargetDoc(SyndicationType type) {
    //    this.type = type;
    //}

    public SyndTargetDoc(String collectionName, SyndicationType type, String name) {
        this.collectionName = collectionName;
        this.name = name;
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SyndicationType getType() {
        return type;
    }

    public void setType(SyndicationType t) {
        this.type = t;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }


    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }


    public SyndicationTarget toServiceObj() {

        SyndicationTarget r = new SyndicationTarget( targetId,  type,  collectionName,  name);
        r.setDisabled(disabled );
        if(updateDate!=null)
            r.setUpdateDate(getDateFieldUtc(updateDate));

        //toServiceObj().get( );
        return r;
    }

    public static ZonedDateTime getDateFieldUtc(Long date) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date),
                ZoneId.of("UTC"));
    }

    public static SyndTargetDoc from(SyndicationTarget t) {
        //Validation
        if(t.getType() ==null)
            throw new InvalidInputException("Invalid SyndicationTarget value.");

        SyndTargetService.validateString("name" , t.getName());
        SyndTargetDoc doc = new SyndTargetDoc(t.getCollectionName(), t.getType(), t.getName());
        doc.setTargetId( t.getTargetId());
        doc.setDisabled( t.getDisabled());
        // SystemAssigned //
        doc.setUpdateDate(Instant.now().toEpochMilli());


        return doc;
    }


}
