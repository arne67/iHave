package com.susarne.ihave2.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;
    private boolean onlyMissingConfirmation=false;


    LoginResult(@Nullable Integer error,boolean onlyMissingConfirmation) {
        this.error = error;
        this.onlyMissingConfirmation = onlyMissingConfirmation;
    }

    LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    public boolean isOnlyMissingConfirmation() {
        return onlyMissingConfirmation;
    }
}