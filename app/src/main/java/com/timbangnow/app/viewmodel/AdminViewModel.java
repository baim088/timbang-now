package com.timbangnow.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.timbangnow.app.model.Timbangan;
import com.timbangnow.app.model.User;
import com.timbangnow.app.repository.AuthRepository;

import java.util.List;

public class AdminViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<User>> memberList = new MutableLiveData<>();
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();

    public LiveData<List<User>> getMemberList() { return memberList; }
    public LiveData<String> getOperationResult() { return operationResult; }

    public void loadAllMembers() {
        db.collection("artifacts").document("timbangnow-app")
                .collection("users")
                .whereEqualTo("role", "user")
                .get()
                .addOnSuccessListener(querySnapshot -> memberList.postValue(querySnapshot.toObjects(User.class)))
                .addOnFailureListener(e -> operationResult.postValue(e.getLocalizedMessage()));
    }

    public void inputTimbangan(String userId, double tinggiBadan, Timbangan t, AuthRepository.AuthCallback cb) {
        // Calculate BMI: weight / (height_in_m)^2
        double tinggiM = tinggiBadan / 100.0;
        double bmi = 0.0;
        if (tinggiM > 0) {
            bmi = t.getBeratBadan() / (tinggiM * tinggiM);
            bmi = Math.round(bmi * 10.0) / 10.0; // round to 1 decimal
        }
        t.setBmi(bmi);

        DocumentReference doc = db.collection("artifacts").document("timbangnow-app")
                .collection("users").document(userId)
                .collection("timbangan").document();
        t.setId(doc.getId());

        doc.set(t)
                .addOnSuccessListener(aVoid -> cb.onSuccess("SUCCESS"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }
}
