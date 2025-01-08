package com.koreait.store.controller;

import com.koreait.store.dto.*;
import com.koreait.store.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Log4j2
@Controller
public class MainController {
    @Autowired private ProductService productService;

    @GetMapping("/") // localhost:8080
    public String get_home(){
        return "main/home"; // 홈페이지로 가라
    }

    // 해당 카테고리의 모든 상품 리스트 화면
    @GetMapping("/product")
    public String get_category(
            PageInfoDTO<ProductDTO> pageInfo,
            Model model
    ){
        productService.get_products(pageInfo);
//        List<ProductDTO> products = productService.get_paginated_products(page, size);
        model.addAttribute("pageInfo", pageInfo);
        return "main/category";
    }

    // 상품 하나의 화면
    @GetMapping("/product/{productNo}")
    public String get_product(
            @PathVariable Integer productNo,
            Model model
    ) {
        ProductDTO product = productService.get_product(productNo);
        List<CategoryDTO> categoryMap = productService.get_category_map(product.getCategory().getNo());
        model.addAttribute("product", product);
        model.addAttribute("categoryMap", categoryMap);

        log.warn(product);
        return "main/product";
    }

    /***************************************************/

    @GetMapping("/product/{productNo}/review")
    public String get_product_reviews(
            @PathVariable("productNo") Integer productNo,
            PageInfoDTO<ReviewDTO> pageInfo,
            Model model
    ){
        Map<String, Map<String, Object>> rateMap = productService.get_reviews(pageInfo, productNo);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("rateMap", rateMap);
        return "main/review-template";
    }
}