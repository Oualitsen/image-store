/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.controllers;

import com.pinitservices.imageStore.model.ImageData;
import com.pinitservices.imageStore.model.ImagePayload;
import com.pinitservices.imageStore.model.User;
import com.pinitservices.imageStore.services.ImageService;
import com.pinitservices.imageStore.utils.ImageUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
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
@RequestMapping("secured-image")
public class SecuredImageController {

    @Autowired
    private ImageService service;

    @Autowired
    private Mono<User> userMono;

    @PostMapping
    public Mono<String> saveImage(@RequestBody Mono<ImagePayload> imageDataMono) {
        return imageDataMono.map(ImagePayload::getData)
                .map(ImageUtils::create)
                .flatMap(imageData -> userMono.map(User::getUserId).doOnNext(imageData::setOwnerId).map(e -> imageData))
                .flatMap(service::save).map(ImageData::getId);
    }

    @PostMapping("part")
    public Mono<String> saveImagePart(@RequestPart("data") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(ImageData::from)
                .flatMap(imageData -> userMono.map(User::getUserId).doOnNext(imageData::setOwnerId).map(e -> imageData))
                .flatMap(service::save).map(ImageData::getId);
    }

    @PostMapping(value = "part/multi", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> saveImages(
            @RequestPart("data") Flux<FilePart> filePartFlux
    ) {
        return filePartFlux
                .flatMap(ImageData::from)
                .flatMap(imageData -> userMono.map(User::getUserId).doOnNext(imageData::setOwnerId)
                        .map(e -> imageData))
                .flatMap(service::save)
                .map(ImageData::getId)
                .doOnNext(log::info);

    }

    @DeleteMapping("{id}")
    public Mono<Void> removeImage(@PathVariable String id) {
        return userMono.map(User::getUserId)
                .flatMap(ownerId -> service.removeImageByIdAndOwnerId(id, ownerId));

    }

    @GetMapping("/own")
    Flux<ImageData> getByOwnerId(@RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex) {
        return userMono.map(User::getUserId)
                .flatMapMany(ownerId -> service.findByOwnerId(ownerId, PageRequest.of(pageIndex, 20)));
    }

    @PostMapping("/own")
    Mono<Void> deleteAll(@RequestBody List<String> ids) {
        return userMono.map(User::getUserId)
                .flatMap(ownerId -> service.deleteAllByOwnerId(ownerId, ids));
    }


    @PostMapping("upload")
    public Mono<ImageData> upload(@RequestPart("data") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(ImageData::from)
                .flatMap(imageData -> Mono.zip(Mono.just(imageData), userMono))
                .map(tuple -> {
                    var image = tuple.getT1();
                    var user = tuple.getT2();
                    image.setOwnerId(user.getUserId());
                    return image;
                }).flatMap(service::save).doOnNext(imageData -> imageData.setData(null));
    }

}
