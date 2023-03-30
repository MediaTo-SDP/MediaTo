package com.github.sdp.mediato.ui.viewmodel;

import com.github.sdp.mediato.data.Database;

public class SearchUserViewModel extends UserViewModel {
    public void reloadUser() {
        Database.getUser(getUserName()).thenAccept(userLiveData::setValue);
    }
}
