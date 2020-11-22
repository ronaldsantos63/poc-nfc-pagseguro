package com.ronaldsantos.pocnfcpagseguro.view.main;

import com.ronaldsantos.pocnfcpagseguro.model.user.UserData;

public interface MainContract {

    interface MainView {
        void onReadNfcSuccessful(UserData userData);
        void onWriteNfcSuccessful();
        void onBlockCleanSuccessful();
        void onErrorNfc(String message);
    }

    interface MainPresenter{
        void readNfc();
        void writeNfc(UserData userData);
        void clearBlocks();

        void onDestroy();

    }

}
