package com.koreait.store.controller;

import com.koreait.store.dto.UserDTO;
import com.koreait.store.service.UserRestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserRestController {
    @Autowired
    private UserRestService userRestService;

    @GetMapping("/id/{userId}")
    public ResponseEntity<Void> get_userId(
            @PathVariable("userId") String userId
    ){
        if(userRestService.find_user_by_id(userId)){
            return ResponseEntity.ok().build(); // 200
        }
        return ResponseEntity.status(HttpStatus.FOUND).build(); // 302
    }
}
