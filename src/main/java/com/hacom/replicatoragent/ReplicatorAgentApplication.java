package com.hacom.replicatoragent;

import org.rocksdb.RocksDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.hacom.replicatoragent.actor.MyActor;
import com.hacom.replicatoragent.config.DatabaseManager;
import com.mongodb.client.MongoDatabase;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

@SpringBootApplication
public class ReplicatorAgentApplication {
	
	RocksDB rocksDB = null;
    
    MongoDatabase database = null;
    MongoDatabase database_r = null;

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ReplicatorAgentApplication.class, args);
		
        ActorSystem actorSystem = context.getBean(ActorSystem.class);
        
        ActorRef ActorPWSAccount = context.getBean("ActorPWSAccount",ActorRef.class);
        ActorRef ActorPWSAccountCBoundary01 = context.getBean("ActorPWSAccountCBoundary01",ActorRef.class);
        ActorRef ActorPWSAccountState = context.getBean("ActorPWSAccountState",ActorRef.class);
        ActorRef ActorPWSAlert = context.getBean("ActorPWSAlert",ActorRef.class);
        ActorRef ActorPWSAlertBroadcastList = context.getBean("ActorPWSAlertBroadcastList",ActorRef.class);
        ActorRef ActorPWSAlertCategory = context.getBean("ActorPWSAlertCategory",ActorRef.class);
        ActorRef ActorPWSAlertDescription = context.getBean("ActorPWSAlertDescription",ActorRef.class);
        ActorRef ActorPWSAlertInstruction = context.getBean("ActorPWSAlertInstruction",ActorRef.class);
        ActorRef ActorPWSAlertReport = context.getBean("ActorPWSAlertReport",ActorRef.class);
        ActorRef ActorPWSAlertStatus = context.getBean("ActorPWSAlertStatus",ActorRef.class);
        ActorRef ActorPWSAlertStatusGroupedLocation = context.getBean("ActorPWSAlertStatusGroupedLocation",ActorRef.class);
        ActorRef ActorPWSAlertStatusOLD = context.getBean("ActorPWSAlertStatusOLD",ActorRef.class);
        ActorRef ActorPWSAudit = context.getBean("ActorPWSAudit",ActorRef.class);
        ActorRef ActorPWSBroadcastCenter = context.getBean("ActorPWSBroadcastCenter",ActorRef.class);
        ActorRef ActorPWSBroadcastElement = context.getBean("ActorPWSBroadcastElement",ActorRef.class);
        ActorRef ActorPWSCategoryEvent = context.getBean("ActorPWSCategoryEvent",ActorRef.class);
        ActorRef ActorPWSCBoundary01 = context.getBean("ActorPWSCBoundary01",ActorRef.class);
        ActorRef ActorPWSCustomConfig = context.getBean("ActorPWSCustomConfig",ActorRef.class);
        ActorRef ActorPWSEvent = context.getBean("ActorPWSEvent",ActorRef.class);
        ActorRef ActorPWSEventAlert = context.getBean("ActorPWSEventAlert",ActorRef.class);
        ActorRef ActorPWSEventSeverity = context.getBean("ActorPWSEventSeverity",ActorRef.class);
        ActorRef ActorPWSNotificationGroup = context.getBean("ActorPWSNotificationGroup",ActorRef.class);
        ActorRef ActorPWSNotificationList = context.getBean("ActorPWSNotificationList",ActorRef.class);
        ActorRef ActorPWSOperator = context.getBean("ActorPWSOperator",ActorRef.class);
        ActorRef ActorPWSRefreshToken = context.getBean("ActorPWSRefreshToken",ActorRef.class);
        ActorRef ActorPWSRol = context.getBean("ActorPWSRol",ActorRef.class);
        ActorRef ActorPWSSenderIn = context.getBean("ActorPWSSenderIn",ActorRef.class);
        ActorRef ActorPWSSenderInEndpoint = context.getBean("ActorPWSSenderInEndpoint",ActorRef.class);
        ActorRef ActorPWSSenderUser = context.getBean("ActorPWSSenderUser",ActorRef.class);
        ActorRef ActorPWSState = context.getBean("ActorPWSState",ActorRef.class);
        ActorRef ActorPWSTemplateAlertCategoryEvents = context.getBean("ActorPWSTemplateAlertCategoryEvents",ActorRef.class);
        ActorRef ActorPWSTemplateCAP = context.getBean("ActorPWSTemplateCAP",ActorRef.class);
        ActorRef ActorPWSTransmissionConfig = context.getBean("ActorPWSTransmissionConfig",ActorRef.class);
        ActorRef ActorPWSUser = context.getBean("ActorPWSUser",ActorRef.class);
        ActorRef ActorPWSUserAccountCBoundary01 = context.getBean("ActorPWSUserAccountCBoundary01",ActorRef.class);
        ActorRef ActorPWSUserAccountState = context.getBean("ActorPWSUserAccountState",ActorRef.class);
        ActorRef ActorPWSZipCodes = context.getBean("ActorPWSZipCodes",ActorRef.class);

        ActorPWSAccount.tell("PWSAccount", ActorRef.noSender());
        ActorPWSAccountCBoundary01.tell("PWSAccountCBoundary01", ActorRef.noSender());
        ActorPWSAccountState.tell("PWSAccountState", ActorRef.noSender());
        ActorPWSAlert.tell("PWSAlert", ActorRef.noSender());
        ActorPWSAlertBroadcastList.tell("PWSAlertBroadcastList", ActorRef.noSender());
        ActorPWSAlertCategory.tell("PWSAlertCategory", ActorRef.noSender());
        ActorPWSAlertDescription.tell("PWSAlertDescription", ActorRef.noSender());
        ActorPWSAlertInstruction.tell("PWSAlertInstruction", ActorRef.noSender());
        ActorPWSAlertReport.tell("PWSAlertReport", ActorRef.noSender());
        ActorPWSAlertStatus.tell("PWSAlertStatus", ActorRef.noSender());
        ActorPWSAlertStatusGroupedLocation.tell("PWSAlertStatusGroupedLocation", ActorRef.noSender());
        ActorPWSAlertStatusOLD.tell("PWSAlertStatusOLD", ActorRef.noSender());
        ActorPWSAudit.tell("PWSAudit", ActorRef.noSender());
        ActorPWSBroadcastCenter.tell("PWSBroadcastCenter", ActorRef.noSender());
        ActorPWSBroadcastElement.tell("PWSBroadcastElement", ActorRef.noSender());
        ActorPWSCategoryEvent.tell("PWSCategoryEvent", ActorRef.noSender());
        ActorPWSCBoundary01.tell("PWSCBoundary01", ActorRef.noSender());
        ActorPWSCustomConfig.tell("PWSCustomConfig", ActorRef.noSender());
        ActorPWSEvent.tell("PWSEvent", ActorRef.noSender());
        ActorPWSEventAlert.tell("PWSEventAlert", ActorRef.noSender());
        ActorPWSEventSeverity.tell("PWSEventSeverity", ActorRef.noSender());
        ActorPWSNotificationGroup.tell("PWSNotificationGroup", ActorRef.noSender());
        ActorPWSNotificationList.tell("PWSNotificationList", ActorRef.noSender());
        ActorPWSOperator.tell("PWSOperator", ActorRef.noSender());
        ActorPWSRefreshToken.tell("PWSRefreshToken", ActorRef.noSender());
        ActorPWSRol.tell("PWSRol", ActorRef.noSender());
        ActorPWSSenderIn.tell("PWSSenderIn", ActorRef.noSender());
        ActorPWSSenderInEndpoint.tell("PWSSenderInEndpoint", ActorRef.noSender());
        ActorPWSSenderUser.tell("PWSSenderUser", ActorRef.noSender());
        ActorPWSState.tell("PWSState", ActorRef.noSender());
        ActorPWSTemplateAlertCategoryEvents.tell("PWSTemplateAlertCategoryEvents", ActorRef.noSender());
        ActorPWSTemplateCAP.tell("PWSTemplateCAP", ActorRef.noSender());
        ActorPWSTransmissionConfig.tell("PWSTransmissionConfig", ActorRef.noSender());
        ActorPWSUser.tell("PWSUser", ActorRef.noSender());
        ActorPWSUserAccountCBoundary01.tell("PWSUserAccountCBoundary01", ActorRef.noSender());
        ActorPWSUserAccountState.tell("PWSUserAccountState", ActorRef.noSender());
        ActorPWSZipCodes.tell("PWSZipCodes", ActorRef.noSender());
		
        //SpringApplication.run(ReplicatorAgentApplication.class, args);
	}

	@Autowired
    private DatabaseManager databaseManager;
	
	
    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create("MyActorSystem");
    }

    @Bean
    public ActorRef ActorPWSAccount(ActorSystem actorSystem) {
        return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAccount");
    }
    
    @Bean
    public ActorRef ActorPWSAccountCBoundary01(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAccountCBoundary01");
    }
    
    @Bean
    public ActorRef ActorPWSAccountState(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAccountState");
    }
    
    @Bean
    public ActorRef ActorPWSAlert(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlert");
    }
    
    @Bean
    public ActorRef ActorPWSAlertBroadcastList(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertBroadcastList");
    }
    
    @Bean
    public ActorRef ActorPWSAlertCategory(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertCategory");
    }
    
    @Bean
    public ActorRef ActorPWSAlertDescription(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertDescription");
    }
    
    @Bean
    public ActorRef ActorPWSAlertInstruction(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertInstruction");
    }
    
    @Bean
    public ActorRef ActorPWSAlertReport(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertReport");
    }
    
    @Bean
    public ActorRef ActorPWSAlertStatus(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertStatus");
    }
    
    @Bean
    public ActorRef ActorPWSAlertStatusGroupedLocation(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertStatusGroupedLocation");
    }
    
    @Bean
    public ActorRef ActorPWSAlertStatusOLD(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAlertStatusOLD");
    }
    
    @Bean
    public ActorRef ActorPWSAudit(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSAudit");
    }
    
    @Bean
    public ActorRef ActorPWSBroadcastCenter(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSBroadcastCenter");
    }
    
    @Bean
    public ActorRef ActorPWSBroadcastElement(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSBroadcastElement");
    }

    @Bean
    public ActorRef ActorPWSCategoryEvent(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSCategoryEvent");
    }
    
    @Bean
    public ActorRef ActorPWSCBoundary01(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSCBoundary01");
    }
    
    @Bean
    public ActorRef ActorPWSCustomConfig(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSCustomConfig");
    }
    
    @Bean
    public ActorRef ActorPWSEvent(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSEvent");
    }
    
    @Bean
    public ActorRef ActorPWSEventAlert(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSEventAlert");
    }
    
    @Bean
    public ActorRef ActorPWSEventSeverity(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSEventSeverity");
    }
    
    @Bean
    public ActorRef ActorPWSNotificationGroup(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSNotificationGroup");
    }
    
    @Bean
    public ActorRef ActorPWSNotificationList(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSNotificationList");
    }
    
    @Bean
    public ActorRef ActorPWSOperator(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSOperator");
    }
    
    @Bean
    public ActorRef ActorPWSRefreshToken(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSRefreshToken");
    }
    
    @Bean
    public ActorRef ActorPWSRol(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSRol");
    }

    @Bean
    public ActorRef ActorPWSSenderIn(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSSenderIn");
    }
    
    @Bean
    public ActorRef ActorPWSSenderInEndpoint(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSSenderInEndpoint");
    }
    
    @Bean
    public ActorRef ActorPWSSenderUser(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSSenderUser");
    }
    
    @Bean
    public ActorRef ActorPWSState(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSState");
    }
    
    @Bean
    public ActorRef ActorPWSTemplateAlertCategoryEvents(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSTemplateAlertCategoryEvents");
    }
    
    @Bean
    public ActorRef ActorPWSTemplateCAP(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSTemplateCAP");
    }
    
    @Bean
    public ActorRef ActorPWSTransmissionConfig(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSTransmissionConfig");
    }
    
    @Bean
    public ActorRef ActorPWSUser(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSUser");
    }
    
    @Bean
    public ActorRef ActorPWSUserAccountCBoundary01(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSUserAccountCBoundary01");
    }
    
    @Bean
    public ActorRef ActorPWSUserAccountState(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSUserAccountState");
    }
    
    @Bean
    public ActorRef ActorPWSZipCodes(ActorSystem actorSystem) {
    	return actorSystem.actorOf(MyActor.props(databaseManager.getRocksDB(), databaseManager.getDatabase(), databaseManager.getDatabase_r()), "ActorPWSZipCodes");
    }

	@Bean
	public WebClient webClient() {
		final int size = 100 * 1024 * 1024;
		final ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
				.build();
		return WebClient.builder()
				.exchangeStrategies(strategies)
				.build();
	}
}
