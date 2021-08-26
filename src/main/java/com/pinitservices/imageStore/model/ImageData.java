/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.model;

import com.pinitservices.imageStore.utils.ImageUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Ramdane
 */
@Document
public class ImageData extends BasicEntity {

    public static final String FIELD_IMAGE_DATA = "imageData";
    public static final String FIELD_IMAGE_TYPE = "imageType";

    public static final String FIELD_WIDTH = "width";
    public static final String FIELD_HEIGHT = "height";

    private byte[] imageData;
    private String imageType;
    private int width;
    private int height;

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ImageData scaleWidth(int newWidth) {
        ImageData image = new ImageData();

        if (newWidth > width) {
            return this;
        }

        image.width = newWidth;
        image.height = (int) (height * ((double) newWidth / (double) this.width));
        image.imageData = ImageUtils.scale(imageData, imageType, image.width, image.height);
        return image;
    }

}
