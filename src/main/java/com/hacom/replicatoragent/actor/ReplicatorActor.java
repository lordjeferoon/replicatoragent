package com.hacom.replicatoragent.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.hacom.replicatoragent.config.DBManager;
import com.hacom.replicatoragent.service.ReplicatorQueue;
import com.hacom.replicatoragent.util.ReplicatorObject;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;

@Log4j2
public class ReplicatorActor extends AbstractActor {
    private final ReplicatorQueue replicatorQueue;
    private final DBManager dbManager;

    public ReplicatorActor(ReplicatorQueue replicatorQueue, DBManager dbManager) {
        this.replicatorQueue = replicatorQueue;
        this.dbManager = dbManager;
        getContext().getSystem().log().info("Actor {} initialized", getSelf().path().name());
    }

    public static Props props(ReplicatorQueue replicatorQueue, DBManager dbManager) {
        return Props.create(ReplicatorActor.class, () -> new ReplicatorActor(replicatorQueue, dbManager));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessQueue.class, msg -> processQueue())
                .build();
    }

    private void processQueue() {
        while (!replicatorQueue.isEmpty()) {
            ReplicatorObject replicatorObject = replicatorQueue.pollFromQueue();
            String operation = replicatorObject.getOperation();
            log.info("ReplicatorActor procesando ReplicatorObject para la Colección: {} con Operación: {}.", replicatorObject.getCollection(), replicatorObject.getOperation());
            switch (operation) {
                case "insert":
                    dbManager.insertDocument(replicatorObject);
                    break;
                case "update":
                    dbManager.updateDocument(replicatorObject);
                    break;
                case "delete":
                    dbManager.deleteDocument(replicatorObject);
                    break;
                default:
                    System.out.println("Operación no válida: " + operation);
                    break;
            }
        }
    }

    public static class ProcessQueue {
        // Mensaje para indicar al actor que procese la cola
    }
}


