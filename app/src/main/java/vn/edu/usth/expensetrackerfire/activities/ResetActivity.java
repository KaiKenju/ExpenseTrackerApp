package vn.edu.usth.expensetrackerfire.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import vn.edu.usth.expensetrackerfire.MainActivity;
import vn.edu.usth.expensetrackerfire.R;

public class ResetActivity extends AppCompatActivity {

    Button btnReset, btnBack;
    EditText edtEmail;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String strEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        btnBack = findViewById(R.id.btnForgotPasswordBack);
        btnReset = findViewById(R.id.btnReset);
        edtEmail = findViewById(R.id.edtForgotPasswordEmail);
        progressBar = findViewById(R.id.forgetPasswordProgressbar);

        mAuth = FirebaseAuth.getInstance();
        //reset
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = edtEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(strEmail)){
                        ResetPassword();
                }
                if (TextUtils.isEmpty(strEmail)) {
                    edtEmail.setError("Email required....");
                    return;
                }
                // Kiểm tra email hợp lệ
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    edtEmail.setError("Enter a valid email address");
                    return;
                }

            }


        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void ResetPassword() {

        mAuth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                Toast.makeText(ResetActivity.this, "Reset Password link has sent to your register email", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}