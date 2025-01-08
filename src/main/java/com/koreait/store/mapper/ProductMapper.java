package com.koreait.store.mapper;

import com.koreait.store.dto.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    Integer selectPaginatedProductsTotalCount(PageInfoDTO<ProductDTO> pageInfo);
    List<ProductDTO> selectProducts(PageInfoDTO<ProductDTO> pageInfo);

    ProductDTO selectProductByNo(Integer productNo);

    @MapKey("no")
    Map<Integer, ProductDTO> selectProductsByCartProducts(List<CartDTO> carts);

    List<CategoryDTO> selectCategoryMapByCategoryNo(Integer categoryNo);
    List<CategoryDTO> selectCategories();

    @MapKey("rate")
    Map<String, Map<String, Object>> selectPaginatedReviewTotalCountByProductNo(Integer productNo);
    List<ReviewDTO> selectPaginatedReviewsByProductNo(PageInfoDTO<ReviewDTO> pageInfo, Integer productNo);
}
