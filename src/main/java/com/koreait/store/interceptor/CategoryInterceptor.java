package com.koreait.store.interceptor;

import com.koreait.store.dto.CategoryDTO;
import com.koreait.store.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Log4j2
@Component
public class CategoryInterceptor implements HandlerInterceptor {
    @Autowired private ProductService productService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        List<CategoryDTO> categories = productService.get_categories();
        modelAndView.addObject("categories", categories);
    }
}
