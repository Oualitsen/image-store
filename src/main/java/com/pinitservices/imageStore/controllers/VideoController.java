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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Log
@RestController
@RequiredArgsConstructor
@RequestMapping("video")
public class VideoController {

    private final VideoService service;
    private final Mono<User> userMono;

    @GetMapping("{videoId}")
    Mono<ResponseEntity<byte[]>> getVideoWithData(@PathVariable String videoId) {
        return service.findById(videoId)
                .map(videoData -> ResponseEntity.ok().contentType(MediaType.valueOf(videoData.getType()))
                        .body(videoData.getData()))
                .switchIfEmpty(Mono.error(new ResourceFoundException(videoId)));
    }

    @GetMapping("no-data/{videoId}")
    Mono<VideoData> getVideo(@PathVariable String videoId) {
        return service.findByIdWithNoData(videoId)
                .switchIfEmpty(Mono.error(new ResourceFoundException(videoId)));
    }


    @GetMapping("/own")
    Flux<VideoData> getByOwnerId(@RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex) {
        return userMono.map(User::getUserId)
                .flatMapMany(ownerId -> service.findByOwnerId(ownerId, PageRequest.of(pageIndex, 20)));

    }

}
