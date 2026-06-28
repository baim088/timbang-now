package com.timbangnow.app.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.timbangnow.app.model.User;

public class AuthRepository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface RoleCallback {
        void onResult(String role);
        void onError(String error);
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void login(String email, String password, AuthCallback cb) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> cb.onSuccess("Login berhasil"))
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void register(String email, String password, String nama, AuthCallback cb) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        // ponytail: tinggiBadan defaults to 0.0, will be entered by coach/admin during physical weighing
                        User newUser = new User(uid, nama, 0.0, email, System.currentTimeMillis(), "user");
                        db.collection("artifacts").document("timbangnow-app")
                                .collection("users").document(uid)
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> cb.onSuccess("Registrasi berhasil"))
                                .addOnFailureListener(e -> cb.onError("Gagal menyimpan profil: " + e.getLocalizedMessage()));
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void getUserRole(String uid, RoleCallback cb) {
        db.collection("artifacts").document("timbangnow-app")
                .collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("role");
                        cb.onResult(role != null ? role : "user");
                    } else {
                        cb.onResult("user"); // ponytail: default to user role
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getLocalizedMessage()));
    }

    public void logout() {
        auth.signOut();
    }
}
