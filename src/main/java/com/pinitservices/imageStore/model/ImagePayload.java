/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.model;

import com.pinitservices.imageStore.utils.ImageUtils;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Ramdane
 */
@Getter
@Setter
public class ImagePayload {

    private byte[] data;
    private String base64;

    public byte[] getData() {

        if (data == null) {
            if (base64 != null) {
                data = ImageUtils.base64ToByteArray(base64);
            }
        }
        return data;
    }

}
