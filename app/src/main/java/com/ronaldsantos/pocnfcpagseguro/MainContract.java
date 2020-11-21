package com.ronaldsantos.pocnfcpagseguro;

public interface MainContract {

    interface MainView {
        void onReadNfcSuccessful(UserData userData);
        void onWriteNfcSuccessful();
        void onErrorNfc(String message);
    }

    interface MainPresenter{
        void readNfc();
        void writeNfc(UserData userData);

        void onDestroy();

    }

}
