/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.utils;

import com.pinitservices.imageStore.model.ImageData;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;

import lombok.extern.java.Log;

/**
 *
 *
 */
@Log
public class ImageUtils {

    public static byte[] scale(byte[] image, String type, int dWidth, int dHeight) {

        var img = new javaxt.io.Image(image);
        img.resize(dWidth, dHeight);
        return img.getByteArray(type);

    }



    public static ImageData create(byte[] data) {
        var image = new javaxt.io.Image(data);
        var imageData = new ImageData();
        imageData.setWidth(image.getWidth());
        imageData.setHeight(image.getHeight());
        imageData.setType(ImageUtils.getImageType(data));
        final HashMap<Integer, Object> exif = image.getExifTags();
        try {

            if (exif.get(0x0112) != null) {

                int orientation = (Integer) exif.get(0x0112);
                switch (orientation) {
                    case 3 ->
                        image.rotate(180);
                    case 6 ->
                        image.rotate(90);
                }
            }

        } catch (Exception ex) {
            log.log(Level.WARNING, null, ex);
        }

        imageData.setData(image.getByteArray(imageData.getType()));
        return imageData;

    }



    public static String getImageType(byte[] data) {
        return Utils.TIKA.detect(data);
    }

    public static byte[] base64ToByteArray(String base64) {
        if (base64 == null) {
            return null;
        }
        int indexOf = base64.indexOf("base64,");
        if (indexOf != -1) {
            base64 = base64.substring(indexOf + "base64,".length());
        }
        return Base64.getDecoder().decode(base64);
    }





}
