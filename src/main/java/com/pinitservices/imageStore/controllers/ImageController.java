/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.controllers;

import com.pinitservices.imageStore.exceptions.ImageNotFoundException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 *
 *
 * @author Ramdane
 */
@Log
@RestController
@RequestMapping("image")
public class ImageController {

    @Autowired
    private ImageService service;

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<byte[]>> getImage(@PathVariable String id,
            @RequestParam(required = false, defaultValue = "0") int size) {
        log.info("getting image " + id);
        return service.findById(id).map(e -> {

            log.info("e.data = " + e.getImageData());
            return e;
        }).flatMap(image -> Mono.just(
                ResponseEntity.ok().contentType(MediaType.valueOf(String.format("image/%s", image.getImageType())))
                        .body(image.scaleWidth(size).getImageData())))
                .switchIfEmpty(Mono.error(new ImageNotFoundException(id)));

    }

    @PostMapping
    public Mono<String> saveImage(@RequestBody Mono<ImagePayload> imageDataMono,
            @RequestParam(value = "ownerId", required = false) String ownerId) {
        log.info("savingImage ========>");
        return imageDataMono.map(ImagePayload::getData).map(ImageUtils::create).map(id -> {
            id.setOwnerId(ownerId);
            return id;
        }).flatMap(service::save).map(ImageData::getId);

    }

    @PostMapping("part")
    public Mono<String> saveImagePart(@RequestPart("data") Mono<FilePart> filePartMono,
            @RequestParam(value = "ownerId", required = false) String ownerId) {
        return filePartMono.flatMapMany(fp -> fp.content()).map(DataBuffer::asByteBuffer).map(ByteBuffer::array)
                .reduce(ImageUtils::concat).map(ImageUtils::create).map(id -> {
                    id.setOwnerId(ownerId);
                    return id;
                }).flatMap(service::save).map(ImageData::getId);
    }

    

    @DeleteMapping("{id}")
    public Mono<Void> removeImage(@PathVariable String id,
            @RequestParam(value = "ownerId", required = false) String ownerId) {
        if (ownerId != null) {
            return service.removeImageByIdAndOwnerId(id, ownerId);
        } else {
            return service.removeImageById(id);
        }
    }

}
