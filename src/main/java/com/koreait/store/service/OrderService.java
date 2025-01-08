package com.koreait.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreait.store.dto.*;
import com.koreait.store.mapper.OrderMapper;
import com.koreait.store.mapper.ProductMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class OrderService {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private ProductMapper productMapper;

    // 해당 유저가 주문하려는 상품들을 장바구니에서 가져오기
    public List<CartDTO> get_carts_by_numbers(List<Integer> cartNumbers, UserDTO user) {
        return orderMapper.selectCartsByNumberAndUser(cartNumbers, user);
    }

    // 카트에 있는 상품들의 no와 옵션의 no와 같은 DB 데이터를 찾아오기
    public List<CartDTO> get_carts_from_database(List<CartDTO> carts){
        // 카트에 있는 상품들의 실제 DB데이터들 (key: 상품번호, value: 해당 상품)
        Map<Integer, ProductDTO> products = productMapper.selectProductsByCartProducts(carts);
        carts.stream().forEach(cart -> {
            ProductDTO cartProduct = cart.getProduct(); // 주문하려고 하는 상품
            if(!products.containsKey(cartProduct.getNo())) return;
            ProductDTO realProduct = products.get(cartProduct.getNo()).clone(); // DB에서 찾은 실제 상품
            // 내가 주문하려고 하는 옵션 값들만 따로 빼서 가져와야 함
            //// => 옵션의 수량까지 정확히 빼와야 하는 상황... 수정해야함
            TreeSet<ProductOptionDTO> realOptions = realProduct.getOptions();
            realOptions.forEach(option -> {option.setAmount(1);});
            realOptions.retainAll(cartProduct.getOptions());
            // 진짜 데이터의 상품에 주문하려고 하는 option만 넣어준다
            realProduct.setOptions(realOptions);
            // 실제 주문하려고하는 카트 데이터에 진짜 상품 데이터를 넣어준다
            cart.setProduct(realProduct);
        });
        return carts;
    }


    // 해당 유저의 장바구니 item들을 가져오기
    public List<CartDTO> get_carts_by_user(UserDTO user) {
        return orderMapper.selectCartsByUser(user);
    }

    /**
     * @param product: 유저가 추가하려는 상품 정보
     * @param user: 로그인된 유저
     */
    // 생성된 장바구니 객체들을 return
//    @Transactional
    public CartDTO add_cart(ProductDTO product, UserDTO user) {
        // 방법 1) (해쉬코드 써서) 수정 필요 ******************************
        List<CartDTO> existCarts = orderMapper.selectCartsByUser(user);
        for(var existCart : existCarts) {
            if (existCart.getProduct().hashCode() == product.hashCode()) {
                log.info("중복 상품! 수량 +1");
                change_cart_amount(existCart.getNo(), existCart.getAmount() + 1, user);
                return existCart;
            }
        }
        // 방법2) hashcode 사용X 버전
//        List<ProductOptionDTO> options = product.getOptions();
//        List<CartDTO> existCarts = orderMapper.selectCartsByUser(user);
//        for(var existCart : existCarts){
//            log.info("existCart: " + existCart);
//            ProductDTO existProduct = existCart.getProduct();
//            Integer existProductNo = existProduct.getNo();
//            List<ProductOptionDTO> existOptions = existProduct.getOptions();
//            if(existProductNo.equals(product.getNo())){
//                // 옵션이 없는데 같은 상품이 들어 있다면
//                // (현재 카트에도 옵션 없고, 조회된 카트도 없고 => 상품 번호만 비교하면 되니 수량 +1
//                if(options.isEmpty() && existOptions.isEmpty()){
//                    log.info("옵션 없는 중복 상품! 수량 +1");
//                    change_cart_amount(existCart.getNo(), existCart.getAmount() + 1, user);
//                    return existCart; // 수량 +1 해야함.. 지금은 일단 pass
//                }
//                // 만약 현재 카트에 넣는 상품에 옵션이 있으면 옵션까지 비교
//                // 현재 카트에도 옵션 있고, 조회된 카트도 옵션 있고, 두 옵션 개수가 동일할때만
//                if(!options.isEmpty() && options.size() == existOptions.size()){
//                    Set<Integer> existProductOptions = existOptions.parallelStream()
//                            .map(ProductOptionDTO::getNo)
//                            .collect(Collectors.toSet());
//                    log.error("existProductOptions: " + existProductOptions);
//
//                    // 중복 옵션만 모은다
//                    Set<Integer> duplicatedNumbers = options.parallelStream()
//                            .map(ProductOptionDTO::getNo)
//                            .filter(existProductOptions::contains)
//                            .collect(Collectors.toSet());
//
//                    // (중복 옵션의 개수 == 장바구니에 들어있는 개수) 라면, 해당 상품은 수량 +1
//                    if(duplicatedNumbers.size() == options.size()){
//                        log.info("옵션 있는 중복 상품! 수량 +1");
//                        change_cart_amount(existCart.getNo(), existCart.getAmount() + 1, user);
//                        return existCart; // 수량 +1 해야함.. 지금은 일단 pass
//                    }
//                }
//            }
//        };

        /*****************************************/

        // 여기까지 왔다는 것은 중복이 없었다는 뜻.
        log.info("중복 없음. 카트 추가");
        CartDTO cart = new CartDTO();
        cart.setProduct(product);
        cart.setUser(user);

        // 주문 이력 추가
        orderMapper.insertCart(cart);
        if (!product.getOptions().isEmpty()) {
            orderMapper.insertCartOptions(cart);
        }
        return cart;
    }

    // cartNo: 변경하려는 장바구니 no
    // amount: 변경하려는 수량
    // user: 변경하는 사람이 맞는지 체크
    public void change_cart_amount(Integer cartNo, Integer amount, UserDTO user) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUser(user);
        cartDTO.setNo(cartNo);
        cartDTO.setAmount(amount);
        orderMapper.updateCartAmount(cartDTO);
    }

    // 장바구니 상품 제거
    public void remove_cart(Integer cartNo, UserDTO user) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUser(user);
        cartDTO.setNo(cartNo);
        orderMapper.deleteCartByNoAndUser(cartDTO);
    }

    public void remove_carts(List<CartDTO> carts, UserDTO user) {
        orderMapper.deleteCartsByNoAndUser(carts, user);
    }

    /***********************************************/

    public List<OrderDTO> get_orders_by_user(String userId){
        List<OrderDTO> orders = orderMapper.selectOrders(userId, null);
        return orders;
    }

    public OrderDTO get_order(String userId, String orderId){
        List<OrderDTO> orders = orderMapper.selectOrders(userId, orderId);
        if(orders.isEmpty()) return null;
        return orders.get(0);
    }

    // Portone 결제 모듈로 결제한 금액과, 실제 주문하려는 상품들의 금액을 비교
    // order: 주문하려고 하는 상품 정보 / payment: 실제 결제 내역 정보
    public boolean check_order_total_price(OrderDTO order, OrderDTO payment){
        log.info("check_order_total_price - 체크중 == ");
//        // 주문하려고 하는 상품(장바구니) 리스트
//        List<CartDTO> carts = order.getCarts();
//        // 주문하려고 하는 상품(장바구니)
//        List<Integer> cartNumbers = carts.parallelStream().map(CartDTO::getNo).toList();
//        // 기존 장바구니에서 해당 장바구니 번호에 해당하는 것들만 가져오기
//        List<CartDTO> existsCarts = get_carts_by_numbers(cartNumbers, order.getUser());

        // 기존 상품에 해당하는 실제 데이터들을 가져오기
//        List<CartDTO> existsCarts = get_carts_from_database(order.getCarts());

        // 사용자가 주문하려고 하는 ORDER 정보의 Cart 정보를 Database에서 조회한 정보로 교체
        // (js에서 온 데이터는 cart의 no밖에 없기 때문에 주문하려는 상품의 정보가 없음)
        // 1) 장바구니에 들어있는 상품의 개수가 주문하려는 개수와는 다를 수 있으므로, 조회한 데이터에서 개수를 재세팅한다
        // => 만약 장바구니에 있는 카트 수량에서 상품 몇 개만 주문가능한 상태일 때는 넣어주세요ㅠㅠ
//        existsCarts.parallelStream().forEach(cartDTO -> {
//            carts.parallelStream().forEach(cart -> {
//                // order에 있는 카트번호와 DB의 카트번호가 같다면
//                if (cartDTO.getNo().equals(cart.getNo())) {
//                    // DB의 카트번호의 amount(수량)을 order의 카트 수량으로 변경 (주문하려는 개수)
//                    cartDTO.setAmount(cart.getAmount());
//                }
//            });
//        });
        // 2) order의 카트 전체를 DB의 카트로 교체
//        order.setCarts(existsCarts);
//        log.info("existsCarts: " + existsCarts);
        log.info("completed order: " + order);

        ////// 1) Portone에서 결제된 금액과 existsCars에 있는 총 금액을 비교
        // 1) DB에서 조회된 카트 정보로 주문하려고 하는 상품들의 총 금액을 계산
        Integer totalPrice = calculate_total_price(order.getCarts());
        // 주문하는 총 금액을 DB와 똑같이 설정
        order.setTotalPrice(totalPrice);
        // 2) 주문하려고 하는 상품의 총 금액, 상품 번호, 수량을 비교
        //// => 총 금액만 비교할게요 (portone에서 주문한 내역에는 이미 totalPrice가 설정되어 있어요)
        if (totalPrice.equals(payment.getTotalPrice())) {
            log.info("check_order_total_price - 총 금액이 같음(success)");
            return true;
        }
        log.info("check_order_total_price - 총 금액이 다름(failed): " + "(DB:" + totalPrice + ")" + "(portone:" + payment.getTotalPrice() + ")");
        ////만약 총 금액이 다르면
        // 방법 1) 위 데이터가 안맞으면 주문 ALL 취소시킴 (Portone에도 취소 시켜야 함)
        // 방법 2) 위 데이터가 안맞으면 기존 장바구니에 있는 상품으로 변경해서 주문 완료시키기
        return false;
    }

    // 사용자가 요청한 주문을 추가
    public void add_order(OrderDTO order) throws Exception {
        // 제대로 결제가 되었는지 확인

        // 1) DB에 주문 이력을 추가한다
        log.info("== add_order - DB 주문 이력 추가 중.. ==");
        orderMapper.insertOrder(order);

        // 2) DB에 주문하는 상품의 내역을 추가한다
        // order 객체를 json 형태로 변환 (직렬화)
        String orderJsonString = objectMapper.writeValueAsString(order);
        orderMapper.insertOrderProductsAndOptions(orderJsonString);

        // 3) DB에 있는 장바구니 상품들을 제거하기 (정확하게 하기 위해 방법 1 사용)
        // 방법 1) 여기서 한다 (정확함)
        // 방법 2) 주문 완료 후에 따로 한다 (속도만 빠름)( response 받는 js에서 fetch 따로 보내)
        // 방법 3) 여기서 하는데, 병렬처리로 한다 (별로)
        log.info("== add_order - DB 장바구니 상품 제거 중.. ==");
        if(Objects.nonNull(order.getCarts().getFirst().getNo())){
            remove_carts(order.getCarts(), order.getUser());
        }
    }

    // 카트에 있는 총 금액을 구하기
//    Integer calc_total_price(List<CartDTO> carts) {
//        int totalPrice = 0;
//        for(CartDTO cartDTO : carts){
//            int cartAmount = cartDTO.getAmount();
//            ProductDTO product = cartDTO.getProduct();
//            int productPrice = product.getPrice();
//            int optionTotalPrice = product.getOptions().parallelStream()
//                    .mapToInt(option -> option.getPrice() * option.getAmount())
//                    .sum();
//            totalPrice += (productPrice + optionTotalPrice) * cartAmount;
//        }
//        return totalPrice;
//    }
    // 카트에 있는 총 금액을 구하기
    public Integer calculate_total_price(List<CartDTO> carts){
        return carts.parallelStream().mapToInt(cart -> {
            ProductDTO product = cart.getProduct();
            Integer productPrice = product.getPrice();
            Integer amount = cart.getAmount();
            Integer totalOptionPrice = product.getOptions().parallelStream().mapToInt(option ->
                    option.getPrice() * option.getAmount()
            ).sum();
            return (productPrice + totalOptionPrice) * amount;
        }).sum();
    }
}
