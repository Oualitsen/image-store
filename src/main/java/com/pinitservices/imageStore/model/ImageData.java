/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.model;

import com.pinitservices.imageStore.utils.ImageUtils;
import com.pinitservices.imageStore.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * @author Ramdane
 */
@Document
@Getter
@Setter
@FieldNameConstants
public class ImageData extends DataEntity {

    public static final int[] ranges = new int[67];

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



    private int width;
    private int height;



    private static int getClosest(int requiredSized) {

        for (int range : ranges) {
            if (requiredSized < range) {
                return range;
            }
        }
        return ranges[ranges.length - 1];

    }

    public static Mono<ImageData> from(FilePart filePart) {

        return filePart.content()
                .map(buff -> buff.asByteBuffer().array())
                .reduce(Utils::concat)
                .map(ImageUtils::create)
                .doOnNext(e -> e.setFileName(filePart.filename()));


    }

    public ImageData scaleWidth(int newWidth) {

        if (newWidth > width || newWidth <= 0) {
            return this;
        }

        int _newWidth = getClosest(newWidth);

        ImageData image = new ImageData();
        image.width = _newWidth;
        image.height = (int) (height * ((double) _newWidth / (double) this.width));
        image.data = ImageUtils.scale(data, type, image.width, image.height);
        return image;
    }

}
