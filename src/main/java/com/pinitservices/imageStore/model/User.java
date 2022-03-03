package com.pinitservices.imageStore.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class User {

    private String userId;
    private List<Role> roles;

    public Map<String, Object> toMap() {
        return Map.of("id", userId, "roles", roles);
    }
}
