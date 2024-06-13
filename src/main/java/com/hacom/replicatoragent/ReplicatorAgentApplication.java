package com.hacom.replicatoragent;

import com.hacom.replicatoragent.actor.ReplicatorActor;
import com.hacom.replicatoragent.config.DBManager;

import com.hacom.replicatoragent.service.ReplicatorQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

@SpringBootApplication
public class ReplicatorAgentApplication {
    private final ReplicatorQueue replicatorQueue;
    private final DBManager dbManager;

    @Value("${customconfig.utcZoneId}")
    private String utcZoneId;

    @Value("${info.application.datacenter}")
    private String dataCenter;

    @Value("${info.application.short-name}")
    private String module;

    public ReplicatorAgentApplication(ReplicatorQueue replicatorQueue, DBManager dbManager) {
        this.replicatorQueue = replicatorQueue;
        this.dbManager = dbManager;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReplicatorAgentApplication.class, args);
        ActorSystem actorSystem = context.getBean(ActorSystem.class);
        ActorRef ActorPWSReplicator = context.getBean("ActorPWSReplicator", ActorRef.class);
        ActorPWSReplicator.tell("", ActorRef.noSender());
    }
	
    @Bean
    public ActorSystem actorSystem() {
    	Config config = ConfigFactory.parseString(
                "akka {\n" +
                "  loglevel = " + "DEBUG" + "\n" +
                "  stdout-loglevel = " + "DEBUG" + "\n" +
                "  actor {\n" +
                "    default-dispatcher {\n" +
                "      fork-join-executor {\n" +
                "        parallelism-min = " + "8" + "\n" +
                "        parallelism-factor = " + "5.0" + "\n" +
                "        parallelism-max = " + "64" + "\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");
    	
        return ActorSystem.create("MyActorSystem",config);
    }

    @Bean
    public ActorRef ActorPWSReplicator(ActorSystem actorSystem) {
        return actorSystem.actorOf(ReplicatorActor.props(replicatorQueue, dbManager), "ActorPWSReplicator");
    }

}
