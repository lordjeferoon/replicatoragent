package com.hacom.replicatoragent.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class ReplicatorObject {
    String collection;
    String operation;
    Object document;

    public static ReplicatorObject fromString(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, ReplicatorObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
