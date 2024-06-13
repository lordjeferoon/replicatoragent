package com.hacom.replicatoragent.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.hacom.replicatoragent.actor.ReplicatorActor;
import com.hacom.replicatoragent.config.DBManager;
import com.hacom.replicatoragent.service.ReplicatorQueue;
import com.hacom.replicatoragent.util.ReplicatorObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/replicator")
public class ReplicatorController {

    private final ReplicatorQueue replicatorQueue;
    private final ActorRef replicatorActor;

    public ReplicatorController(ReplicatorQueue replicatorQueue, ActorSystem actorSystem, DBManager dbManager) {
        this.replicatorQueue = replicatorQueue;
        this.replicatorActor = actorSystem.actorOf(ReplicatorActor.props(replicatorQueue, dbManager));
    }

    @PostMapping("/process")
    public ResponseEntity<String> processReplicatorObject(@RequestBody ReplicatorObject replicatorObject) {
        log.info("Se recibió una solicitud para procesar ReplicatorObject para la Colección: {} con Operación: {}.", replicatorObject.getCollection(), replicatorObject.getOperation());

        try {
            replicatorQueue.addToQueue(replicatorObject);
            log.info("ReplicatorObject para la Colección: {} con Operación: {} agregado a la cola exitosamente.", replicatorObject.getCollection(), replicatorObject.getOperation());

            replicatorActor.tell(new ReplicatorActor.ProcessQueue(), ActorRef.noSender());
            log.info("ReplicatorActor notificado para procesar la cola.");

            return ResponseEntity.status(HttpStatus.OK).body("ReplicatorObject para la Colección: " + replicatorObject.getCollection() + " con Operación: " + replicatorObject.getOperation() + " recibido y agregado a la cola exitosamente.");
        } catch (Exception e) {
            log.error("No se pudo procesar ReplicatorObject para la Colección: {} con Operación {}. Error: {}", replicatorObject.getCollection(), replicatorObject.getOperation(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo procesar ReplicatorObject.");
        }
    }

}