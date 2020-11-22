package com.ronaldsantos.pocnfcpagseguro.model.user.usecase;

import android.util.Pair;

import com.ronaldsantos.pocnfcpagseguro.helpers.NFCConstants;
import com.ronaldsantos.pocnfcpagseguro.model.nfc.usecase.NfcUseCase;
import com.ronaldsantos.pocnfcpagseguro.model.user.UserData;
import com.ronaldsantos.pocnfcpagseguro.helpers.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

public class GetUserUseCase {

    private final NfcUseCase mNFCUseCase;

    public GetUserUseCase(NfcUseCase nfcUseCase) {
        this.mNFCUseCase = nfcUseCase;
    }

    public Single<UserData> getUser(){
//        final UserData userData = new UserData();

        // List Of Observables for concatenate
        final List<Observable<Pair<UserFieldEnum, String>>> observableSources = Arrays.asList(
                readNameFromNfc(),
                readBirthdayFromNfc(),
                readAddressFromNfc(),
                readMotherNameFromNfc(),
                readFatherNameFromNfc(),
                readCellPhoneFromNFCCard()
        );
        return Observable.concat(observableSources).collect(UserData::new, (user, value) -> {
            switch (value.first){
                case NAME: user.setName(value.second); break;
                case BIRTHDAY: user.setBirthday(value.second); break;
                case ADDRESS: user.setAddress(value.second); break;
                case MOTHER_NAME: user.setMotherName(value.second); break;
                case FATHER_NAME: user.setFatherName(value.second); break;
                case CELL_PHONE: user.setCellPhone(value.second); break;
            }
        });
    }


    private Observable<Pair<UserFieldEnum, String>> readNameFromNfc(){
        return mNFCUseCase.readNfc(NFCConstants.NAME_BLOCK).map(
                result -> new Pair<>(UserFieldEnum.NAME, getStringFromResult(result))
        );
    }

    private Observable<Pair<UserFieldEnum, String>> readBirthdayFromNfc(){
        return mNFCUseCase.readNfc(NFCConstants.BIRTHDAY_BLOCK).map(
                result -> new Pair<>(UserFieldEnum.BIRTHDAY, getStringFromResult(result))
        );
    }

    private Observable<Pair<UserFieldEnum, String>> readAddressFromNfc(){
        return mNFCUseCase.readNfc(NFCConstants.ADDRESS_BLOCK).map(
                result -> new Pair<>(UserFieldEnum.ADDRESS, getStringFromResult(result))
        );
    }

    private Observable<Pair<UserFieldEnum, String>> readMotherNameFromNfc(){
        return mNFCUseCase.readNfc(NFCConstants.MOTHER_BLOCK).map(
                result -> new Pair<>(UserFieldEnum.MOTHER_NAME, getStringFromResult(result))
        );
    }

    private Observable<Pair<UserFieldEnum, String>> readFatherNameFromNfc(){
        return mNFCUseCase.readNfc(NFCConstants.FATHER_BLOCK).map(
                result -> new Pair<>(UserFieldEnum.FATHER_NAME, getStringFromResult(result))
        );
    }

    private Observable<Pair<UserFieldEnum, String>> readCellPhoneFromNFCCard(){
        return mNFCUseCase.readNfc(NFCConstants.CELL_PHONE_BLOCK).map(
                result -> new Pair<>(UserFieldEnum.CELL_PHONE, getStringFromResult(result))
        );
    }

    private String getStringFromResult(PlugPagNFCResult result){
        return Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false);
    }

}
