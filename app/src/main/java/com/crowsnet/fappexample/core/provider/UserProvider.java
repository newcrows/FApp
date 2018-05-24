package com.crowsnet.fappexample.core.provider;

import android.support.annotation.NonNull;

import com.crowsnet.fappexample.core.pojo.User;
import com.crowsnet.fappexample.core.exception.DateProviderException;
import com.crowsnet.fappexample.core.exception.UserProviderException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class UserProvider {

    private static final String REF_USERS = "/users";

    private static final String NOT_SIGNED_IN = "No signed in user found";

    private static UserProvider instance;

    private ReferenceProvider provider;
    private FirebaseAuth auth;

    public synchronized static UserProvider getInstance() {
        if (instance == null)
            instance = new UserProvider();

        return instance;
    }

    private UserProvider() {
        provider = ReferenceProvider.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void checkSignIn(SignInCallback callback) {
        if (auth.getCurrentUser() != null) {
            String userRef = getCurrentUserRef();
            provider.addReferenceListener(userRef, makeUserListener(callback));
        } else {
            callback.onFail(new UserProviderException(NOT_SIGNED_IN));
        }
    }

    public void signIn(String email, String pass, final SignInCallback callback) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userRef = getCurrentUserRef();
                    provider.addReferenceListener(userRef, makeUserListener(callback));
                } else {
                    callback.onFail(new DateProviderException(task.getException().getMessage()));
                }
            }
        });
    }

    public void signOut() {
        auth.signOut();
    }

    private String getCurrentUserRef() {
        return REF_USERS + "/" + auth.getCurrentUser().getUid();
    }

    private ReferenceProvider.ReferenceListener makeUserListener(final SignInCallback callback) {
        return new ReferenceProvider.ReferenceListener() {
            @Override
            public void onAdded(String ref) {

            }

            @Override
            public void onRemoved(String ref) {

            }

            @Override
            public void onValueEvent(String ref, DataSnapshot snapshot) {
                User user = User.from(snapshot);
                callback.onSuccess(user);

                provider.removeReferenceListener(ref, this);
            }

            @Override
            public void onError(String ref, DatabaseError error) {
                callback.onFail(new DateProviderException(error.getMessage()));

                provider.removeReferenceListener(ref, this);
            }
        };
    }

    public interface SignInCallback {

        void onSuccess(User user);

        void onFail(Object error);
    }
}
