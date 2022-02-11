package com.pinitservices.imageStore.model;

import com.pinitservices.imageStore.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

@Getter
@Setter
@ToString
@FieldNameConstants
public class VideoData extends BasicEntity {

    private byte[] data;
    private String type;
    private String ownerId;
    private long length;
    private String fileName;

    public static Mono<VideoData> from(FilePart filePart) {
        return filePart.content()
                .map(buff -> buff.asByteBuffer().array())
                .reduce(Utils::concat)
                .map(VideoData::create)
                .doOnNext(e -> e.setFileName(filePart.filename()));
    }

    public static VideoData create(byte[] data) {
        VideoData video = new VideoData();
        video.setData(data);
        video.setType(Utils.TIKA.detect(data));
        return video;
    }
}
