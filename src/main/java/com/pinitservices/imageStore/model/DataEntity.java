package com.pinitservices.imageStore.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
public class DataEntity extends BasicEntity{

    protected String ownerId;
    protected byte[] data;
    protected String fileName;
    protected String url;
    protected String type;

}
