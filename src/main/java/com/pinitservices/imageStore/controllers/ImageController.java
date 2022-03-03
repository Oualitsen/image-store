/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.controllers;

import com.pinitservices.imageStore.exceptions.ResourceFoundException;
import com.pinitservices.imageStore.services.ImageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
        return service.findById(id).flatMap(image -> Mono.just(
                        ResponseEntity.ok().contentType(MediaType.valueOf(image.getType()))
                                .body(image.scaleWidth(size).getData())))
                .switchIfEmpty(Mono.error(new ResourceFoundException(id)));
    }



}
