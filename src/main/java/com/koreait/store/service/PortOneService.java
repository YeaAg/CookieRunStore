package com.koreait.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreait.store.dto.OrderDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;


@Service
public class PortOneService {
    @Autowired private ObjectMapper objectMapper;
    private static final Logger log = LogManager.getLogger(PortOneService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${IMP_KEY}")
    private String IMP_KEY = "";
    @Value("${IMP_SECRET}")
    private String IMP_SECRET = "";
    private String AUTHENTICATION_TOKEN_URI =  "https://api.iamport.kr/users/getToken";
    private String TEL_AUTHENTICATION_URI = "https://api.iamport.kr/certifications/{impUid}";

    private final String PAYMENTS_URI = "https://api.iamport.kr/payments/{impUid}";

    // 포트원 API의 인증 토큰 발급 받기
    private String get_authentication_token() {
        try {
            Map<String, String> bodyData = Map.of(
                    "imp_key", IMP_KEY,
                    "imp_secret", IMP_SECRET
            );
            RequestEntity<String> request = RequestEntity
                    .post(AUTHENTICATION_TOKEN_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(bodyData));
            // PORTONE의 응답을 받아옴
            ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
            if(response.getStatusCode().equals(HttpStatus.OK)){
                System.out.println("토큰 발급 성공!");
                Map responseBody = (Map)response.getBody().get("response");
                String accessToken = (String)responseBody.get("access_token");
                System.out.println("토큰 값: " + accessToken);
                return accessToken;
            }
            System.out.println("토큰 발급 실패!");
        }
        catch (Exception e){
            log.error("오류 발생!: " + e.getMessage());
        }
        return null;
    }

    // 포트원 API에서 해당 impUid로 인증된 유저의 CI를 가져오기
    private String tel_certification(String token, String impUid, String tel){
        RequestEntity<Void> request = RequestEntity
                .get(TEL_AUTHENTICATION_URI, impUid)
                .header("Authorization", "Bearer " + token)
                .build();

        ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
        // 요청이 성공했다면
        if(response.getStatusCode().equals(HttpStatus.OK)){
            Map body = response.getBody();
            Map responseBody = (Map)body.get("response");
            String phone = (String) responseBody.get("phone"); // 휴대폰 번호
            boolean certified = (Boolean) responseBody.get("certified"); // 인증 여부
            String uniqueKey = (String) responseBody.get("unique_key"); // 이 유저의 ci
            log.info("[" + phone + "] 으로 " + certified + " 되었음");
            // 인증되지 않았거나, 인증된 번호와 회원가입 시도하는 휴대폰번호가 다르다면
            if(!certified || !phone.equals(tel.replace("-", ""))){
                return null;
            }
            // 정확히 인증된 유저라면 CI값을 반환
            return uniqueKey;
        }
        return null;
    }

    // 휴대폰 인증 받기 (impUid: 회원가입시 인증했던 impUid, tel: 회원가입시 작성한 휴대폰번호)
    // 인증이 완료되었다면 해당 유저의 CI값을 반환한다
    public String tel_authentication(String impUid, String tel){
        // 1) PORTONE 토큰을 발급받아서 가져온다
        String token = get_authentication_token();
        // 토큰을 발급받지 못했으면 실패
        if(Objects.isNull(token)){
            return null;
        }
        // 회원가입시 인증했던 impUid값으로 휴대폰번호를 검사한 후 ci를 반환받는다
        // 조회된 ci를 반환한다 (NULL 가능성도 있음)
        return tel_certification(token, impUid, tel);
    }

    /****************************************/

    // 결제 내역을 조회한다
    private OrderDTO get_payments(String impUid, String token){
        RequestEntity<Void> request = RequestEntity
                .get(PAYMENTS_URI, impUid)
                .header("Authorization", "Bearer " + token)
                .build();

        ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
        // 요청이 성공했다면
        if(response.getStatusCode().equals(HttpStatus.OK)){
            Map body = response.getBody();
            Map responseBody = (Map)body.get("response");
            String merchantUid = (String) responseBody.get("merchant_uid"); // 주문번호
            Integer amount = (Integer) responseBody.get("amount"); // 결제 금액
            // 인증되지 않았거나, 인증된 번호와 회원가입 시도하는 휴대폰번호가 다르다면
            OrderDTO order = new OrderDTO();
            order.setTotalPrice(amount);
            order.setId(merchantUid);
            log.info("[결제정보]: " + order);
            return order;
        }
        return null;
    }

    // 정확히 결제가 되었는지 확인한다 (본체)
    public OrderDTO payments_authentication(String impUid){
        String token = get_authentication_token();
        // 토큰을 발급받지 못했으면 실패
        if(Objects.isNull(token)){
            return null;
        }
        return get_payments(impUid, token);
    }
}
