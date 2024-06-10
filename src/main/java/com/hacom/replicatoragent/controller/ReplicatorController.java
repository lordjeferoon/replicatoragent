package com.hacom.replicatoragent.controller;

import com.hacom.replicatoragent.service.ReplicatorService;
import com.hacom.replicatoragent.util.ReplicatorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/replicator")
public class ReplicatorController {

    private final ReplicatorService replicatorService;

    public ReplicatorController(ReplicatorService replicatorService) {
        this.replicatorService = replicatorService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processReplicatorObject(@RequestBody ReplicatorObject replicatorObject) {
        replicatorService.processDocument(replicatorObject.getCollection(), replicatorObject.getOperation(), replicatorObject.getDocument());
        return ResponseEntity.status(HttpStatus.OK).body("Objeto recibido y procesado exitosamente.");
    }
}
