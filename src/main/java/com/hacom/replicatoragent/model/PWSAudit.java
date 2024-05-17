package com.hacom.replicatoragent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "PWSAudit")
public class PWSAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String username;
    private String module;
    private String action;
    private String description;
    private LocalDateTime eventTime;

}