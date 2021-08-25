/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.utils;

import com.pinitservices.imageStore.model.ImageData;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.tika.Tika;
import org.springframework.core.io.buffer.DataBuffer;

/**
 *
 *
 */
public class ImageUtils {

    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    public static final String TYPE_PNG = "png";
    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";

    private static final Tika TIKA = new Tika();

    public static byte[] scale(byte[] imgage, String type, int dWidth, int dHeight) {

        var img = new javaxt.io.Image(imgage);

        img.resize(dWidth, dHeight);
        final byte[] byteArray = img.getByteArray(type);
        return byteArray;

    }

    public static ImageData create(byte[] data) {
        var image = new javaxt.io.Image(data);
        var imageData = new ImageData();

        imageData.setWidth(image.getWidth());
        imageData.setHeight(image.getHeight());
        imageData.setImageType(ImageUtils.getImageType(data).replaceFirst("image/", ""));

        final HashMap<Integer, Object> exif = image.getExifTags();

        try {

            if (exif.get(0x0112) != null) {

                int orientation = (Integer) exif.get(0x0112);
                switch (orientation) {
                    case 3 -> image.rotate(180);
                    case 6 -> image.rotate(90);
                }
            }

        } catch (Exception ex) {
            logger.log(Level.WARNING, TYPE_PNG, ex);
        }

        imageData.setImageData(image.getByteArray(imageData.getImageType()));
        return imageData;

    }

    public static BufferedImage byteArrayToImage(byte[] array) {
        try {
            return ImageIO.read(new ByteArrayInputStream(array));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] imageToByteArray(BufferedImage image, String type) {

        ByteArrayOutputStream boss = new ByteArrayOutputStream();
        byte[] toByteArray;
        try {
            ImageIO.write(image, type, boss);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {

            try {
                boss.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        toByteArray = boss.toByteArray();
        return toByteArray;
    }

    public static String getImageType(byte[] data) {
        final String type = TIKA.detect(data);
        return type;
    }

    public static byte[] base64ToByteArray(String base64) {
        if (base64 == null) {
            return null;
        }
        int indexOf = base64.indexOf("base64,");
        if (indexOf != -1) {
            base64 = base64.substring(indexOf + "base64,".length());
        }
        final byte[] decode = Base64.getDecoder().decode(base64);
        return decode;
    }

    public static byte[] collect(List<Byte> list) {
        byte[] result = new byte[list.size()];
        
        
        for(int i = 0; i<list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
    
    public static byte[] concat(byte[] a, byte[] b ) {
        byte[] result = new byte[a.length + b.length];
        
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length,  b.length);
        
        
        
        return result;
    } 
    
    public static void main(String[] args) {
        byte[] a = {0, 1, 2};
        byte[] b = {3, 4, 5, 6};
        
        var result = concat(a, b);
        
        for(byte _byte:result) {
            System.out.println("byte = " + _byte);
        }
        
    }
    
    

}
