package com.hacom.replicatoragent.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.hacom.replicatoragent.actor.SchedulerActor;

@Configuration
public class TaskSchedulerConfig {
	
	private ActorSystem actorSystem;

    @Autowired
    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }
	
	@Autowired
    private DatabaseManager databaseManager;
	
	@Bean
    public ActorRef schedulerActor() {
		return this.actorSystem.actorOf(SchedulerActor.props(databaseManager.getRocksDB(),databaseManager.getDatabase_r()), "schedulerActor");
    }
	
	@Bean
    public ScheduledExecutorService scheduledExecutorService() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::executeTask, 0, 600000, TimeUnit.MILLISECONDS); //Ejecución cada 10 minutos
        return scheduler;
    }

    private void executeTask() {
    	this.actorSystem.actorSelection("/user/schedulerActor").tell("TAREA", ActorRef.noSender());
    }

    /*private long calculateDelay() {
        LocalDate today = LocalDate.now();
        LocalTime executionTime = LocalTime.of(00, 20); // Hora de ejecución deseada
        LocalDateTime nextExecution = LocalDateTime.of(today, executionTime);

        long initialDelay = ChronoUnit.MILLIS.between(LocalDateTime.now(), nextExecution);
        
        if (initialDelay < 0) {
            LocalDateTime tomorrow = LocalDateTime.of(today.plusDays(1), executionTime);
            initialDelay = ChronoUnit.MILLIS.between(LocalDateTime.now(), tomorrow);
        }
        return initialDelay;
    }*/

}
