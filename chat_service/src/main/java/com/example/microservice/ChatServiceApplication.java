package com.example.microservice;
import com.example.microservice.dto.SendMessageRequest;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.PrivateConversationRepo;
import com.example.microservice.services.ChatService;
import com.example.microservice.services.MessageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ChatServiceApplication {
	private  MessageRepo messageRepo;
	private  PrivateConversationRepo conversationRepo;

	public static void main(String[] args) {
//		SpringApplication.run(ChatServiceApplication.class, args);

		ApplicationContext context = SpringApplication.run(ChatServiceApplication.class, args);
		MessageService service = context.getBean(MessageService.class);
		Pageable pageable = (Pageable) PageRequest.of(0, 10);
		Page<Message> messagePage = service.getConversation(1L, pageable);
		System.out.println("Trang hiện tại: " + messagePage.getNumber());
		System.out.println("Kích thước trang: " + messagePage.getSize());
		System.out.println("Tổng số phần tử: " + messagePage.getTotalElements());
		System.out.println("Tổng số trang: " + messagePage.getTotalPages());
		System.out.println("Danh sách tin nhắn: " + messagePage.getContent());
//		System.out.println(service.getConversation(1L, pageable));

//		SendMessageRequest sendMessageRequest= new SendMessageRequest();
//		sendMessageRequest.setConversationId(1);
//		sendMessageRequest.setContent("heloo 2 nef");
//		sendMessageRequest.setSenderId(1);
//		sendMessageRequest.setType("TEXT");
//
//		System.out.println(service.saveMess(sendMessageRequest));
	}



}
