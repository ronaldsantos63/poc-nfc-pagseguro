package com.ronaldsantos.pocnfcpagseguro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.MainView {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txv_result)
    MaterialTextView txvResult;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_name)
    EditText edtName;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_birthday)
    EditText edtBirthday;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_address)
    EditText edtAddress;

    private MainContract.MainPresenter presenter;
    private PlugPag mPlugPag;
    private NfcUseCase mNfcUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    private void init(){
        mPlugPag = new PlugPag(this);
        mNfcUseCase = new NfcUseCase(mPlugPag);
        presenter = new MainPresenterImpl(this, mNfcUseCase);
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_write_nfc)
    public void writeInNfc(View view){
        UserData userData = new UserData();
        userData.setName(edtName.getText().toString());
        userData.setBirthday(edtBirthday.getText().toString());
        userData.setAddress(edtAddress.getText().toString());
        presenter.writeNfc(userData);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_read_nfc)
    public void readFromNfc(View view){
        presenter.readNfc();
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReadNfcSuccessful(UserData userData) {
        edtName.setText(userData.getName());
        edtBirthday.setText(userData.getBirthday());
        edtAddress.setText(userData.getAddress());
        txvResult.setText(R.string.on_read_nfc_successful_text);
    }

    @Override
    public void onWriteNfcSuccessful() {
        txvResult.setText(R.string.on_write_nfc_successful_text);
        edtName.setText("");
        edtBirthday.setText("");
        edtAddress.setText("");
    }

    @Override
    public void onErrorNfc(String message) {
        txvResult.setText(message);
    }
}