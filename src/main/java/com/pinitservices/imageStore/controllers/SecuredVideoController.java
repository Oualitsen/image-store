package com.pinitservices.imageStore.controllers;

import com.pinitservices.imageStore.exceptions.ResourceFoundException;
import com.pinitservices.imageStore.model.User;
import com.pinitservices.imageStore.model.VideoData;
import com.pinitservices.imageStore.services.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Log
@RestController
@RequiredArgsConstructor
@RequestMapping("secured-video")
public class SecuredVideoController {

    private final VideoService service;
    private final Mono<User> userMono;

    @PostMapping
    Mono<String> save(@RequestPart("data") Mono<FilePart> videoPart) {

        return videoPart.flatMap(e -> Mono.zip(VideoData.from(e), userMono))
                .map(tuple -> {
                    var videoData = tuple.getT1();
                    var user = tuple.getT2();
                    videoData.setOwnerId(user.getUserId());
                    return videoData;
                }).flatMap(service::save)
                .map(VideoData::getId);
    }


    @PostMapping("multi")
    Flux<String> save(@RequestPart("data") Flux<FilePart> flux,
                      @RequestParam(value = "ownerId", required = false) String ownerId) {
        return flux.flatMap(part -> service.save(part, ownerId))
                .map(VideoData::getId);
    }


    @DeleteMapping("{id}")
    Mono<Void> delete(@PathVariable String id) {
        return userMono.map(User::getUserId).flatMap(ownerId -> service.deleteById(id, ownerId));
    }


    @GetMapping("/own")
    Flux<VideoData> getByOwnerId(@RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex) {
        return userMono.map(User::getUserId)
                .flatMapMany(ownerId -> service.findByOwnerId(ownerId, PageRequest.of(pageIndex, 20)));

    }


    @PostMapping("/own")
    Mono<Void> deleteAll(@RequestBody List<String> ids) {
        return userMono.map(User::getUserId).flatMap(ownerId -> service.deleteAllByOwnerId(ownerId, ids));
    }


    @PostMapping("/upload")
    public Mono<VideoData> upload(@RequestPart("data") Mono<FilePart> videoPart) {
        return videoPart.flatMap(VideoData::from)
                .flatMap(videoData -> Mono.zip(Mono.just(videoData), userMono))
                .map(e -> {
                    var v = e.getT1();
                    var u = e.getT2();
                    v.setOwnerId(u.getUserId());
                    return v;
                }).flatMap(service::save);
    }
}
