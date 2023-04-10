package com.github.sdp.mediato.ui.viewmodel;

import com.github.sdp.mediato.data.UserDatabase;

public class SearchUserViewModel extends UserViewModel {
    public void reloadUser() {
        UserDatabase.getUser(getUserName()).thenAccept(userLiveData::setValue);
    }
}
