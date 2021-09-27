/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 *
 * @author Ramdane
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(of = "id")
public class BasicEntity {

    @Id
    private String id;

    @CreatedDate
    private long creationDate;

    @LastModifiedDate
    private long lastUpdate;

    @LastModifiedBy
    private String lastModifiedBy;

}
