package com.timbangnow.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.timbangnow.app.model.AnalisaKebugaran;
import com.timbangnow.app.repository.AuthRepository;
import com.timbangnow.app.repository.AnalisaRepository;

import java.util.List;

public class AnalisaViewModel extends ViewModel {

    private final AnalisaRepository repo = new AnalisaRepository();

    private final MutableLiveData<List<AnalisaKebugaran>> analisaList = new MutableLiveData<>();
    private final MutableLiveData<AnalisaKebugaran> latestAnalisa = new MutableLiveData<>();
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();

    public LiveData<List<AnalisaKebugaran>> getAnalisaList() { return analisaList; }
    public LiveData<AnalisaKebugaran> getLatestAnalisa() { return latestAnalisa; }
    public LiveData<String> getOperationResult() { return operationResult; }

    public void simpanAnalisa(AnalisaKebugaran data) {
        repo.simpanAnalisa(data, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                operationResult.postValue("SUCCESS");
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }

    public void loadByUser(String userId) {
        repo.getRiwayatAnalisaByUser(userId, new AnalisaRepository.DataCallback<List<AnalisaKebugaran>>() {
            @Override
            public void onSuccess(List<AnalisaKebugaran> data) {
                analisaList.postValue(data);
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }

    public void loadAll() {
        repo.getAllRiwayatAnalisa(new AnalisaRepository.DataCallback<List<AnalisaKebugaran>>() {
            @Override
            public void onSuccess(List<AnalisaKebugaran> data) {
                analisaList.postValue(data);
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }

    public void loadLatestByUser(String userId) {
        repo.getLatestAnalisaByUser(userId, new AnalisaRepository.DataCallback<AnalisaKebugaran>() {
            @Override
            public void onSuccess(AnalisaKebugaran data) {
                latestAnalisa.postValue(data);
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }
}
