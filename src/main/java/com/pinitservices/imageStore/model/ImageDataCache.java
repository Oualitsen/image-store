package com.pinitservices.imageStore.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ImageDataCache extends ImageData {

    @Indexed
    private String originalImageId;

    public String getOriginalImageId() {
        return originalImageId;
    }

    
    
    public void setOriginalImageId(String originalImageId) {
        this.originalImageId = originalImageId;
    }
    
    public static ImageDataCache fromImageData(ImageData imageData) {
        ImageDataCache cache = new ImageDataCache();
        cache.setOriginalImageId(imageData.getId());
        System.out.println("@@@@@@@@################## " +cache.getOriginalImageId() );
        cache.setHeight(imageData.getHeight());
        cache.setWidth(imageData.getWidth());
        cache.setImageType(imageData.getImageType());
        
        cache.setImageData(imageData.getImageData());
        
        return cache;
        
    }
    
    
}
