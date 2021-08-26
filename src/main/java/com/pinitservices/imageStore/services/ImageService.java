/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.services;

import com.pinitservices.imageStore.model.ImageData;
import com.pinitservices.imageStore.model.ImageDataCache;
import com.pinitservices.imageStore.repositories.ImageDataCacheRepository;
import com.pinitservices.imageStore.repositories.ImageDataRepository;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 *
 * @author Ramdane
 */
@Service
public class ImageService {

    @Autowired
    private Logger logger;

    @Autowired
    private ImageDataCacheRepository cacheRepo;

    @Autowired
    private ImageDataRepository repo;

    public Mono<ImageData> getImage(String imageId, int width) {

        if (width <= 0) {
            return repo.findById(imageId);
        }

        return cacheRepo.findFirstByOriginalImageIdAndWidth(imageId, width).cast(ImageData.class).switchIfEmpty(Mono.defer(() -> {
            return repo.findById(imageId)
                    .map(image -> image.scaleWidth(width))
                    .doOnNext(image -> {
                        image.setId(imageId);
                        cache(image);
                    });

        }));

    }

    private void cache(ImageData imageData) {
        cacheRepo.save(ImageDataCache.fromImageData(imageData)).cast(ImageData.class).subscribe();
    }

    public <S extends ImageData> Mono<S> save(S s) {
        return repo.save(s);
    }

    public Mono<Void> deleteById(String id) {
        return repo.deleteById(id).zipWith(cacheRepo.deleteAllByOriginalImageId(id)).map(t -> null);
    }

}
