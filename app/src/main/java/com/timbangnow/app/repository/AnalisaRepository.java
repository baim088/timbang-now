package com.timbangnow.app.repository;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.timbangnow.app.model.AnalisaKebugaran;

import java.util.List;

public class AnalisaRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    public void simpanAnalisa(AnalisaKebugaran data, AuthRepository.AuthCallback cb) {
        DocumentReference docRef = db.collection("artifacts").document("timbangnow-app")
                .collection("public").document("data")
                .collection("analisa_kebugaran").document();
        
        data.setId(docRef.getId());
        docRef.set(data)
                .addOnSuccessListener(aVoid -> cb.onSuccess("Data analisa disimpan"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getRiwayatAnalisaByUser(String userId, DataCallback<List<AnalisaKebugaran>> cb) {
        db.collection("artifacts").document("timbangnow-app")
                .collection("public").document("data")
                .collection("analisa_kebugaran")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .addOnSuccessListener(querySnapshot -> cb.onSuccess(querySnapshot.toObjects(AnalisaKebugaran.class)))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getAllRiwayatAnalisa(DataCallback<List<AnalisaKebugaran>> cb) {
        db.collection("artifacts").document("timbangnow-app")
                .collection("public").document("data")
                .collection("analisa_kebugaran")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(querySnapshot -> cb.onSuccess(querySnapshot.toObjects(AnalisaKebugaran.class)))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getLatestAnalisaByUser(String userId, DataCallback<AnalisaKebugaran> cb) {
        db.collection("artifacts").document("timbangnow-app")
                .collection("public").document("data")
                .collection("analisa_kebugaran")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<AnalisaKebugaran> list = querySnapshot.toObjects(AnalisaKebugaran.class);
                    if (!list.isEmpty()) {
                        cb.onSuccess(list.get(0));
                    } else {
                        cb.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }
}
