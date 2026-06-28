package com.timbangnow.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.timbangnow.app.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepo = new AuthRepository();

    private final MutableLiveData<String> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> registerResult = new MutableLiveData<>();

    public LiveData<String> getLoginResult() { return loginResult; }
    public LiveData<String> getRegisterResult() { return registerResult; }

    public void login(String email, String password) {
        authRepo.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                loginResult.postValue("SUCCESS");
            }

            @Override
            public void onError(String error) {
                loginResult.postValue(error);
            }
        });
    }

    public void register(String email, String password, String nama, String alamat) {
        authRepo.register(email, password, nama, alamat, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                registerResult.postValue("SUCCESS");
            }

            @Override
            public void onError(String error) {
                registerResult.postValue(error);
            }
        });
    }

    public void logout() {
        authRepo.logout();
    }
}
