package com.example.trialproject3.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    private static FirebaseFirestore firebaseFirestore;
    private static FirebaseAuth auth;

    public static FirebaseFirestore getFireStoreInstance() {
        if (firebaseFirestore == null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
        }

        return firebaseFirestore;
    }

    public static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static FirebaseUser getUser() {
        return getAuth().getCurrentUser();
    }

    public static void signOutUser() {
        getAuth().signOut();
    }
}
