/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.controllers;

import com.pinitservices.imageStore.exceptions.ImageNotFoundException;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import com.pinitservices.imageStore.model.ImageData;
import com.pinitservices.imageStore.model.ImagePayload;
import com.pinitservices.imageStore.services.ImageService;
import com.pinitservices.imageStore.utils.ImageUtils;
import java.nio.ByteBuffer;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
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
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

/**
 *
 *
 * @author Ramdane
 */
@RestController
@Log
public class ImageController {

    @Autowired
    private ImageService service;

    private final int[] ranges = new int[67];

    @PostConstruct
    public void init() {

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

    @GetMapping(value = "{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public Mono<byte[]> getImage(@PathVariable String id,
            @RequestParam(required = false, defaultValue = "0") int size) {

        return service.getImage(id, size).map(ImageData::getImageData).switchIfEmpty(Mono.defer(() -> {
            throw new ImageNotFoundException(id);
        }));

    }

    @PostMapping
    public Mono<String> saveImage(@RequestBody Mono<ImagePayload> imageDataMono) {
        return imageDataMono.map(ImagePayload::getData).map(ImageUtils::create)
                .flatMap(service::save)
                .map(ImageData::getId);

    }

    @PostMapping("file")
    public Object saveImagePart(@RequestPart("data") Mono<FilePart> filePartMono) {
        log.info("saveImagePart ");
        return filePartMono.flatMapMany(fp -> fp.content())
                .map(DataBuffer::asByteBuffer)
                .map(ByteBuffer::array)
                .reduce(ImageUtils::concat).map(ImageUtils::create).flatMap(service::save).map(ImageData::getId);

    }

    @DeleteMapping("{id}")
    public Mono<Void> removeImage(@PathVariable String id) {
        log.info("removeImage " + id);
        return service.deleteById(id);
    }

}
