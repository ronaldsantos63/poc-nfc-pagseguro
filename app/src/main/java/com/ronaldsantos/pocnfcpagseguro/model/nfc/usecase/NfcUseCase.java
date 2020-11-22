package com.ronaldsantos.pocnfcpagseguro.model.nfc.usecase;

import android.nfc.tech.MifareClassic;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ronaldsantos.pocnfcpagseguro.helpers.Utils;

import java.util.ArrayList;
import java.util.List;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagNFCAuth;
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagSimpleNFCData;
import br.com.uol.pagseguro.plugpagservice.wrapper.exception.PlugPagException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class NfcUseCase {

    private final PlugPag mPlugPag;

    public NfcUseCase(PlugPag plugPag){
        this.mPlugPag = plugPag;
    }

    public Observable<Integer> writeNfc(PlugPagSimpleNFCData cardData){
        return Observable.create(emitter -> {
            try {
                int resultStartNfc = mPlugPag.startNFCCardDirectly();
                if (resultStartNfc != 1) {
                    emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
                    emitter.onComplete();
                    return;
                }


                PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, (byte) cardData.getSlot(), MifareClassic.KEY_DEFAULT);
                int resultAuth = mPlugPag.authNFCCardDirectly(auth);
                if (resultAuth != 1) {
                    emitter.onError(new PlugPagException(String.format("Erro ao autenticar bloco [ %s ]", cardData.getSlot())));
                    emitter.onComplete();
                    return;
                }

                Integer result = mPlugPag.writeToNFCCardDirectly(cardData);

                if (result == 1) {
                    emitter.onNext(result);
                } else {
                    emitter.onError(new PlugPagException(String.format("Ocorreu um erro ao escrever no bloco [ %s ]  do cartão nfc", cardData.getSlot())));
                }

                mPlugPag.stopNFCCardDirectly();
            } catch (Exception e){
                e.printStackTrace();
                emitter.onError(e);
            }

            emitter.onComplete();
        });
    }

    public Observable<PlugPagNFCResult> readNfc(Integer block){
        return Observable.create(emitter -> {
            int resultStartNfc = mPlugPag.startNFCCardDirectly();
            if (resultStartNfc != 1){
                emitter.onError(new PlugPagException("Ocorreu um erro ao iniciar serviço nfc"));
                emitter.onComplete();
                return;
            }


            PlugPagNFCAuth auth = new PlugPagNFCAuth(PlugPagNearFieldCardData.ONLY_M, block.byteValue(), MifareClassic.KEY_DEFAULT);
            int resultAuth = mPlugPag.authNFCCardDirectly(auth);
            if (resultAuth != 1){
                emitter.onError(new PlugPagException(String.format("Erro ao autenticar bloco [ %s ]", block)));
                emitter.onComplete();
                return;
            }


            PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, block, MifareClassic.KEY_DEFAULT);

            PlugPagNFCResult result = mPlugPag.readNFCCardDirectly(cardData);

            if (result.getResult() == 1){
                Log.d(NfcUseCase.class.getSimpleName(), Utils.convertBytes2String(result.getSlots()[result.getStartSlot()].get("data"), false));
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException(String.format("Ocoreu um erro ao ler bloco [ %s ] do cartão nfc", block)));
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

    public Observable<Integer> clearBlocks(List<Integer> blocks){
        return Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (Integer trailerBlock : getSectorTrailerBlocks()){
                if (blocks.contains(trailerBlock)){
                    emitter.onError(new PlugPagException(String.format("O bloco [ %s ] é de permissão de acesso e não pode ser limpo!", trailerBlock)));
                    emitter.onComplete();
                    return;
                }
            }

            for ( Integer block : blocks ){
                emitter.onNext(block);
            }

            if (!emitter.isDisposed()){
                emitter.onComplete();
            }
        })
                .concatMap(block -> { // Using concatMap to ensure that observables are not called at the same time
                    PlugPagSimpleNFCData emptyData = new PlugPagSimpleNFCData(PlugPagNearFieldCardData.ONLY_M, block, MifareClassic.KEY_DEFAULT);
                    emptyData.setValue(new byte[16]);
                    return writeNfc(emptyData);
                });
    }

    public Observable<Integer> writePermissions(@NonNull byte[] keyA, @NonNull byte[] permissions, @Nullable byte[] keyB){
        return Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (Integer i : getSectorTrailerBlocks()){
                emitter.onNext(i);
            }

            if (!emitter.isDisposed()){
                emitter.onComplete();
            }
        })
                .concatMap(sectorTrailerBlock -> {
                    PlugPagSimpleNFCData cardData = new PlugPagSimpleNFCData(
                            PlugPagNearFieldCardData.ONLY_M,
                            sectorTrailerBlock,
                            MifareClassic.KEY_DEFAULT);
                    cardData.setValue(buildDataAccess(keyA, permissions, keyB));
                    return writeNfc(cardData);
                });
    }

    private List<Integer> getSectorTrailerBlocks(){
        final List<Integer> ret = new ArrayList<>();
        for (int i = 8; i < 65; i += 4){
            ret.add(i);
        }
        return ret;
    }

    private byte[] buildDataAccess(@NonNull byte[] keyA, @NonNull byte[] permissions, @Nullable byte[] keyB){
        byte[] data = new byte[16];
        System.arraycopy(keyA, 0, data, 0, 6);
        System.arraycopy(permissions, 0, data, 6, 4);
        if (keyB != null){
            System.arraycopy(keyB, 0, data, 10, 6);
        }
        return data;
    }

}
