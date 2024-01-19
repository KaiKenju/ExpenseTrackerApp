//package vn.edu.usth.expensetrackerfire.activities;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.Locale;
//
//import vn.edu.usth.expensetrackerfire.MainActivity;
//import vn.edu.usth.expensetrackerfire.R;
//
//public class RegistrationActivity extends AppCompatActivity {
//
//    private EditText mEmail, mPassword, mName;
//    private Button btnReg;
//    private TextView mSignin;
//    private ProgressDialog mDialog;
//    //Firebase
//    private FirebaseAuth mAuth;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registration);
//
//        mAuth = FirebaseAuth.getInstance();
//        mDialog = new ProgressDialog(this);
//        registration();
//        loadLocale();
//
//
//    }
//    private void loadLocale() {
//        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
//        String language = prefs.getString("My_Lang", "");
//        setLocale(language);
//    }
//    private void setLocale(String lang) {
//        Locale locale = new Locale(lang);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
////        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//    }
//
//
//    private void registration(){
//        mEmail = findViewById(R.id.email_reg);
//        mPassword = findViewById(R.id.password_reg);
//        mName = findViewById(R.id.name_reg);
//        btnReg = findViewById(R.id.btn_reg);
//        mSignin = findViewById(R.id.signin_here);
//
//        btnReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = mEmail.getText().toString().trim();
//                String name = mName.getText().toString().trim();
//                String password = mPassword.getText().toString().trim();
//
//                if(TextUtils.isEmpty(email)){
//                    mEmail.setError("Email required....");
//                    return;
//                }
//                if(TextUtils.isEmpty(password)){
//                    mPassword.setError("Password required....");
//                }
//                // Kiểm tra email hợp lệ
//                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    mEmail.setError("Enter a valid email address");
//                    return;
//                }
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
//
//                mDialog.setMessage("Processing...");
//                mDialog.show();
//
//                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if(task.isSuccessful()){
//                            mDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), "Registration Complete",Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
//                            intent.putExtra("NAME_KEY", name);
//                            intent.putExtra("EMAIL_KEY", email);
//                            startActivity(intent);
//
//                            // lưu trữ dữ liệu để cb re-login
//                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("NAME_KEY", name);
//                            editor.putString("EMAIL_KEY", email);
//                            editor.apply();
//                            finish();
//
//
//                            //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                            //finish();
//                        }else{
//                            mDialog.dismiss();
//                            //Toast.makeText(getApplicationContext(), "Registration Failed",Toast.LENGTH_SHORT).show();
//
//                            String errorMessage = task.getException().getMessage();
//                            if (errorMessage.contains("The email address is already in use by another account.")) {
//                                // Email đã được sử dụng bởi tài khoản khác
//                                Toast.makeText(getApplicationContext(), "This email is already registered!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                // Các lỗi khác
//                                Toast.makeText(getApplicationContext(), "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//
//            }
//        });
//
//        mSignin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            }
//        });
//    }
//}
package vn.edu.usth.expensetrackerfire.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Locale;

import vn.edu.usth.expensetrackerfire.MainActivity;
import vn.edu.usth.expensetrackerfire.R;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mName;
    private Button btnReg;
    private TextView mSignin, mForget;
    private ProgressDialog mDialog;
    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        registration();
        loadLocale();
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void registration() {
        mEmail = findViewById(R.id.email_reg);
        mPassword = findViewById(R.id.password_reg);
        mName = findViewById(R.id.name_reg);
        btnReg = findViewById(R.id.btn_reg);
        mSignin = findViewById(R.id.signin_here);
        mForget = findViewById(R.id.forget_password);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String name = mName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email required....");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password required....");
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Enter a valid email address");
                    return;
                }

                final int MIN_PASSWORD_LENGTH = 6;
                if (password.length() < MIN_PASSWORD_LENGTH) {
                    mPassword.setError("Password should be at least " + MIN_PASSWORD_LENGTH + " characters");
                    return;
                }
                String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).*$";
                if (!password.matches(passwordPattern)) {
                    mPassword.setError("Password should contain at least one digit, one letter, and one special character");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result.getSignInMethods().size() == 0) {
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            mDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                            intent.putExtra("NAME_KEY", name);
                                            intent.putExtra("EMAIL_KEY", email);
                                            startActivity(intent);

                                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("NAME_KEY", name);
                                            editor.putString("EMAIL_KEY", email);
                                            editor.apply();
                                            finish();
                                        } else {
                                            mDialog.dismiss();
                                            String errorMessage = task.getException().getMessage();
                                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                Toast.makeText(getApplicationContext(), "This email is already registered!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "This email is already registered!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error checking email registration status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        mForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });
    }
}
