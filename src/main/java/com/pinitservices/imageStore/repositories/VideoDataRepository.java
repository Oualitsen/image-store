/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.repositories;

import com.pinitservices.imageStore.model.VideoData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 *
 * @author Ramdane
 */
@Repository
public interface VideoDataRepository extends ReactiveMongoRepository<VideoData, String> {

    Mono<Void> deleteByIdAndOwnerId(String id, String ownerId);


}
