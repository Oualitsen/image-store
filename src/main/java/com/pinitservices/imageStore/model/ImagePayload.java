/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.model;

import com.pinitservices.imageStore.utils.ImageUtils;

/**
 *
 * @author Ramdane
 */
public class ImagePayload {
    private byte[] data;
    private String base64;

    public byte[] getData() {
        
        if(data == null) {
            if(base64 != null) {
                data = ImageUtils.base64ToByteArray(base64);
            }
        }
        return data;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    
    

}
