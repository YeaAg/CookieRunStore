package com.koreait.store.controller;

import com.koreait.store.dto.CartDTO;
import com.koreait.store.dto.OrderDTO;
import com.koreait.store.dto.ProductDTO;
import com.koreait.store.dto.UserDTO;
import com.koreait.store.service.OrderService;
import com.koreait.store.service.PortOneService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Log4j2
@RestController
public class OrderRestController {
    @Autowired private OrderService orderService;
    @Autowired private PortOneService portOneService;

    // 장바구니에 상품을 추가하기
    @PostMapping("/cart")
    public ResponseEntity<Integer> post_cart(
            @RequestBody ProductDTO product,
            @AuthenticationPrincipal UserDTO userDTO
    ){
        if (Objects.isNull(userDTO)){
            log.warn("로그인 되지 않은 유저가 장바구니 삽입을 시도");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 해당 상품과 유저를 전달해서 카트에 추가하기
        CartDTO createdCart = orderService.add_cart(product, userDTO);
        return ResponseEntity.ok().body(createdCart.getNo());
    }

    // 카트의 상품 수량을 변경하기
    @PatchMapping("/cart/{cartNO}")
    public ResponseEntity<Void> patch_cart(
            @PathVariable Integer cartNO,
            @RequestBody Integer amount,
            @AuthenticationPrincipal UserDTO user
    ){
        orderService.change_cart_amount(cartNO, amount, user);
        return ResponseEntity.ok().build();
    }

    // 카트의 상품 제거하기
    @DeleteMapping("/cart/{cartNO}")
    public ResponseEntity<Void> delete_cart(
            @PathVariable Integer cartNO,
            @AuthenticationPrincipal UserDTO user
    ){
        orderService.remove_cart(cartNO, user);
        return ResponseEntity.ok().build();
    }

    /***************** 주문 ******************/
    // 하나의 주문 정보를 가져온다
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderDTO> get_order(
            @AuthenticationPrincipal UserDTO userDTO,
            @PathVariable("orderId") String orderId
    ){
        if (Objects.isNull(userDTO)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OrderDTO order = orderService.get_order(userDTO.getId(), orderId);
        log.info("조회된 ORDER: " + order);
        return ResponseEntity.ok().body(order);
    }

    // 사용자가 상품 or 장바구니 창에서 주문하기 버튼을 눌렀음
    @PostMapping("/order/prepare")
    public ResponseEntity<Void> post_order_prepare(
            @RequestBody List<CartDTO> carts, // 주문하려고하는 상품의 목록 (no와 amount만 가지고 있음)
            HttpSession session,
            @AuthenticationPrincipal UserDTO userDTO
    ){
        if(Objects.isNull(userDTO)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 주문하려고 하는 상품의 실제 데이터들을 DB에서 조회해서 와야함
        List<CartDTO> existsCarts = orderService.get_carts_from_database(carts);
        session.setAttribute("carts", existsCarts);
        log.error("최종 주문 내용:" + existsCarts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 사용자가 주문 페이지에서 상품을 주문
    @PostMapping("/order")
    public ResponseEntity<Void> post_order(
            @RequestBody OrderDTO orderDTO,
            @SessionAttribute("carts") List<CartDTO> carts, // 미리 설정해놓은 주문할 상품들
            @AuthenticationPrincipal UserDTO user
    ){
        orderDTO.setUser(user); // 주문하려는 유저를 설정
        orderDTO.setCarts(carts); // 주문하려는 상품 정보를 등록
        // 전달된 imp_uid 값이 잘못되었거나, Portone과 연계가 실패(요청실패) 했을 경우 NULL
        OrderDTO paymentInfo = portOneService.payments_authentication(orderDTO.getImpUid());
        // imp_uid로 조회한 주문번호가 사용자가 주문 페이지에서 전달한 주문번호와 다르다?
        if(Objects.isNull(paymentInfo) || !paymentInfo.getId().equals(orderDTO.getId())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // 기존에 주문하려고 하는 상품을 DB에서 조회하고, 실제 결제 내역과 비교
        boolean result = orderService.check_order_total_price(orderDTO, paymentInfo);
        if(!result){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        // 결제가 정확하다면 주문 이력에 추가해야 한다
        try {
            log.error("orderDTO: " + orderDTO);
            orderService.add_order(orderDTO);
        }catch (Exception e){
            log.error("주문 이력 추가 중 에러 발생!: " + e.getMessage());
            // 최종 주문 완료
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // 최종 주문 완료
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
