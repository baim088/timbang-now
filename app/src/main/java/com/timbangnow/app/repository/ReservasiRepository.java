package com.timbangnow.app.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.timbangnow.app.model.Reservasi;

import java.util.List;

public class ReservasiRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    private CollectionReference getReservasiCollection() {
        return db.collection("artifacts").document("timbangnow-app")
                .collection("public").document("data").collection("reservasi");
    }

    public void buatReservasi(Reservasi r, AuthRepository.AuthCallback cb) {
        DocumentReference doc = getReservasiCollection().document();
        r.setId(doc.getId());
        doc.set(r)
                .addOnSuccessListener(aVoid -> cb.onSuccess("Reservasi berhasil dibuat"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getReservasiByTanggal(long tanggalMidnight, DataCallback<List<Reservasi>> cb) {
        getReservasiCollection()
                .whereEqualTo("tanggalPilihan", tanggalMidnight)
                .get()
                .addOnSuccessListener(querySnapshot -> cb.onSuccess(querySnapshot.toObjects(Reservasi.class)))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getReservasiUser(String userId, DataCallback<List<Reservasi>> cb) {
        getReservasiCollection()
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> cb.onSuccess(querySnapshot.toObjects(Reservasi.class)))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void updateStatusHadir(String docId, boolean hadir, AuthRepository.AuthCallback cb) {
        getReservasiCollection().document(docId)
                .update("statusHadir", hadir)
                .addOnSuccessListener(aVoid -> cb.onSuccess("Status kehadiran diperbarui"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }
}
