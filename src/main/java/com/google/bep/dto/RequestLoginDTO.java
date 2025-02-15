package com.google.bep.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestLoginDTO {
    @Schema(description = "이메일", example = "googletest@gamil.com")
    private String email;

    @Schema(description = "비밀번호", example = "00000000")
    private String password;
}
