package com.vani.week4.backend.global;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  인증된 유저를 가져오기 위한 어노테이션
 * @author vani
 * @since 10/14/25
 */
@Target(ElementType.PARAMETER)          // 파라메터에 붙일 수 있는 @
@Retention(RetentionPolicy.RUNTIME)     // 런타임에 스프링이 인식
public @interface CurrentUser {
}
