package com.timbangnow.app.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.timbangnow.app.model.AirMinum;
import com.timbangnow.app.model.Nutrisi;
import com.timbangnow.app.model.Target;
import com.timbangnow.app.model.Timbangan;
import com.timbangnow.app.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class UserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    private DocumentReference getUserDoc() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
        return db.collection("artifacts").document("timbangnow-app")
                .collection("users").document(uid);
    }

    private long getTodayMidnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private String getTodayDocId() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }

    private String getDocIdFromTimestamp(long timestamp) {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(timestamp));
    }

    public void getUserProfile(DataCallback<User> cb) {
        getUserDoc().get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        cb.onSuccess(doc.toObject(User.class));
                    } else {
                        cb.onError("Data user tidak ditemukan");
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void updateUserProfile(String nama, String alamat, AuthRepository.AuthCallback cb) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nama", nama);
        updates.put("alamat", alamat);

        getUserDoc().update(updates)
                .addOnSuccessListener(aVoid -> cb.onSuccess("Profil berhasil diperbarui"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getTimbanganList(DataCallback<List<Timbangan>> cb) {
        getUserDoc().collection("timbangan")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .addOnSuccessListener(querySnapshot -> cb.onSuccess(querySnapshot.toObjects(Timbangan.class)))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getLatestTimbangan(DataCallback<Timbangan> cb) {
        getUserDoc().collection("timbangan")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Timbangan> list = querySnapshot.toObjects(Timbangan.class);
                    if (!list.isEmpty()) {
                        cb.onSuccess(list.get(0));
                    } else {
                        cb.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getNutrisiHariIni(DataCallback<List<Nutrisi>> cb) {
        getNutrisiByTanggal(getTodayMidnight(), cb);
    }

    public void getNutrisiByTanggal(long tanggalMidnight, DataCallback<List<Nutrisi>> cb) {
        long start = tanggalMidnight;
        long end = start + (24 * 60 * 60 * 1000) - 1;
        getUserDoc().collection("nutrisi")
                .whereGreaterThanOrEqualTo("timestamp", start)
                .whereLessThanOrEqualTo("timestamp", end)
                .get()
                .addOnSuccessListener(querySnapshot -> cb.onSuccess(querySnapshot.toObjects(Nutrisi.class)))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getRiwayatNutrisi(DataCallback<List<Nutrisi>> cb) {
        getUserDoc().collection("nutrisi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(40)
                .get()
                .addOnSuccessListener(querySnapshot -> cb.onSuccess(querySnapshot.toObjects(Nutrisi.class)))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void saveNutrisi(Nutrisi nutrisi, AuthRepository.AuthCallback cb) {
        String datePrefix = getDocIdFromTimestamp(nutrisi.getTimestamp());
        String docId = datePrefix + "_" + nutrisi.getKategoriWaktu();
        nutrisi.setId(docId);
        getUserDoc().collection("nutrisi").document(docId)
                .set(nutrisi)
                .addOnSuccessListener(aVoid -> cb.onSuccess("Nutrisi disimpan"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getAirMinumHariIni(DataCallback<AirMinum> cb) {
        String docId = getTodayDocId();
        getUserDoc().collection("air_minum").document(docId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        cb.onSuccess(doc.toObject(AirMinum.class));
                    } else {
                        cb.onSuccess(new AirMinum(docId, getTodayMidnight(), 0));
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void tambahAirMinum(int ml, AuthRepository.AuthCallback cb) {
        String docId = getTodayDocId();
        getAirMinumHariIni(new DataCallback<AirMinum>() {
            @Override
            public void onSuccess(AirMinum airMinum) {
                int newTotal = airMinum.getTotalAirMl() + ml;
                AirMinum updated = new AirMinum(docId, getTodayMidnight(), newTotal);
                getUserDoc().collection("air_minum").document(docId)
                        .set(updated)
                        .addOnSuccessListener(aVoid -> cb.onSuccess("Air minum ditambahkan"))
                        .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
            }

            @Override
            public void onError(String error) {
                cb.onError(error);
            }
        });
    }

    public void getTarget(DataCallback<Target> cb) {
        getUserDoc().collection("target")
                .orderBy("tanggalMulai", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Target> list = querySnapshot.toObjects(Target.class);
                    if (!list.isEmpty()) {
                        cb.onSuccess(list.get(0));
                    } else {
                        cb.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void saveTarget(Target target, AuthRepository.AuthCallback cb) {
        DocumentReference doc = getUserDoc().collection("target").document();
        target.setId(doc.getId());
        doc.set(target)
                .addOnSuccessListener(aVoid -> cb.onSuccess("Target disimpan"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }
}
