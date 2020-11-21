package com.ronaldsantos.pocnfcpagseguro;

public interface MainContract {

    interface MainView {
        void onReadNfcSuccessful(String message);
        void onWriteNfcSuccessful();
        void onErrorNfc(String message);
    }

    interface MainPresenter{
        void readNfc();
        void writeNfc(String message);

        void onDestroy();

    }

}
