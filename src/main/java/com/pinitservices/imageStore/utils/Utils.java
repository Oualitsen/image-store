package com.pinitservices.imageStore.utils;

import org.apache.tika.Tika;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Mono;

public class Utils {
    public static final Tika TIKA = new Tika();

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static Mono<String> partToString(Part part) {
        return part.content().map(buff -> buff.asByteBuffer().array())
                .reduce(Utils::concat)
                .map(String::new);
    }
}
