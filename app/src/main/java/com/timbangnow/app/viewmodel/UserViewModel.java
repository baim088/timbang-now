package com.timbangnow.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.timbangnow.app.model.AirMinum;
import com.timbangnow.app.model.Nutrisi;
import com.timbangnow.app.model.Target;
import com.timbangnow.app.model.Timbangan;
import com.timbangnow.app.model.User;
import com.timbangnow.app.repository.AuthRepository;
import com.timbangnow.app.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepo = new UserRepository();

    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Timbangan> latestTimbangan = new MutableLiveData<>();
    private final MutableLiveData<List<Timbangan>> timbanganList = new MutableLiveData<>();
    private final MutableLiveData<List<Nutrisi>> nutrisiHariIni = new MutableLiveData<>();
    private final MutableLiveData<AirMinum> airMinumHariIni = new MutableLiveData<>();
    private final MutableLiveData<Target> target = new MutableLiveData<>();
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();

    public LiveData<User> getUserProfile() { return userProfile; }
    public LiveData<Timbangan> getLatestTimbangan() { return latestTimbangan; }
    public LiveData<List<Timbangan>> getTimbanganList() { return timbanganList; }
    public LiveData<List<Nutrisi>> getNutrisiHariIni() { return nutrisiHariIni; }
    public LiveData<AirMinum> getAirMinumHariIni() { return airMinumHariIni; }
    public LiveData<Target> getTarget() { return target; }
    public LiveData<String> getOperationResult() { return operationResult; }

    public void loadUserProfile() {
        userRepo.getUserProfile(new UserRepository.DataCallback<User>() {
            @Override
            public void onSuccess(User data) { userProfile.postValue(data); }
            @Override
            public void onError(String error) { operationResult.postValue(error); }
        });
    }

    public void loadLatestTimbangan() {
        userRepo.getLatestTimbangan(new UserRepository.DataCallback<Timbangan>() {
            @Override
            public void onSuccess(Timbangan data) { latestTimbangan.postValue(data); }
            @Override
            public void onError(String error) { operationResult.postValue(error); }
        });
    }

    public void loadTimbanganList() {
        userRepo.getTimbanganList(new UserRepository.DataCallback<List<Timbangan>>() {
            @Override
            public void onSuccess(List<Timbangan> data) { timbanganList.postValue(data); }
            @Override
            public void onError(String error) { operationResult.postValue(error); }
        });
    }

    public void loadNutrisiHariIni() {
        userRepo.getNutrisiHariIni(new UserRepository.DataCallback<List<Nutrisi>>() {
            @Override
            public void onSuccess(List<Nutrisi> data) { nutrisiHariIni.postValue(data); }
            @Override
            public void onError(String error) { operationResult.postValue(error); }
        });
    }

    public void loadAirMinumHariIni() {
        userRepo.getAirMinumHariIni(new UserRepository.DataCallback<AirMinum>() {
            @Override
            public void onSuccess(AirMinum data) { airMinumHariIni.postValue(data); }
            @Override
            public void onError(String error) { operationResult.postValue(error); }
        });
    }

    public void loadTarget() {
        userRepo.getTarget(new UserRepository.DataCallback<Target>() {
            @Override
            public void onSuccess(Target data) { target.postValue(data); }
            @Override
            public void onError(String error) { operationResult.postValue(error); }
        });
    }

    public void tambahAirMinum(int ml) {
        userRepo.tambahAirMinum(ml, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                loadAirMinumHariIni();
            }
            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }

    public void saveNutrisi(Nutrisi nutrisi) {
        userRepo.saveNutrisi(nutrisi, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                loadNutrisiHariIni();
            }
            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }

    public void saveTarget(Target targetData) {
        userRepo.saveTarget(targetData, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                loadTarget();
            }
            @Override
            public void onError(String error) {
                operationResult.postValue(error);
            }
        });
    }
}
