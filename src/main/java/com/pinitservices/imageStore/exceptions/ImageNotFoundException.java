/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Ramdane
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Image not found")
public class ImageNotFoundException extends RuntimeException {

    private final String imageId;

    public ImageNotFoundException(String imageId) {
        super(String.format("Image [%s] not found", imageId));
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

}
