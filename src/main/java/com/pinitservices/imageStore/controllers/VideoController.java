package com.pinitservices.imageStore.controllers;

import com.pinitservices.imageStore.exceptions.ResourceFoundException;
import com.pinitservices.imageStore.model.ImageData;
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
@RequestMapping("video")
public class VideoController {

    private final VideoService service;

    @GetMapping("{videoId}")
    Mono<ResponseEntity<byte[]>> getVideoWithData(@PathVariable String videoId) {
        log.info("getting video ### " + videoId);
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

    @PostMapping
    Mono<String> save(@RequestPart("data") Mono<FilePart> videoPart,
                      @RequestParam(value = "ownerId", required = false) String ownerId) {
        return videoPart.flatMap(e -> service.save(e, ownerId)).map(VideoData::getId);
    }


    @PostMapping("multi")
    Flux<String> save(@RequestPart("data") Flux<FilePart> flux,
                      @RequestParam(value = "ownerId", required = false) String ownerId) {
        return flux.flatMap(part -> service.save(part, ownerId))
                .map(VideoData::getId);
    }

    @DeleteMapping("{id}")
    Mono<Void> delete(@PathVariable String id) {
        return service.deleteById(id);
    }

    @DeleteMapping("{id}/{ownerId}")
    Mono<Void> delete(@PathVariable String id, @PathVariable String ownerId) {
        return service.deleteById(id, ownerId);
    }


    @GetMapping("/own/{ownerId}")
    Flux<VideoData> getByOwnerId(@PathVariable String ownerId, @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex) {
        return service.findByOwnerId(ownerId, PageRequest.of(pageIndex, 20));
    }


    @PostMapping("/own/{ownerId}")
    Mono<Void> deleteAll(@PathVariable String ownerId, @RequestBody List<String> ids) {
        return service.deleteAllByOwnerId(ownerId, ids);
    }


    @PostMapping("upload/{ownerId}")
    public Mono<VideoData> upload(@RequestPart("data") Mono<FilePart> videoPart,
                                  @PathVariable String ownerId) {
        return videoPart.flatMap(e -> service.save(e, ownerId)).doOnNext(v -> v.setData(null));
    }
}
