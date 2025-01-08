package com.koreait.store.controller;

import com.koreait.store.dto.ChatDTO;
import com.koreait.store.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatbot")
public class ChatbotRestController {
    @Autowired private ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<String> post_chatbot(
            @RequestBody String sentence
    ){
        ChatDTO chat = chatbotService.send_question_and_receive_answer(sentence);
        return ResponseEntity.ok(chat.getAnswer());
    }
}
