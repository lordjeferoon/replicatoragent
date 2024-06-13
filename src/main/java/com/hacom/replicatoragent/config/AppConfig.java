package com.hacom.replicatoragent.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

@Configuration
public class AppConfig {
	
	@Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create("MyActorSystem");
    }

}
