/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.model;

import com.pinitservices.imageStore.utils.ImageUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Ramdane
 */
@Document
@Getter
@Setter
@FieldNameConstants
public class ImageData extends BasicEntity {

    private byte[] imageData;
    private String imageType;
    private int width;
    private int height;

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
