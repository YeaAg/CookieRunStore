package com.koreait.store.mapper;

import com.koreait.store.dto.CartDTO;
import com.koreait.store.dto.OrderDTO;
import com.koreait.store.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    /***************** 주문 ****************/
    List<CartDTO> selectCartsByNumberAndUser( // 주문하려고 하는 장바구니 상품들 가져오기
            List<Integer> cartNumbers,
            UserDTO user
    );
    /*
        1) ORDER - INSERT (주문정보만)
        2) ORDER_ID 알고있어서 조회 따로 안해도 됨
        3) PRODUCTS - INSERT (주문에 들어간 상품들) - PK가 AI
        => 자바에서 for 로 INSERT 각각..(mybatis foreach말고)
        => SELECT LAST_ID까지
        4) OPTIONS - INSERT (주문에 들어간 상품에 대한 옵션들)
        => 직전에 PRODUCTS 리스트를 사용
     */

    List<OrderDTO> selectOrders(String userId, String orderId);
    void insertOrder(OrderDTO order);
    void insertOrderProductsAndOptions(String orderJsonString); // json 문자열을 전달해서 프로시저 호출

    /***************** 장바구니 ****************/
    List<CartDTO> selectCartsByUser(UserDTO user);

    void insertCart(CartDTO cart); // 장바구니에 상품 정보 추가
    void insertCartOptions(CartDTO cart); // 장바구니에 상품의 옵션 정보 추가

    void updateCartAmount(CartDTO cart); // 장바구니의 해당 장바구니 수량 변경
    void deleteCartByNoAndUser(CartDTO cart); // 장바구니의 해당 상품 제거

    void deleteCartsByNoAndUser(List<CartDTO> carts, UserDTO user); // 해당하는 모든 장바구니 상품들 제거
}
