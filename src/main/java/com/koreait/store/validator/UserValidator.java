package com.koreait.store.validator;

import com.koreait.store.dto.UserDTO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;
        String tel = userDTO.getTel();
        if(!tel.matches("^(010|011|017|019|018)-[0-9]{3,4}-[0-9]{4}$")){
            errors.rejectValue("Tel", "Tel number is incorrect");
            throw new IllegalArgumentException("Tel number is incorrect");
        }
    }
}

//간단히 보여준 거 안씀
