package com.koreait.store.mapper;

import com.koreait.store.dto.FileDTO;
import com.koreait.store.dto.ProductDTO;
import com.koreait.store.dto.ProductOptionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    void insertProduct(ProductDTO product);
    void insertProductImages(ProductDTO product);
    void insertProductOptions(ProductDTO product);

    void insertFile(byte[] fileData);
    FileDTO selectFile(Integer no);
}
