package com.koreait.store.mapper;

import com.koreait.store.dto.ReviewDTO;
import com.koreait.store.dto.SnsInfoDTO;
import com.koreait.store.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserDTO selectUserById(String id);
    UserDTO selectUserByCi(String ci);
    void insertUser(UserDTO user);
    void insertSnsInfo(SnsInfoDTO snsInfo);

    void insertReview(ReviewDTO review);
}
