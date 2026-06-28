package com.timbangnow.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.timbangnow.app.model.Reservasi;
import com.timbangnow.app.repository.AuthRepository;
import com.timbangnow.app.repository.ReservasiRepository;

import java.util.List;

public class ReservasiViewModel extends ViewModel {

    private final ReservasiRepository repo = new ReservasiRepository();

    private final MutableLiveData<List<Reservasi>> reservasiList = new MutableLiveData<>();
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();

    public LiveData<List<Reservasi>> getReservasiList() { return reservasiList; }
    public LiveData<String> getOperationResult() { return operationResult; }

    public void buatReservasi(Reservasi r) {
        repo.buatReservasi(r, new AuthRepository.AuthCallback() {
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

    public void loadByTanggal(long tanggalMidnight) {
        repo.getReservasiByTanggal(tanggalMidnight, new ReservasiRepository.DataCallback<List<Reservasi>>() {
            @Override
            public void onSuccess(List<Reservasi> data) {
                reservasiList.postValue(data);
            }
            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }

    public void loadByUser(String userId) {
        repo.getReservasiUser(userId, new ReservasiRepository.DataCallback<List<Reservasi>>() {
            @Override
            public void onSuccess(List<Reservasi> data) {
                reservasiList.postValue(data);
            }
            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }

    public void updateStatusHadir(String docId, boolean hadir) {
        repo.updateStatusHadir(docId, hadir, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                operationResult.postValue("STATUS_UPDATED");
            }
            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }
}
