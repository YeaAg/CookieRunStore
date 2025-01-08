package com.koreait.store.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Integer no;
    private String originalFilename;
    @ToString.Exclude  // = @ToString(exclude = "data")
    private byte[] data;

//    public String getData() {
//        return Base64.getEncoder().encodeToString(data);
//    }
}
