package com.hacom.replicatoragent.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hacom.replicatoragent.actor.MyActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

@Configuration
public class AppConfig {
	
	@Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create("MyActorSystem");
    }

    @Bean
    public ActorRef myActor(ActorSystem actorSystem) {
        return actorSystem.actorOf(Props.create(MyActor.class), "myActor");
    }
    
    public static Props props() {
        return Props.create(MyActor.class);
    }

}
