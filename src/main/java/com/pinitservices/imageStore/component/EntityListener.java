package com.pinitservices.imageStore.component;

import com.pinitservices.imageStore.model.DataEntity;
import com.pinitservices.imageStore.model.ImageData;
import com.pinitservices.imageStore.model.VideoData;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Log
@Component
public class EntityListener extends AbstractMongoEventListener<DataEntity> {

    @Value("${serverUrl}")
    private String url;


    @Override
    public void onBeforeConvert(BeforeConvertEvent<DataEntity> event) {
        var entity = event.getSource();
        if (entity instanceof ImageData imageData) {
            if (imageData.getId() == null) {
                imageData.setId(new ObjectId().toString());
            }
            imageData.setUrl(imageUrl(imageData.getId()));

        } else if (entity instanceof VideoData videoData) {
            if (videoData.getId() == null) {
                videoData.setId(new ObjectId().toString());
            }
            videoData.setUrl(videoUrl(videoData.getId()));
        }
        super.onBeforeConvert(event);
    }


    public String videoUrl(String videoId) {
        return url + "video/" + videoId;
    }

    public String imageUrl(String imageId) {
        return url + "image/" + imageId;
    }
}
