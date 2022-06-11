package com.example.esnaf_randevu_sistemi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EsnafGiris extends AppCompatActivity {
    private TextInputEditText emailTextView, passwordTextView;
    private Button Btn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esnaf_giris);
        mAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        Btn = findViewById(R.id.login);

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

    }

    private void loginUserAccount() {
        String email, password;
        email = emailTextView.getText().toString();  // editText den gelen değeri email değişkenine at
        password = passwordTextView.getText().toString(); //editText den gelen değeri password değişkenine at

        // EditText' lerin boş bırakılamamasını sağlar
        if (TextUtils.isEmpty(email)) {
            emailTextView.setError("Bu alan boş bırakılamaz.");
            //Toast.makeText(getApplicationContext(), "Lütfen mailinizi girin!!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordTextView.setError("Bu alan boş bırakılamaz.");
            //Toast.makeText(getApplicationContext(), "Lütfen şifrenizi girin!!", Toast.LENGTH_LONG).show();
            return;
        }

        // Mevcut olan kullanıcıya giriş yap
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Giriş başarılı!!", Toast.LENGTH_LONG).show();
                                    Log.d("uid", mAuth.getUid());
                                    Intent esnafGiris_esnafRandevuGoruntule = new Intent(EsnafGiris.this, EsnafRandevuGoruntule.class);
                                    esnafGiris_esnafRandevuGoruntule.putExtra("uId",mAuth.getUid());
                                    startActivity(esnafGiris_esnafRandevuGoruntule);

                                } else {  // eğer girilen bilgiler yanlışsa hata verir
                                    emailTextView.setError("Hatalı mail yada şifre"); //editText ile uyarı mesajı gönder
                                    passwordTextView.setError("Hatalı mail yada şifre");
                                    //Toast.makeText(getApplicationContext(), "Giriş başarısız!!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
    }


}