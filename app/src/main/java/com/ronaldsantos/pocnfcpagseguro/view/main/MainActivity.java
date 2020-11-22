package com.ronaldsantos.pocnfcpagseguro.view.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textview.MaterialTextView;
import com.ronaldsantos.pocnfcpagseguro.R;
import com.ronaldsantos.pocnfcpagseguro.managers.UserDataManager;
import com.ronaldsantos.pocnfcpagseguro.model.nfc.usecase.NfcUseCase;
import com.ronaldsantos.pocnfcpagseguro.model.user.UserData;
import com.ronaldsantos.pocnfcpagseguro.model.user.usecase.GetUserUseCase;
import com.ronaldsantos.pocnfcpagseguro.model.user.usecase.NewUserUseCase;

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

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_mother_name)
    EditText edtMotherName;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_father_name)
    EditText edtFatherName;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_cell_phone)
    EditText edtCellPhone;

    private MainContract.MainPresenter presenter;
    private PlugPag mPlugPag;
    private NfcUseCase mNfcUseCase;
    private UserDataManager mUserManager;

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

        mUserManager = new UserDataManager(
                new GetUserUseCase(mNfcUseCase),
                new NewUserUseCase(mNfcUseCase)
        );

        presenter = new MainPresenterImpl(this, mNfcUseCase, mUserManager);
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_write_nfc)
    public void writeInNfc(View view){
        UserData userData = new UserData();
        userData.setName(edtName.getText().toString());
        userData.setBirthday(edtBirthday.getText().toString());
        userData.setAddress(edtAddress.getText().toString());
        userData.setMotherName(edtMotherName.getText().toString());
        userData.setFatherName(edtFatherName.getText().toString());
        userData.setCellPhone(edtCellPhone.getText().toString());
        presenter.writeNfc(userData);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_read_nfc)
    public void readFromNfc(View view){
        presenter.readNfc();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_clear_blocks)
    public void clearBlocks(View view){
        presenter.clearBlocks();
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReadNfcSuccessful(UserData userData) {
        edtName.setText(userData.getName());
        edtBirthday.setText(userData.getBirthday());
        edtAddress.setText(userData.getAddress());
        edtMotherName.setText(userData.getMotherName());
        edtFatherName.setText(userData.getFatherName());
        edtCellPhone.setText(userData.getCellPhone());
        txvResult.setText(R.string.on_read_nfc_successful_text);
    }

    @Override
    public void onWriteNfcSuccessful() {
        txvResult.setText(R.string.on_write_nfc_successful_text);
        edtName.setText("");
        edtBirthday.setText("");
        edtAddress.setText("");
        edtMotherName.setText("");
        edtFatherName.setText("");
        edtCellPhone.setText("");
    }

    @Override
    public void onBlockCleanSuccessful() {
        txvResult.setText(R.string.on_block_clean_successful_text);
    }

    @Override
    public void onErrorNfc(String message) {
        txvResult.setText(message);
    }
}