package dev.cjo.starter.ms.syndtarget.service.model;

import dev.cjo.starter.ms.syndtarget.dao.model.IndexSchema.SyndicationType;
import dev.cjo.starter.ms.syndtarget.util.CustomDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;

import java.time.ZonedDateTime;

/**
 * A target (social account, customer url) where asset is syndicated
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyndicationTarget {
    @ApiModelProperty(required = true)
    private String name, collectionName;

    private String targetId; //auto assigned by system

    private SyndicationType type;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime  updateDate;
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //private LocalDateTime authenticatedDate, updateDate;

    private Boolean disabled = false;

    public SyndicationTarget() {
    }

    public SyndicationTarget(String targetId, SyndicationType type) {
        this.targetId = targetId;
        this.type = type;
    }

    public SyndicationTarget(String targetId, SyndicationType type, String collectionName, String name) {
        this.targetId = targetId;
        this.name = name;
        this.type = type;
        this.collectionName = collectionName;
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

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
