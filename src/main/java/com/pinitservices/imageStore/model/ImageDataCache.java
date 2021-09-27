package com.pinitservices.imageStore.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class ImageDataCache extends ImageData {

    @Indexed
    private String originalImageId;

    public static ImageDataCache fromImageData(ImageData imageData) {
        ImageDataCache cache = new ImageDataCache();
        cache.setOriginalImageId(imageData.getId());
        cache.setHeight(imageData.getHeight());
        cache.setWidth(imageData.getWidth());
        cache.setImageType(imageData.getImageType());

        cache.setImageData(imageData.getImageData());

        return cache;

    }

}
