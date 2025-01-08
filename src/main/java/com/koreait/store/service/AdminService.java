package com.koreait.store.service;

import com.koreait.store.dto.FileDTO;
import com.koreait.store.dto.ProductDTO;
import com.koreait.store.dto.ProductOptionDTO;
import com.koreait.store.mapper.AdminMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;
    // 상품과 해당 상품의 옵션들을 DB에 INSERT
    public void add_product(ProductDTO product){
        // 상품을 전달해서 DB에 상품의 정보를 추가하기!
        adminMapper.insertProduct(product);
        log.info("상품을 DB에 삽입함");

        adminMapper.insertProductImages(product);
        log.info("상품을 이미지(들)을 DB에 삽입함");

        // 해당 상품의 모든 옵션을 가져오기
        Set<ProductOptionDTO> productOptions = product.getOptions();
        // 해당 상품에 옵션이 존재한다면
        // => 옵션을 전달하지 않으면 null
        if(Objects.nonNull(productOptions) && !productOptions.isEmpty()){
            log.info("옵션이 존재함!");
            // 해당 상품이 가져야하는 모든 옵션을 DB에 추가하기!
            adminMapper.insertProductOptions(product);
            log.info("상품의 옵션(들)을 DB에 삽입함");
        }
    }

    public void add_file(byte[] data){
        adminMapper.insertFile(data);
    }

    public FileDTO get_file(Integer no){
        return adminMapper.selectFile(no);
    }
}
