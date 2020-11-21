package com.ronaldsantos.pocnfcpagseguro;

import android.nfc.tech.MifareClassic;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagNFCAuth;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import br.com.uol.pagseguro.plugpagservice.wrapper.exception.PlugPagException;
import io.reactivex.Completable;
import io.reactivex.Observable;

public class NfcUseCase {

    private final PlugPag mPlugPag;

    public NfcUseCase(PlugPag plugPag){
        this.mPlugPag = plugPag;
    }

    public Observable<Integer> writeNfc(String message){
        return Observable.create(emitter -> {
            try {
                int resultStartNfc = mPlugPag.startNFCCardDirectly();
                if (resultStartNfc != 1) {
                    emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
                    emitter.onComplete();
                    return;
                }


                PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) 28, MifareClassic.KEY_DEFAULT);
                int resultAuth = mPlugPag.authNFCCardDirectly(auth);
                if (resultAuth != 1) {
                    emitter.onError(new PlugPagException("Erro na autenticação"));
                    emitter.onComplete();
                    return;
                }

                byte[] bytes = Utils.convertString2Bytes(message);

                PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, 28, MifareClassic.KEY_DEFAULT);
                cardData.setValue(bytes);

                Integer result = (Integer) mPlugPag.writeToNFCCardDirectly(cardData);

                if (result == 1) {
                    emitter.onNext(result);
                } else {
                    emitter.onError(new PlugPagException("Ocorreu um erro ao escrever no cartão nfc"));
                }

                mPlugPag.stopNFCCardDirectly();
            } catch (Exception e){
                e.printStackTrace();
                emitter.onError(e);
            }

            emitter.onComplete();
        });
    }

    public Observable<PlugPagNFCResult> readNfc(){
        return Observable.create(emitter -> {
            int resultStartNfc = mPlugPag.startNFCCardDirectly();
            if (resultStartNfc != 1){
                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
                emitter.onComplete();
                return;
            }


            PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) 28, MifareClassic.KEY_DEFAULT);
            int resultAuth = mPlugPag.authNFCCardDirectly(auth);
            if (resultAuth != 1){
                emitter.onError(new PlugPagException("Erro na autenticação"));
                emitter.onComplete();
                return;
            }


            PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, 28, MifareClassic.KEY_DEFAULT);
//            cardData.setStartSlot(28);
//            cardData.setEndSlot(28);

            PlugPagNFCResult result = mPlugPag.readNFCCardDirectly(cardData);

            if (result.getResult() == 1){
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Ocoreu um erro ao ler o cartão nfc"));
            }
            mPlugPag.stopNFCCardDirectly();

            emitter.onComplete();
        });
    }

    public Observable<Object> startNfc(){
        return Observable.create(emitter -> {
            int result = mPlugPag.startNFCCardDirectly();
            if (result == 1){
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar nfc"));
            }

            emitter.onComplete();
        });
    }

    public Observable<Object> stopNfc(){
        return Observable.create(emitter -> {
            int result = mPlugPag.startNFCCardDirectly();
            if (result == 1){
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar nfc"));
            }

            emitter.onComplete();
        });
    }

    public Completable abort(){
        return Completable.create(emitter -> mPlugPag.abortNFC());
    }

}
