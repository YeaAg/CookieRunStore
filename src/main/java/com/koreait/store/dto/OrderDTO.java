package com.koreait.store.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class OrderDTO {
    private String id; // 주문번호
    private UserDTO user; // 주문한 유저
    private String impUid; // Portone 고유 번호
    private String name; // 주문명
    private Integer totalPrice; // 총 금액
    private List<CartDTO> carts; // 주문한 상품들

    private String shippingName; // 배송 받는 분
    private String shippingTel; // 배송 받는 사람 전번
    private String shippingAddr; // 배송지1
    private String shippingAddrDetail; // 배송지2
    private String shippingPostcode; // 우편번호

    private String ordererName; // 주문자명
    private String ordererTel; // 주문자 전번
    private String ordererEmail; // 주문자 이메일

    private LocalDateTime orderedAt; // 주문 날짜/시각
}
