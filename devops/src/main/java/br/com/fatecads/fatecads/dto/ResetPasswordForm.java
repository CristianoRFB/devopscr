package br.com.fatecads.fatecads.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordForm {

    private String token;
    private String password;
    private String confirmPassword;
}
