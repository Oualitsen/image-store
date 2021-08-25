/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.controllers;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import com.pinitservices.imageStore.model.ImageData;
import com.pinitservices.imageStore.repositories.ImageDataRepository;
import com.pinitservices.imageStore.services.ImageService;
import com.pinitservices.imageStore.utils.ImageUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 *
 * @author Ramdane
 */
@RestController
public class ImageController {

    @Autowired
    private ImageDataRepository imageDataRepository;
    
    @Autowired
    private ImageService service;

    @Autowired
    private Logger logger;

    private final int[] ranges = new int[67];

    @PostConstruct
    private void init() {

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

  

    @GetMapping(value = "{id}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public Mono<byte[]> getImage(@PathVariable String id,
            @RequestParam(required = false, defaultValue = "0") int size) {
        
        
        return service.getImage(id, size).map(ImageData::getImageData);

    }

    @PostMapping
    public Mono<String> saveImage(@RequestBody Mono<ImageData> imageDataMono) {
        return imageDataMono.flatMap(image -> {
         return imageDataRepository.save(image);
        }).map(ImageData::getId);
        
    }

    @PostMapping("file")
    public Object saveImagePart(@RequestPart("data") Mono<FilePart> filePartMono) {
      return  filePartMono.flatMapMany(fp -> fp.content())
                .map(buffer -> {
                    return buffer.asByteBuffer().array();
                })
                .reduce(ImageUtils::concat).map(ImageUtils::create).flatMap(image -> {
                    
                    return imageDataRepository.save(image);
                
                }).map(ImageData::getId);


    }

    @PostMapping("/test")
    public Mono<String> sendData(@RequestBody String data) {

        logger.info("send data");
        return Mono.just(data);

    }

    @DeleteMapping("{id}")
    public Mono<Void> removeImage(@PathVariable String id) {
        return imageDataRepository.deleteById(id);
    }

}
