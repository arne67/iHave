package com.susarne.ihave2.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class RegisterFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer passwordRepeatedError;
    @Nullable
    private Integer fullNameError;
    @Nullable
    private Integer emailError;
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer usernameError,
                      @Nullable Integer passwordError,
                      @Nullable Integer passwordRepeatedError,
                      @Nullable Integer fullNameError,
                      @Nullable Integer emailError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.passwordRepeatedError=passwordRepeatedError;
        this.fullNameError = fullNameError;
        this.emailError = emailError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getPasswordRepeatedError() {
        return passwordRepeatedError;
    }

    @Nullable
    Integer getFullNameError() {
        return fullNameError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}