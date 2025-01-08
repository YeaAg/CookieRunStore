package com.koreait.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreait.store.dto.ChatDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Log4j2
@Service
public class ChatbotService {
    @Autowired private ObjectMapper objectMapper;
    String SERVER_IP = "127.0.0.1";
    Integer SERVER_PORT = 5050;

    public ChatDTO send_question_and_receive_answer(String sentence){
        // try - with - resources
        try(
                // 소켓을 생성하는 순간,! 챗봇 서버와 연결 (bind)됩니다
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                // 소켓연결이 되면 아래 코드들이 실행됨
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ){
            log.info("질문 전송 중....");
            writer.println(sentence);
            log.info("질문 전송 완료!");
            String answer = reader.readLine();
            ChatDTO chat = objectMapper.readValue(answer, ChatDTO.class);
            if(chat.getIntent().equals("상담")){
                chat.setAnswer("상담사 불러주까? 여기로 와봐 <a href='#'>클릭~</a>");
            }
            log.info("CHATBOT SERVER ANSWER: " + chat);
            return chat;
        }
        catch (IOException e){
            log.error("CHATBOT SERVER SEND ERROR: " + e.getMessage());
        }
        catch (Exception e){
            log.error("JSON ERROR: " + e.getMessage());
        }
        return ChatDTO.builder().answer("챗봇 서버가 불안정한 것 같아요 ㅠㅠ").build();
    }
}
