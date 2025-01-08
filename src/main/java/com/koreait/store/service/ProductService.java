package com.koreait.store.service;

import com.koreait.store.dto.CategoryDTO;
import com.koreait.store.dto.PageInfoDTO;
import com.koreait.store.dto.ProductDTO;
import com.koreait.store.dto.ReviewDTO;
import com.koreait.store.mapper.ProductMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private static final Logger log = LogManager.getLogger(ProductService.class);
    @Autowired
    private ProductMapper productMapper;

    // 상품의 부모 - 자식- 자식2(현재 상품) 와 같은 카테고리의 계층 구조를 조회한다
    public List<CategoryDTO> get_category_map(Integer categoryNo) {
        return productMapper.selectCategoryMapByCategoryNo(categoryNo);
    }

    // page: 내가 가져오고 싶은 페이지 번호 (ex: 현재 2페이지다)
    // size: 한 페이지 당 가져올 상품 개수 (ex: 4개씩 보여주겠다)

    public List<CategoryDTO> get_categories(){
        return productMapper.selectCategories();
    }

    public void get_products(PageInfoDTO<ProductDTO> pageInfo) {
        if (pageInfo.getPage() < 1){
            return;
        }
        // 카테고리 조건에 맞는 총 상품 개수를 구한다
        Integer totalElementCount = productMapper.selectPaginatedProductsTotalCount(pageInfo);
        // 화면에 표시할 상품이 존재한다면
        if (totalElementCount != 0) {
            var products = productMapper.selectProducts(pageInfo);
            pageInfo.setTotalElementCount(totalElementCount);
            pageInfo.setElements(products); // DB에서 조회된 조건에 맞는 상품들
        }
    }

    public ProductDTO get_product(Integer productNo){
        return productMapper.selectProductByNo(productNo);
    }

    // 이 상품에 해당하는 페이지네이션 된 리뷰 가져오기
    public Map<String, Map<String, Object>> get_reviews(PageInfoDTO<ReviewDTO> pageInfo, Integer productNo){
        // 리뷰는 무조건 3개씩 보이게 고정
        pageInfo.setSize(3);
        if (pageInfo.getPage() < 1){
            return null;
        }
        // 카테고리 조건에 맞는 총 상품 개수를 구한다
        Map<String, Map<String, Object>> result = productMapper.selectPaginatedReviewTotalCountByProductNo(productNo);
        // 화면에 표시할 리뷰가 존재한다면
        if (!result.isEmpty()) {
            Integer totalElementCount = Integer.parseInt(result.get("result").get("count").toString());
            var reviews = productMapper.selectPaginatedReviewsByProductNo(pageInfo, productNo);
            pageInfo.setTotalElementCount(totalElementCount);
            pageInfo.setElements(reviews); // DB에서 조회된 조건에 맞는 상품들
        }
        return result;
    }
}
