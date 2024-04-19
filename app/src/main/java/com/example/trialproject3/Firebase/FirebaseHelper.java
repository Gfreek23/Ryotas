package com.example.trialproject3.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import retrofit2.Retrofit;

public class FirebaseHelper {

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_COLLECTION_BOOKINGS = "bookings";
    public static final String KEY_COLLECTION_PRODUCTS = "products";
    public static final String KEY_COLLECTION_CHATS = "chats";
    public static final String KEY_COLLECTION_POSTS = "posts";
    public static final String KEY_COLLECTION_STORES = "stores";
    public static final String KEY_COLLECTION_CARTS = "carts";
    public static final String KEY_COLLECTION_FEEDBACKS = "feedbacks";
    public static final String KEY_COLLECTION_TRIP_FEEDBACKS = "tripFeedbacks";
    public static final String KEY_COLLECTION_FAVORITES = "favorites";
    private static FirebaseFirestore firebaseFirestore;
    public static DatabaseReference databaseReference;
    private static FirebaseAuth auth;
    //	private static FirebaseStorage firebaseStorage;
    public static HashMap<String, String> remoteMsgHeaders = null;
    private static Retrofit retrofit = null;

//	public static Retrofit getClient() {
//		if (retrofit == null) {
//			retrofit = new Retrofit.Builder()
//					.baseUrl("https://fcm.googleapis.com/fcm/")
//					.addConverterFactory(ScalarsConverterFactory.create())
//					.build();
//		}
//		return retrofit;
//	}

    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    "Authorization",
                    "key=AAAAL79ShUw:APA91bH0mZmKzad678UAk3bRtnlDlvWOiLKDM0rmpFbQK7K4nY87hl58NseGCjgP3Cht5Y7ZPoJE82zzdIU3Motx3K5P8BMXO43auFVAw-R6XVmdki0ugDX-ouOo2oiNKmnBwGkiyiaq");
            remoteMsgHeaders.put("Content-Type", "application/json");
        }

        return remoteMsgHeaders;
    }

//	public static FirebaseStorage getFirebaseStorageInstance() {
//		if (firebaseStorage == null) {
//			firebaseStorage = FirebaseStorage.getInstance();
//		}
//		return firebaseStorage;
//	}

    public static DatabaseReference getDatabaseReferenceInstance() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

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

    public static FirebaseUser currentUser() {
        return getAuth().getCurrentUser();
    }

    public static void signOutUser() {
        getAuth().signOut();
    }

    public static String currentUserID() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance()
                .collection(KEY_COLLECTION_USERS)
                .document(currentUserID());
    }
}
