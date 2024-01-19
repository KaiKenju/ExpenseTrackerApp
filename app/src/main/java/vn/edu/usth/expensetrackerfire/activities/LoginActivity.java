package vn.edu.usth.expensetrackerfire.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Locale;

import vn.edu.usth.expensetrackerfire.MainActivity;
import vn.edu.usth.expensetrackerfire.R;

public class LoginActivity extends AppCompatActivity {


    private EditText mEmail, mPassword, mName;
    private Button btnLogin;
    private ImageView login_gg;
    private TextView mForgetPassword, mSignuphere;
    private ProgressDialog mDialog;
    //firebase

    private FirebaseAuth mAuth;
    private GoogleSignInClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadLocale();
        // lấy token từ firebase trên gg
        login_gg = findViewById(R.id.btn_login_gg);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);
        login_gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signOut();
                Intent i  = client.getSignInIntent();
                startActivityForResult(i, 1234);
            }
        });
        //change actionbar title, if you do not change ==? default en
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.app_name));
        } else {
            // Handle null ActionBar here, perhaps log a message or perform an alternative action
            Log.e("MainActivity", "ActionBar is null");
        }

        // change language
        ImageView changeLang = findViewById(R.id.changeMyLang);

        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show alert dialog
                showChangeLanguage();
            }
        });

        if (!isNetworkAvailable()) {
            Toast.makeText(LoginActivity.this, "No network available. Please check your connection.", Toast.LENGTH_SHORT).show();
            return; // Prevent login attempts without a network
        }






        //firebase account gg
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            // lấy dữ liệu khi re-login
            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String personName = sharedPreferences.getString("NAME_KEY", "");
            String personEmail = sharedPreferences.getString("EMAIL_KEY", "");
            String personPhotoUrl = sharedPreferences.getString("PROFILE_KEY", "");

            // Check data user in  SharedPreferences or not
            if (!TextUtils.isEmpty(personName) && !TextUtils.isEmpty(personEmail)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("NAME_KEY", personName);
                intent.putExtra("EMAIL_KEY", personEmail);
                intent.putExtra("PROFILE_KEY", personPhotoUrl);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Can't not retrieve data re-login", Toast.LENGTH_SHORT).show();
            }

//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        mDialog = new ProgressDialog(this);
        loginDetail();
    }
    //check network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showChangeLanguage(){
        final String[] listItems = {"French", "Viet Nam", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        mBuilder.setTitle("Choose Language..");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    //french
                    setLocale("fr");
                    recreate();
                }
                else if (i == 1){
                    //french
                    setLocale("vi");
                    recreate();
                }
                else if (i == 2){
                    //french
                    setLocale("en");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang ){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shareReference
        SharedPreferences.Editor editorr = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editorr.putString("My_Lang", lang);
        editorr.apply();
    }
    //load language saved in shared
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String personName = account.getDisplayName();
                String personEmail = account.getEmail();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
////                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
////                                    startActivity(intent);
//                                    //dành cho firebase
//                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                                    Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
//                                    intent.putExtra("NAME_KEY", personName);
//                                    intent.putExtra("EMAIL_KEY", personEmail);
//                                    startActivity(intent);
//
//                                    // lưu trữ dữ liệu để cb re-login
//                                    SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString("NAME_KEY", personName);
//                                    editor.putString("EMAIL_KEY", personEmail);
//                                    editor.apply();
//
//                                    // Finish the current activity
//                                    finish();
                                    Uri personPhotoUri = account.getPhotoUrl();
                                    if (personPhotoUri != null) {
                                        String personPhotoUrl = personPhotoUri.toString();

                                        // Tạo Intent để chuyển dữ liệu đến HomeActivity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                        intent.putExtra("NAME_KEY", personName);
                                        intent.putExtra("EMAIL_KEY", personEmail);
                                        intent.putExtra("PROFILE_KEY", personPhotoUrl); // Gửi Uri hình ảnh qua Intent

                                        startActivity(intent);

                                        // Lưu trữ dữ liệu để re-login
                                        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("NAME_KEY", personName);
                                        editor.putString("EMAIL_KEY", personEmail);
                                        editor.putString("PROFILE_KEY", personPhotoUrl);
                                        editor.apply();

                                        // Kết thúc Activity hiện tại
                                        finish();
                                    } else {
                                        // Trường hợp không có hình ảnh
                                        Toast.makeText(LoginActivity.this, "No profile picture available", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }

                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null){
//
//        }
    }

    // Add a sign-out method
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        client.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Perform any additional operations after signing out
                    }
                });
    }

    private void loginDetail(){
        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mForgetPassword = findViewById(R.id.forget_password_login);
        mSignuphere = findViewById(R.id.signup_reg);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();



                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required...");
                    return;
                }if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password Required...");
                    return;
                }
                // Kiểm tra email hợp lệ
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Enter a valid email address");
                    return;
                }
//                // Kiểm tra độ dài tối thiểu cho mật khẩu
//                final int MIN_PASSWORD_LENGTH = 6;
//                if (password.length() < MIN_PASSWORD_LENGTH) {
//                    mPassword.setError("Password should be at least " + MIN_PASSWORD_LENGTH + " characters");
//                    return;
//                }
//                String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).*$";
//                if (!password.matches(passwordPattern)) {
//                    mPassword.setError("Password should contain at least one digit, one letter, and one special character");
//                    return;
//                }


                mDialog.setMessage("Processing...");
                mDialog.show();
                if (!isNetworkAvailable()) {
                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "No network available. Please check your connection.", Toast.LENGTH_SHORT).show();
                    return; // Prevent login attempts without a network
                }

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            mDialog.dismiss();

                            String email = mEmail.getText().toString().trim();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            intent.putExtra("EMAIL_KEY", email);
                            startActivity(intent);

                            // lưu trữ dữ liệu để cb re-login
                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("EMAIL_KEY", email);
                            editor.apply();
                            finish();



//                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Login Successful...", Toast.LENGTH_SHORT).show();
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //registration activiy

        mSignuphere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        //Reset password
        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });
    }
}