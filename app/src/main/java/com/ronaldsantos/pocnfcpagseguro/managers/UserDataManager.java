package com.ronaldsantos.pocnfcpagseguro.managers;

import com.ronaldsantos.pocnfcpagseguro.model.nfc.usecase.NfcUseCase;
import com.ronaldsantos.pocnfcpagseguro.model.user.UserData;
import com.ronaldsantos.pocnfcpagseguro.model.user.usecase.GetUserUseCase;
import com.ronaldsantos.pocnfcpagseguro.model.user.usecase.NewUserUseCase;

import io.reactivex.Observable;
import io.reactivex.Single;

public class UserDataManager {

    private GetUserUseCase mGetUser;
    private NewUserUseCase mNewUser;

    public UserDataManager(GetUserUseCase getUser, NewUserUseCase newUser) {
        this.mGetUser = getUser;
        this.mNewUser = newUser;
    }

    public Single<UserData> getUserData(){
        return mGetUser.getUser();
    }

    public Observable<Integer> writeUserData(UserData userData){
        return mNewUser.writeUserInNFcCard(userData);
    }
}
