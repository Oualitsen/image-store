package com.pinitservices.imageStore.services;

import com.pinitservices.imageStore.model.BasicEntity;
import com.pinitservices.imageStore.model.VideoData;
import com.pinitservices.imageStore.repositories.VideoDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoDataRepository repository;
    private final ReactiveMongoTemplate template;

    public Mono<VideoData> save(FilePart part, String ownerId) {
        return VideoData.from(part)
                .doOnNext(vd -> vd.setOwnerId(ownerId))
                .flatMap(repository::save);
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    public Mono<Void> deleteById(String id, String ownerId) {
        return repository.deleteByIdAndOwnerId(id, ownerId);
    }

    public Mono<VideoData> findById(String id) {
        return repository.findById(id);
    }

    public Mono<VideoData> findByIdWithNoData(String id) {
        Query query = Query.query(Criteria.where(BasicEntity.Fields.id).is(id));
        query.fields().exclude(VideoData.Fields.data);

        return template.findOne(query, VideoData.class);
    }


    public Flux<VideoData> findByOwnerId(String ownerId, Pageable pageable) {

        Query query = Query.query(Criteria.where(VideoData.Fields.ownerId).is(ownerId)).with(pageable);
        query.fields().exclude(VideoData.Fields.data);
        return template.find(query, VideoData.class);

    }


    public Mono<Void> deleteAllByOwnerId(String ownerId, List<String> ids) {


        return Flux.fromStream(ids.stream())
                .flatMap(id -> repository.deleteByIdAndOwnerId(id, ownerId))
                .then();


    }
}
