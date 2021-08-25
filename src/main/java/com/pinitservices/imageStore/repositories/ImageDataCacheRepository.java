/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.repositories;

import com.pinitservices.imageStore.model.ImageDataCache;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 *
 * @author Ramdane
 */
@Repository
public interface ImageDataCacheRepository extends ReactiveMongoRepository<ImageDataCache, String> {
    
    Mono<ImageDataCache> findByOriginalImageIdAndWidth(String id, int width);
}
