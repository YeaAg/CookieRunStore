package com.koreait.store.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewDTO {
    private Integer productNo; // 리뷰를 등록한 상품명
    private String userId; // 리뷰를 등록한 사람
    private String text; // 리뷰 내용
    private Integer rate; // 별점
}
