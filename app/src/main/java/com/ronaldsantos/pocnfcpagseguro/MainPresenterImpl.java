package com.ronaldsantos.pocnfcpagseguro;

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
        mSubscribe = mNfcUseCase.readNfc()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            try {
                                mView.onReadNfcSuccessful(Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                            }catch (Exception e){
                                e.printStackTrace();
                                mView.onErrorNfc("Erro na leitura: " + e.getMessage());
                            }
                        },
                        throwable -> mView.onErrorNfc(throwable.getMessage()));
    }

    @Override
    public void writeNfc(String message){
        mSubscribe = mNfcUseCase.writeNfc(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mView.onWriteNfcSuccessful(),
                        throwable -> mView.onErrorNfc(throwable.getMessage()));
    }

    @Override
    public void onDestroy() {
        if (mSubscribe != null){
            mSubscribe.dispose();
        }
    }

}
