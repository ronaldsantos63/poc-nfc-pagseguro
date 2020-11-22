package com.ronaldsantos.pocnfcpagseguro.view.main;

import android.nfc.tech.MifareClassic;
import android.util.Log;

import com.ronaldsantos.pocnfcpagseguro.helpers.NFCConstants;
import com.ronaldsantos.pocnfcpagseguro.managers.UserDataManager;
import com.ronaldsantos.pocnfcpagseguro.model.user.UserData;
import com.ronaldsantos.pocnfcpagseguro.helpers.Utils;
import com.ronaldsantos.pocnfcpagseguro.model.nfc.usecase.NfcUseCase;

import java.util.Arrays;
import java.util.List;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenterImpl implements MainContract.MainPresenter {

    private final MainContract.MainView mView;
    private final NfcUseCase mNfcUseCase;
    private final UserDataManager mUserManager;

    private Disposable mSubscribe;

    public MainPresenterImpl(MainContract.MainView view, NfcUseCase nfcUseCase, UserDataManager userManager){
        this.mView = view;
        this.mNfcUseCase = nfcUseCase;
        this.mUserManager = userManager;
    }

    @Override
    public void readNfc() {
        mSubscribe = mUserManager.getUserData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mView.onReadNfcSuccessful(result),
                        throwable -> mView.onErrorNfc(throwable.getMessage())
                );
    }



    @Override
    public void writeNfc(UserData userData){
        mSubscribe = mUserManager.writeUserData(userData)
                .lastElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mView.onWriteNfcSuccessful(),
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        () -> {
                            Log.d(MainPresenterImpl.class.getSimpleName(), "writeUser finished");
                            mView.onWriteNfcSuccessful();
                        }
                );
    }

    @Override
    public void clearBlocks(){
        // using ArrayList to have no block limit
        List<Integer> blocks = Arrays.asList(
                NFCConstants.NAME_BLOCK,
                NFCConstants.BIRTHDAY_BLOCK,
                NFCConstants.ADDRESS_BLOCK,
                NFCConstants.MOTHER_BLOCK,
                NFCConstants.FATHER_BLOCK,
                NFCConstants.CELL_PHONE_BLOCK
        );
        mSubscribe = mNfcUseCase.clearBlocks(blocks)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {},
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        mView::onBlockCleanSuccessful
                );
    }

    @Override
    public void onDestroy() {
        if (mSubscribe != null){
            mSubscribe.dispose();
        }
    }

}
