package com.ronaldsantos.pocnfcpagseguro;

import android.nfc.tech.MifareClassic;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenterImpl implements MainContract.MainPresenter {

    private final NfcUseCase mNfcUseCase;
    private final MainContract.MainView mView;

    private Disposable mSubscribe;

    public MainPresenterImpl(MainContract.MainView view, NfcUseCase nfcUseCase){
        this.mView = view;
        this.mNfcUseCase = nfcUseCase;
    }

    @Override
    public void readNfc() {
        UserData userData = new UserData();
        mSubscribe = mNfcUseCase.readNfc(NFCConstants.NAME_BLOCK)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            try {
                                userData.setName(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            }catch (Exception e){
                                e.printStackTrace();
                                mView.onErrorNfc("Erro na leitura: " + e.getMessage());
                            }
                        },
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        () -> readBirthdayFromNfc(userData)
                );
    }

    private void readBirthdayFromNfc(UserData userData){
        mSubscribe = mNfcUseCase.readNfc(NFCConstants.BIRTHDAY_BLOCK)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            try {
                                userData.setBirthday(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            }catch (Exception e){
                                e.printStackTrace();
                                mView.onErrorNfc("Erro na leitura: " + e.getMessage());
                            }
                        },
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        () -> readAddressFromNfc(userData)
                );
    }

    private void readAddressFromNfc(UserData userData){
        mSubscribe = mNfcUseCase.readNfc(NFCConstants.ADDRESS_BLOCK)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            try {
                                userData.setAddress(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            }catch (Exception e){
                                e.printStackTrace();
                                mView.onErrorNfc("Erro na leitura: " + e.getMessage());
                            }
                        },
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        () -> mView.onReadNfcSuccessful(userData)
                );
    }

    @Override
    public void writeNfc(UserData userData){
        mSubscribe = mNfcUseCase.writeNfc(buildCardData(NFCConstants.NAME_BLOCK, userData.getName()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {},
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        () -> writeBirthdayInNfc(userData)
                );
    }

    private void writeBirthdayInNfc(UserData userData){
        mSubscribe = mNfcUseCase.writeNfc(buildCardData(NFCConstants.BIRTHDAY_BLOCK, userData.getBirthday()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {},
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        () -> writeAddressInNfc(userData)
                );
    }

    private void writeAddressInNfc(UserData userData){
        mSubscribe = mNfcUseCase.writeNfc(buildCardData(NFCConstants.ADDRESS_BLOCK, userData.getAddress()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {},
                        throwable -> mView.onErrorNfc(throwable.getMessage()),
                        mView::onWriteNfcSuccessful
                );
    }


    private PlugPagSimpleNFCData buildCardData(Integer block, String value){
        PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, block, MifareClassic.KEY_DEFAULT);
        cardData.setValue(Utils.convertString2Bytes(value));
        return cardData;
    }

    @Override
    public void onDestroy() {
        if (mSubscribe != null){
            mSubscribe.dispose();
        }
    }

}
