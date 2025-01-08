package com.koreait.store.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnsInfoDTO {
    private String id;
    private String userId;
    private String ci;
    private String name;
    private LocalDateTime connectedDate;
}
