/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinitservices.imageStore.repositories;

import com.pinitservices.imageStore.model.ImageData;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Ramdane
 */
@Repository
public interface ImageDataRepository extends ReactiveMongoRepository<ImageData, String> {

}
