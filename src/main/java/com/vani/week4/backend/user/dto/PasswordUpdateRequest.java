package com.vani.week4.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author vani
 * @since 10/22/25
 */
public record PasswordUpdateRequest(
        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[ !\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~])[A-Za-z\\d !\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
        )
        String password
) {
}
