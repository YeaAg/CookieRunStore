package com.koreait.store.configuration;

import com.koreait.store.converter.MultipartConverter;
import com.koreait.store.interceptor.CategoryInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@ComponentScan(basePackages = "com.koreait.store.aspect")
@EnableAspectJAutoProxy
public class MainConfiguration implements WebMvcConfigurer {
    @Autowired private CategoryInterceptor categoryInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor())
                        .addPathPatterns("/**")
                        .excludePathPatterns("/resources/**");
        registry.addInterceptor(categoryInterceptor)
                .addPathPatterns("/", "/product", "/product/**", "/user/**")
                .excludePathPatterns("/resources/**", "/product/*/review");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new MultipartConverter());
    }

    /**********************************************************/

    // 세션을 사용할 때 사용하는 LocaleChangeInterceptor
    // 위에 있는 interceptor 에 add 해줘야 한다.
//    @Bean
//    public LocaleChangeInterceptor localeChangeInterceptor() {
//        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
//        localeChangeInterceptor.setParamName("lang"); // ?lang=ko 으로 요청해야 함
//        return localeChangeInterceptor;
//    }

    @Bean
    public LocaleResolver localeResolver() {
        // 세션을 사용하는 로케일 리졸버
//        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
//        sessionLocaleResolver.setDefaultLocale(Locale.KOREAN);

        // 쿠키를 사용하는 로케일 리졸버
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        // 쿠키명을 따로 지정하지 않으면 기본값이 적용된다
        // 기본값은 어플리케이션이 스프링을 사용한다는 것을 유추할 수 있는 이름인
        // 'org.spring.....i18n.CookieLocaleResolver.LOCALE'이 사용되기 때문에 변경하는 것이 좋다
        resolver.setCookieName("locale");
        resolver.setDefaultLocale(Locale.KOREAN); // 기본은 한국어
        return resolver;
    }

}

//    Locale 로케일 로켈
//
//    === org.spring...LocaleResolver 인터페이스 ===
//    기본: AcceptHeaderLocaleResolver를 사용
//    순위1) LocaleResolver에 지정한 기본 Locale
//    순위2) JVM에 지정한 Locale
//    순위3) OS에 지정한 Locale
//
//    AcceptHeaderLocaleResolver
//    - Http 요청의 Accept-Language 헤더에 설정된 Locale정보를 활용
//    변경시 => 브라우저의 언어 설정을 변경하면 된다
//
//    SessionLocaleResolver
//    - HTTP 세션에 저장된 Locale 정보를 활용
//    다른 Locale로 변경시 => LocaleChangeInterceptor에서 처리한다
//
//    CookieLocaleResolver
//    -쿠키에 저장된 Locale 정보를 활용
//    다른 Locale로 변경시 => LocaleChangeInterceptor에서 처리한다
//
//    FixedLocaleResolver
//    -JVM, OS의 Locale정보나, 어플리케이션에서 설정한 Locale정보를 활용
//    Locale을 변경하지 않고, 고정해서 사용하고 싶을 때 사용함
