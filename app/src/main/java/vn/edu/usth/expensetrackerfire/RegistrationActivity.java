package vn.edu.usth.expensetrackerfire;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.units.qual.m;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mName;
    private Button btnReg;
    private TextView mSignin;
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


    }


    private void registration(){
        mEmail = findViewById(R.id.email_reg);
        mPassword = findViewById(R.id.password_reg);
        mName = findViewById(R.id.name_reg);
        btnReg = findViewById(R.id.btn_reg);
        mSignin = findViewById(R.id.signin_here);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String name = mName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email required....");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password required....");
                }
                // Kiểm tra email hợp lệ
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Enter a valid email address");
                    return;
                }
                // Kiểm tra độ dài tối thiểu cho mật khẩu
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

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Registration Complete",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                            intent.putExtra("NAME_KEY", name);
                            intent.putExtra("EMAIL_KEY", email);
                            startActivity(intent);

                            // lưu trữ dữ liệu để cb re-login
                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("NAME_KEY", name);
                            editor.putString("EMAIL_KEY", email);
                            editor.apply();
                            finish();


                            //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            //finish();
                        }else{
                            mDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Registration Failed",Toast.LENGTH_SHORT).show();

                            String errorMessage = task.getException().getMessage();
                            if (errorMessage.contains("The email address is already in use by another account.")) {
                                // Email đã được sử dụng bởi tài khoản khác
                                Toast.makeText(getApplicationContext(), "This email is already registered!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Các lỗi khác
                                Toast.makeText(getApplicationContext(), "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}