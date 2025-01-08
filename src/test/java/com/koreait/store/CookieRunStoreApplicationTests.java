package com.koreait.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreait.store.dto.ProductDTO;
import com.koreait.store.dto.ProductOptionDTO;
import com.koreait.store.mapper.OrderMapper;
import com.koreait.store.service.ChatbotService;
import com.koreait.store.service.EmailService;
import com.koreait.store.service.PortOneService;
import com.koreait.store.service.ValueTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.CookieStore;
import java.net.URI;
import java.util.List;
import java.util.Map;

@SpringBootTest
class CookieRunStoreApplicationTests {
    @Autowired private JavaMailSender mailSender;
    @Autowired private EmailService emailService;
    @Autowired private PortOneService portOneService;
    @Autowired private OrderMapper orderMapper;
    @Autowired private ChatbotService chatbotService;

    @Autowired private ValueTest valueTest;
    @Test
    public void test(){
        valueTest.test();
    }

    @Test
    public void chatbotTest(){
        chatbotService.send_question_and_receive_answer("하이요!");
    }

    @Test
    public void test_call_procedure(){
        String jsonString = "{\"id\":\"2\",\"user\":{\"id\":\"korea\",\"ci\":\"TEST_CI\",\"password\":\"$2a$10$eQvm/Yq2QAYcHlWsuhxWvOGZ7m/cv9ngz21.bEmk6cYFFuu58iPcm\",\"tel\":\"010-1234-1234\",\"email\":\"yegkwkstella@naver.com\",\"nickname\":\"\",\"snsInfo\":[],\"name\":\"korea\",\"attributes\":{},\"username\":\"korea\",\"authorities\":[{\"authority\":\"TEMP\"}],\"enabled\":true,\"accountNonExpired\":true,\"credentialsNonExpired\":true,\"accountNonLocked\":true},\"impUid\":\"1\",\"name\":null,\"totalPrice\":null,\"carts\":[{\"no\":22,\"user\":null,\"product\":{\"no\":8,\"name\":\"クッキーランマスコットぬいぐるみキーホルダー：第2弾\",\"price\":1465,\"detail\":null,\"uploadedAt\":null,\"category\":null,\"options\":[{\"no\":2,\"name\":\"ハッカ飴味クッキー [在庫切れ]\",\"price\":5000,\"amount\":1},{\"no\":3,\"name\":\"海賊味クッキー [在庫切れ]\",\"price\":1800,\"amount\":1}],\"images\":[{\"no\":39,\"originalFilename\":null,\"data\":null},{\"no\":40,\"originalFilename\":null,\"data\":null},{\"no\":41,\"originalFilename\":null,\"data\":null},{\"no\":42,\"originalFilename\":null,\"data\":null},{\"no\":43,\"originalFilename\":null,\"data\":null},{\"no\":44,\"originalFilename\":null,\"data\":null}]},\"amount\":1},{\"no\":21,\"user\":null,\"product\":{\"no\":8,\"name\":\"クッキーランマスコットぬいぐるみキーホルダー：第2弾\",\"price\":1465,\"detail\":null,\"uploadedAt\":null,\"category\":null,\"options\":[{\"no\":3,\"name\":\"海賊味クッキー [在庫切れ]\",\"price\":1800,\"amount\":1}],\"images\":[{\"no\":39,\"originalFilename\":null,\"data\":null},{\"no\":40,\"originalFilename\":null,\"data\":null},{\"no\":41,\"originalFilename\":null,\"data\":null},{\"no\":42,\"originalFilename\":null,\"data\":null},{\"no\":43,\"originalFilename\":null,\"data\":null},{\"no\":44,\"originalFilename\":null,\"data\":null}]},\"amount\":2},{\"no\":20,\"user\":null,\"product\":{\"no\":8,\"name\":\"クッキーランマスコットぬいぐるみキーホルダー：第2弾\",\"price\":1465,\"detail\":null,\"uploadedAt\":null,\"category\":null,\"options\":[],\"images\":[{\"no\":39,\"originalFilename\":null,\"data\":null},{\"no\":40,\"originalFilename\":null,\"data\":null},{\"no\":41,\"originalFilename\":null,\"data\":null},{\"no\":42,\"originalFilename\":null,\"data\":null},{\"no\":43,\"originalFilename\":null,\"data\":null},{\"no\":44,\"originalFilename\":null,\"data\":null}]},\"amount\":2}],\"shippingName\":null,\"shippingTel\":null,\"shippingAddr\":null,\"shippingAddrDetail\":null,\"shippingPostCode\":null,\"ordererName\":null,\"ordererTel\":null,\"ordererEmail\":null,\"orderedAt\":null}";
        orderMapper.insertOrderProductsAndOptions(jsonString);
    }

//    @Test
//    public void tel_auth_test() throws Exception{
//        ProductDTO product1 = new ProductDTO();
//        ProductOptionDTO productOption1 = new ProductOptionDTO();
//        productOption1.setNo(10);
//        ProductOptionDTO productOption2 = new ProductOptionDTO();
//        productOption2.setNo(20);
//
//        product1.setNo(1);
//        product1.setOptions(List.of(productOption1, productOption2));
//        System.out.println(product1.hashCode());
//        /***************************************************************/
//        ProductDTO product2 = new ProductDTO();
//        ProductOptionDTO productOption3 = new ProductOptionDTO();
//        productOption3.setNo(30);
//        product2.setNo(1);
//        product2.setOptions(List.of(productOption3));
//
//        System.out.println(product2.hashCode());
//        /***************************************************************/
//        ProductDTO product3 = new ProductDTO();
//        ProductOptionDTO productOption4 = new ProductOptionDTO();
//        productOption4.setNo(30);
//        product3.setNo(1);
//        product3.setOptions(List.of(productOption4));
//
//        System.out.println(product3.hashCode());
//    }

    @Test
    void send_test() throws MessagingException {
        emailService.send_cert_mail("yegkwkstella@naver.com");
    }

    @Test
    void send_mail() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // helper를 통해 간단히 메일을 구성
        // MimeMessageHelper에 true를 설정시, 파일 첨부 가능! + 인코딩 설정도 가능!
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("yegkwkstella@naver.com"); // 보내는 사람(application.properties에 작성한 유저밖에 안됩니다..!)
        helper.setTo("yegkwkstella@naver.com"); // 받는 사람. 아무나.
        helper.setSubject("웹개발 메일 테스트"); // 메일 제목
        helper.setText("<h1 style=\"color: blue;\">잘 받았니? ㅋ</h1>", true); // 메일 내용, true 설정 시 HTML형식이라고 설정함!
        //생성한 MimeMessage를 SEND
        mailSender.send(message);
    }

    @Test
    void rest_client_test() throws ParseException {
        RestOperations restTemplate = new RestTemplate();

        // header, body로 이루어져 있는 entity / List는 body로 온 데이터 저장
        ResponseEntity<List> response = restTemplate
                .getForEntity("http://localhost:8081/rest/computer/get", List.class);

        // body 데이터만 필요할 때
        List response2 = restTemplate
                .getForObject("http://localhost:8081/rest/computer/get", List.class);

//        List array = (List)(new JSONParser().parse(response.getBody()));
//        for (var data : array){
//            System.out.println(data);
//            Map map = (Map)data;
//            System.out.println(map.get("serial"));
//            System.out.println(map.get("price"));
//            System.out.println(map.get("release"));
//            System.out.println(map.get("name"));
//        }
//        System.out.println(array);

        System.out.println("응답코드: " + response.getStatusCode());
        System.out.println("body: " + response.getBody());
    }

    @Autowired private ObjectMapper objectMapper;
    @Test
    void rest_client_test2() throws JsonProcessingException {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(8081)
                .path("/rest/computer/get/C-1")
                .build();

        RestTemplate restTemplate = new RestTemplate();

        // getForObject가 제일 좋음
//        ComputerDTO response = restTemplate.getForObject(uriComponents.toUriString(), ComputerDTO.class);
//        System.out.println(response);
//
//        String jsonStringResponse = restTemplate.getForObject(uriComponents.toUriString(), String.class);
//        System.out.println(jsonStringResponse); // 단순 JSON 형태의 문자열
//        // 단순 JSON 형태의 문자열을 ComputerDTO형태로 마샬링 부탁~
//        ComputerDTO computerDTO = objectMapper.readValue(jsonStringResponse, ComputerDTO.class);
////        ComputerDTO computerDTO = objectMapper.readValue("{\"serial\": 123}", ComputerDTO.class); // serial을 제외한 나머지는 null로 출력됨
//        System.out.println(computerDTO);

        /////////////////////////// POST 요청 테스트
        String postUri = UriComponentsBuilder
                .fromUri(uriComponents.toUri())
                .replacePath("/rest/computer/post")
                .toUriString();

        String requestData = "{}"; // JSON 형태로 안된다. text/plain으로 인식한다.
        // Body 데이터에 실어서 보내면 자동으로 마샬링/언마샬링 된다! (JSON 형태로)
        Map map = Map.of("price", 2000);
        // Body 데이터에 실어서 보내면 자동으로 마샬링/언마샬링 된다! (JSON 형태로)
        ComputerDTO computerDTO = new ComputerDTO();
        computerDTO.setPrice(3000);

        // postForEntity(요청경로, 요청할때 전달하는 Body 데이터 형태, 응답으로 받는 Body 데이터 형태)
//        ResponseEntity<Void> postResponse = restTemplate.postForEntity(postUri, computerDTO, Void.class);
//        System.out.println("postResponse: " + postResponse);
//
//        // 요청 데이터를 구성하는 요청 Entity 객체
//        RequestEntity<Void> getRequest = RequestEntity.get(uriComponents.toUri()).build();
//        // RequestEntity 하나로 퉁치기
//        ResponseEntity<ComputerDTO> getResponse = restTemplate.exchange(getRequest, ComputerDTO.class);
//        // URI, METHOD를 분리
//        ResponseEntity<ComputerDTO> getResponse2 = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, getRequest, ComputerDTO.class);
        /////////////////////////////////////////
        RequestEntity<Map> postRequest = RequestEntity.post(postUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("serial", "TEST"));
        ResponseEntity<Void> postResponse2 = restTemplate.exchange(postRequest, Void.class);

        // 객체를 body데이터로 보내고 싶다
        RequestEntity<ComputerDTO> postRequest2 = RequestEntity.post(postUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ComputerDTO.builder().name("안녕").build());
        ResponseEntity<Void> postResponse3 = restTemplate.exchange(postRequest2, Void.class);
    }

    public static void main(String[] args) {
        // https://localhost:8080/user/login
        String path = "join";
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https") // 프로토콜
                .host("localhost")
                .port(8080)
                .path("/{a}/{b}")
                .queryParam("email", "a@naver.com")
                .queryParam("password", 123)
                .buildAndExpand(Map.of("a", "admin", "b", "login"));
//                .buildAndExpand("admin", path);
        String uriString = uriComponents.toUriString();
        System.out.println(uriString);
//        URI uri = uriComponents.toUri();
//        System.out.println(uri);

        String myURI = "https://localhost:3306/test";
        UriComponents uriComponents1 = UriComponentsBuilder
                .fromUriString(myURI)
                .replacePath("python")
                .replaceQueryParam("a", 456)
                .replaceQueryParam("message", "{message}")
                .buildAndExpand("안녕하세요");
        String result = uriComponents1.toUriString();
        System.out.println(result);
    }
}
