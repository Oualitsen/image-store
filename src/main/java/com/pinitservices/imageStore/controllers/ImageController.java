/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.controllers;

import com.pinitservices.imageStore.exceptions.ResourceFoundException;
import com.pinitservices.imageStore.model.ImageData;
import com.pinitservices.imageStore.model.ImagePayload;
import com.pinitservices.imageStore.services.ImageService;
import com.pinitservices.imageStore.utils.ImageUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
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
        return service.findById(id).flatMap(image -> Mono.just(
                        ResponseEntity.ok().contentType(MediaType.valueOf(String.format("image/%s", image.getImageType())))
                                .body(image.scaleWidth(size).getImageData())))
                .switchIfEmpty(Mono.error(new ResourceFoundException(id)));

    }

    @PostMapping
    public Mono<String> saveImage(@RequestBody Mono<ImagePayload> imageDataMono,
                                  @RequestParam(value = "ownerId", required = false) String ownerId) {
        return imageDataMono.map(ImagePayload::getData).map(ImageUtils::create).map(id -> {
            id.setOwnerId(ownerId);
            return id;
        }).flatMap(service::save).map(ImageData::getId);

    }

    @PostMapping("part")
    public Mono<String> saveImagePart(@RequestPart("data") Mono<FilePart> filePartMono,
                                      @RequestParam(value = "ownerId", required = false) String ownerId) {
        return filePartMono.flatMap(ImageData::from)
                .doOnNext(id -> id.setOwnerId(ownerId))
                .flatMap(service::save).map(ImageData::getId);
    }

    @PostMapping(value = "part/multi", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> saveImages(
            @RequestPart("data") Flux<FilePart> filePartFlux,
            @RequestParam(value = "ownerId", required = false) String ownerId
    ) {
        log.info("saving multiple images");
        return filePartFlux
                .flatMap(ImageData::from)
                .doOnNext(image -> image.setOwnerId(ownerId))
                .flatMap(service::save)
                .map(ImageData::getId)
                .doOnNext(log::info);

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

    @GetMapping("/own/{ownerId}")
    Flux<ImageData> getByOwnerId(@PathVariable String ownerId, @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex) {
        return service.findByOwnerId(ownerId, PageRequest.of(pageIndex, 20));
    }

    @PostMapping("/own/{ownerId}")
    Mono<Void> deleteAll(@PathVariable String ownerId, @RequestBody List<String> ids) {
        return service.deleteAllByOwnerId(ownerId, ids);
    }


    @PostMapping("upload/{ownerId}")
    public Mono<ImageData> upload(@RequestPart("data") Mono<FilePart> filePartMono,
                                  @PathVariable String ownerId) {
        return filePartMono.flatMap(ImageData::from)
                .doOnNext(id -> id.setOwnerId(ownerId))
                .flatMap(service::save).doOnNext(imageData -> imageData.setImageData(null));
    }

}
