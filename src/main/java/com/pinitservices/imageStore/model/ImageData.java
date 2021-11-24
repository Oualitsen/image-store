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

    private static final int[] ranges = new int[67];

    static {
        int range = 32;

        for (int i = 0; i < ranges.length; i++) {
            ranges[i] = range;
            if (range < 128) {
                range += 32;
            } else {
                range += 128;
            }
        }
    }

    private static int getClosest(int requiredSized) {

        for (int range : ranges) {
            if (requiredSized < range) {
                return range;
            }
        }
        return ranges[ranges.length - 1];

    }

    private byte[] imageData;
    private String imageType;
    private int width;
    private int height;

    private String ownerId;

    public ImageData scaleWidth(int newWidth) {

        if (newWidth > width || newWidth <= 0) {
            return this;
        }

        int _newWidth = getClosest(newWidth);

        ImageData image = new ImageData();
        image.width = _newWidth;
        image.height = (int) (height * ((double) _newWidth / (double) this.width));
        image.imageData = ImageUtils.scale(imageData, imageType, image.width, image.height);
        return image;
    }

}
