/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.services;

import com.pinitservices.imageStore.model.DataEntity;
import com.pinitservices.imageStore.model.ImageData;
import com.pinitservices.imageStore.model.ImageDataCache;
import com.pinitservices.imageStore.repositories.ImageDataCacheRepository;
import com.pinitservices.imageStore.repositories.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Ramdane
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${serverUrl}")
    private String url;

    private final ImageDataCacheRepository cacheRepo;

    private final ImageDataRepository repo;
    private final ReactiveMongoTemplate template;

    public Mono<ImageData> getImage(String imageId, int width) {

        if (width <= 0) {
            return repo.findById(imageId);
        }

        return cacheRepo.findFirstByOriginalImageIdAndWidth(imageId, width)
                .cast(ImageData.class)
                .switchIfEmpty(
                        repo.findById(imageId).map(image -> image.scaleWidth(width))
                                .doOnNext(image -> {
                                    image.setId(imageId);
                                    cache(image);
                                })

                );
    }

    private void cache(ImageData imageData) {
        cacheRepo.save(ImageDataCache.fromImageData(imageData)).cast(ImageData.class).subscribe();
    }

    public Mono<Void> removeImageById(String imageId) {
        return repo.deleteById(imageId).flatMap(e -> cacheRepo.deleteAllByOriginalImageId(imageId));
    }

    public Mono<Void> removeImageByIdAndOwnerId(String id, String ownerId) {
        return repo.deleteByIdAndOwnerId(id, ownerId).flatMap(e -> cacheRepo.deleteAllByOriginalImageId(id));
    }

    public Mono<ImageData> save(ImageData entity) {


        return repo.save(entity);
    }

    public Mono<ImageData> findById(String s) {
        return repo.findById(s);
    }

    public Flux<ImageData> findByOwnerId(String ownerId, Pageable pageable) {

        Query query = Query.query(Criteria.where(DataEntity.Fields.ownerId).is(ownerId)).with(pageable);
        query.fields().exclude(DataEntity.Fields.data);
        return template.find(query, ImageData.class);
    }

    public Mono<Void> deleteAllByOwnerId(String ownerId, List<String> ids) {
        return Flux.fromStream(ids.stream())
                .flatMap(id -> repo.deleteByIdAndOwnerId(id, ownerId))
                .then();
    }




}
