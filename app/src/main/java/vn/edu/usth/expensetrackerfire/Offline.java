package vn.edu.usth.expensetrackerfire;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Offline extends Application {
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
