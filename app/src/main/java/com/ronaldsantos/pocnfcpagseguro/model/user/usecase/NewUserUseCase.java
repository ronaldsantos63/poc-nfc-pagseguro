package com.ronaldsantos.pocnfcpagseguro.model.user.usecase;

import android.nfc.tech.MifareClassic;

import androidx.annotation.NonNull;

import com.ronaldsantos.pocnfcpagseguro.helpers.NFCConstants;
import com.ronaldsantos.pocnfcpagseguro.helpers.Utils;
import com.ronaldsantos.pocnfcpagseguro.model.nfc.usecase.NfcUseCase;
import com.ronaldsantos.pocnfcpagseguro.model.user.UserData;

import java.util.Arrays;
import java.util.List;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import io.reactivex.Observable;

public class NewUserUseCase {

    private final NfcUseCase mNfcUseCase;

    public NewUserUseCase(NfcUseCase nfcUseCase) {
        this.mNfcUseCase = nfcUseCase;
    }

    public Observable<Integer> writeUserInNFcCard(UserData userData){

        final List<Observable<Integer>> observableSources = Arrays.asList(
                writeNameInNfcCard(userData),
                writeBirthdayInNfcCard(userData),
                writeAddressInNfcCard(userData),
                writeMotherNameInNfcCard(userData),
                writeFatherNameInNfcCard(userData),
                writeCellPhoneInNFcCard(userData)
        );

        return Observable.concat(observableSources);
    }


    private Observable<Integer> writeNameInNfcCard(UserData userData){
        return mNfcUseCase.writeNfc(buildCardData(NFCConstants.NAME_BLOCK, userData.getName()));
    }

    private Observable<Integer> writeBirthdayInNfcCard(UserData userData){
        return mNfcUseCase.writeNfc(buildCardData(NFCConstants.BIRTHDAY_BLOCK, userData.getBirthday()));
    }

    private Observable<Integer> writeAddressInNfcCard(UserData userData){
        return mNfcUseCase.writeNfc(buildCardData(NFCConstants.ADDRESS_BLOCK, userData.getAddress()));
    }

    private Observable<Integer> writeMotherNameInNfcCard(UserData userData){
        return mNfcUseCase.writeNfc(buildCardData(NFCConstants.MOTHER_BLOCK, userData.getMotherName()));
    }

    private Observable<Integer> writeFatherNameInNfcCard(UserData userData){
        return mNfcUseCase.writeNfc(buildCardData(NFCConstants.FATHER_BLOCK, userData.getFatherName()));
    }

    private Observable<Integer> writeCellPhoneInNFcCard(UserData userData){
        return mNfcUseCase.writeNfc(buildCardData(NFCConstants.CELL_PHONE_BLOCK, userData.getCellPhone()));
    }

    private PlugPagSimpleNFCData buildCardData(@NonNull Integer block, @NonNull String value){
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, block, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(value));
        return cardData;
    }
}
