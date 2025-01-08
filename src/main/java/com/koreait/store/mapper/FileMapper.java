package com.koreait.store.mapper;

import com.koreait.store.dto.FileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileMapper {
    @Select("SELECT * FROM product_image WHERE no = #{imageNo}")
    FileDTO getImageFileByNo(Integer imageNo);
}
