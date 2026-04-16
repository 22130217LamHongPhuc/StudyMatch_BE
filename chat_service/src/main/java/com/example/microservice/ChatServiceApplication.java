package com.example.microservice;
import com.example.microservice.dto.SendMessageRequest;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.PrivateConversationRepo;
import com.example.microservice.services.ChatService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ChatServiceApplication {
	private  MessageRepo messageRepo;
	private  PrivateConversationRepo conversationRepo;

	public static void main(String[] args) {
//		SpringApplication.run(ChatServiceApplication.class, args);

		ApplicationContext context = SpringApplication.run(ChatServiceApplication.class, args);
		ChatService service = context.getBean(ChatService.class);
		SendMessageRequest sendMessageRequest= new SendMessageRequest();
		sendMessageRequest.setConversationId(1);
		sendMessageRequest.setContent("heloo 2 nef");
		sendMessageRequest.setSenderId(1);
		sendMessageRequest.setType("TEXT");

		System.out.println(service.saveMess(sendMessageRequest));


	}



}
