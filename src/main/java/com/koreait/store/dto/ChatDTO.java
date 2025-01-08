package com.koreait.store.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    String query;
    String answer;
    String intent;
}
