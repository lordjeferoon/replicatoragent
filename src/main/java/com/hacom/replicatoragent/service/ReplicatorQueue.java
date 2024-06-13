package com.hacom.replicatoragent.service;

import java.util.PriorityQueue;
import com.hacom.replicatoragent.util.ReplicatorObject;
import org.springframework.stereotype.Component;
import java.util.Comparator;

@Component
public class ReplicatorQueue {
    private final PriorityQueue<ReplicatorObject> queue;

    public ReplicatorQueue() {
        // Inicializa la cola con capacidad inicial y un comparador
        this.queue = new PriorityQueue<>(new ReplicatorObjectComparator());
    }

    public void addToQueue(ReplicatorObject replicatorObject) {
        queue.offer(replicatorObject); // Agrega el objeto a la cola
    }

    public ReplicatorObject pollFromQueue() {
        return queue.poll(); // Obtiene y elimina el primer elemento de la cola
    }

    public boolean isEmpty() {
        return queue.isEmpty(); // Verifica si la cola está vacía
    }

    // Clase interna para comparar ReplicatorObject según su prioridad
    private static class ReplicatorObjectComparator implements Comparator<ReplicatorObject> {
        @Override
        public int compare(ReplicatorObject o1, ReplicatorObject o2) {
            // Define la lógica de comparación basada en la prioridad de las operaciones
            switch (o1.getOperation()) {
                case "insert":
                    return -1; // Este objeto tiene mayor prioridad que 'other'

                case "update":
                    if (o2.getOperation().equals("insert")) {
                        return 1; // 'other' tiene mayor prioridad que este objeto
                    } else {
                        return -1; // Este objeto tiene mayor prioridad que 'other'
                    }
                case "delete":
                    if (o2.getOperation().equals("insert") || o2.getOperation().equals("update")) {
                        return 1; // 'other' tiene mayor prioridad que este objeto
                    } else {
                        return 0; // Igual prioridad
                    }
            }
            return 0; // En caso de igualdad o si las operaciones no son válidas
        }
    }
}

