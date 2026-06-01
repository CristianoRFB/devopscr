package br.com.fatecads.fatecads.service;

public enum PasswordResetRequestResult {
    SUCCESS,
    ACCOUNT_NOT_FOUND,
    RATE_LIMITED,
    DELIVERY_FAILED
}
