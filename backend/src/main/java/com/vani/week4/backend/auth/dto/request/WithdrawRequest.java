package com.vani.week4.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * @author vani
 * @since 10/14/25
 */
public record WithdrawRequest (
    @NotBlank(message = "비밀번호는 필수입니다")
    String password
    ) {}
