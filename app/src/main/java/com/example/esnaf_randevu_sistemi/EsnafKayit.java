package com.example.esnaf_randevu_sistemi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EsnafKayit extends AppCompatActivity {
    private FirebaseFirestore mFirestore;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private HashMap<String, Object> mData;
    private String esnisim, esnKlnA, esnSfr, esnAdres, esnAcS, esnKpnS, esnTel, esnAralikS;
    private ArrayAdapter<CharSequence> adapterGender, adapterGenderk, adapterGenderAralikS;

    TextInputEditText esnafisim, esnafKlnA, esnafSfr, esnafAdres, esnafTel;
    AppCompatButton kayitOl;
    Spinner acilisSaatis, kapanisSaatis, aralikSaatis;
    //tanımlamalar

    String[] saatA, saatK, saatAra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esnaf_kayit);


        esnafisim = findViewById(R.id.esnafisim);
        esnafKlnA = findViewById(R.id.esnafka);
        esnafSfr = findViewById(R.id.esnafsifre);                      //main componentlerini  yönetme
        esnafAdres = findViewById(R.id.esnafadres);
        esnafTel = findViewById(R.id.esnaftel);
        kayitOl = findViewById(R.id.esnafkayit);

        Spinnerkontrol();                                                 //Spinnerları kullanma


        kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                KayitOl();                                            //kayit olma fonksiyonu

            }
        });





    }

    private void Spinnerkontrol() {
        acilisSaatis = findViewById(R.id.spinneracilis);
        kapanisSaatis = findViewById(R.id.spinnerkapanis);              //Spinner componentleri yönetme
        aralikSaatis = findViewById(R.id.spinneraralik);
        adapterGender = ArrayAdapter.createFromResource(this, R.array.AcilisSaat, android.R.layout.simple_spinner_item); // açılış saati için string dizisini Spinnera ekleme 1
        adapterGender.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);                                    // açılış saati için string dizisini spinnera ekleme 2
        acilisSaatis.setAdapter(adapterGender);                                                    // oluşturulan diziyi istediğimiz spinnara tanımlama
        acilisSaatis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                esnAcS = adapterView.getItemAtPosition(i).toString();                               // spinnerdan seçim yapılınca olacaklar
                saatA = esnAcS.split(":");                                        // gelen değerin istediğimiz verisini alma
                esnAcS = saatA[0];


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                esnAcS = "9";                                                         //spinnerda seçim yapılmazsa olacaklar
            }
        });
        adapterGenderk = ArrayAdapter.createFromResource(this, R.array.KapanisSaat, android.R.layout.simple_spinner_item); // kapanis saati için string dizisini Spinnera ekleme 1
        adapterGenderk.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);                                     // kapanis saati için string dizisini Spinnera ekleme 2
        kapanisSaatis.setAdapter(adapterGenderk);                                                                  // oluşturulan diziyi istediğimiz spinnara tanımlama
        kapanisSaatis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                esnKpnS = adapterView.getItemAtPosition(i).toString();                                                 // spinnerdan seçim yapılınca olacaklar
                saatK = esnKpnS.split(":");                                                                  // gelen değerin istediğimiz verisini alma
                esnKpnS = saatK[0];


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                esnKpnS = "17";                                                              //spinnerda seçim yapılmazsa olacaklar
            }
        });
        adapterGenderAralikS = ArrayAdapter.createFromResource(this, R.array.AralikSaat, android.R.layout.simple_spinner_item);    //  saat aralığı için string dizisini Spinnera ekleme 1
        adapterGenderAralikS.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);                                      // saat aralığı için string dizisini Spinnera ekleme 2
        aralikSaatis.setAdapter(adapterGenderAralikS);                                                                                      // oluşturulan diziyi istediğimiz spinnara tanımlama
        aralikSaatis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                esnAralikS = adapterView.getItemAtPosition(i).toString();                    // spinnerdan seçim yapılınca olacaklar;
                saatAra = esnAralikS.split(":");                                         // gelen değerin istediğimiz verisini alma
                esnAralikS = saatAra[0];


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                esnAralikS = "15";                                                      //spinnerda seçim yapılmazsa olacaklar
            }
        });

    }

    public void KayitOl() {

        mFirestore = FirebaseFirestore.getInstance();                                    //firestore için gerekli kısım
        mAuth = FirebaseAuth.getInstance();                                               //Auth için gerekli kısım
        esnisim = esnafisim.getText().toString();
        esnKlnA = esnafKlnA.getText().toString();                                         //txt View değerlerini alma
        esnSfr = esnafSfr.getText().toString();
        esnAdres = esnafAdres.getText().toString();
        esnTel = esnafTel.getText().toString();
        if (!validateEsnafIsim() | !validateEmail() | !validateSifre() | !validateAdres() | !validateTelefonNo()) {
            return;                 // Herhangi bir veride hata var mı diye her veriyi kontrol ediyor
        }
        if (validateEsnafIsim() == true && validateEmail() == true && validateSifre() == true && validateAdres() == true && validateTelefonNo() == true) {                                                                                      //email kontrolü
            //Girilen bütün veriler uygun ise esnafı kaydetme işlemlerini yapıyor
            mAuth.createUserWithEmailAndPassword(esnKlnA, esnSfr)                    //authentication oluşturur ve ona bağlı kayıt ekler
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mUser = mAuth.getCurrentUser();                                   //authentiacationdan verileri getirir
                            mData = new HashMap<>();                                          //verileri göndermek için yapı oluşturur
                            mData.put("randevuaraligidakika", esnAralikS);
                            mData.put("esnafisim", esnisim);
                            mData.put("esnafmail", esnKlnA);
                            mData.put("esnafsifre", esnSfr);                                     //verileri yapıya kayıt eder
                            mData.put("esnafadres", esnAdres);
                            mData.put("esnafacilissaati", esnAcS);
                            mData.put("esnafkapanissaati", esnKpnS);
                            mData.put("esnaftelefon", esnTel);
                            mData.put("esnafid", mUser.getUid());
                            Log.d("tag", "3");
                            mFirestore.collection("esnaflar").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(mData)                          //yapıyı firebase'e ekler

                                    .addOnCompleteListener(EsnafKayit.this, new OnCompleteListener<Void>() {

                                        @Override
                                        //kayıt işlemi tamamlanıp tamamlanmadığını görme

                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) { //kayıt sorunsuz tamamlanırsa mesaj gönder ve intent ile giriş sayfasına geç
                                                Toast.makeText(EsnafKayit.this, "Kayit İşlemi Başarılı Lütfen Giriş Yapınız", Toast.LENGTH_SHORT).show();
                                                Intent esnafKayit_esnafGiris = new Intent(getApplicationContext(), EsnafGiris.class);
                                                startActivity(esnafKayit_esnafGiris);
                                            } else {
                                                Toast.makeText(EsnafKayit.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });

        }
    }



    private boolean validateEmail() {

        if (esnKlnA.isEmpty()) {
            esnafKlnA.setError("Bu Alan Boş Bırakılamaz");
            return false;
        } else if (esnKlnA.length() > 50) { //mail verisi 50 karakterden fazlaysa hata mesajı gönder ve geçilmesine izin verme
            esnafKlnA.setError("Çok Uzun Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(esnKlnA).matches()) { //mailin doğru kurallarda girilmesini kontrol eder
            esnafKlnA.setError("Lütfen Doğru Bir Mail Adresi Giriniz");
            return false;
        } else {
            esnafKlnA.setError(null);       //eğer girilen veri hatasızsa hata mesajını kaldır
            return true;                 //true olarak dönüş değerini ver
        }

    }

    private boolean validateEsnafIsim() {
        String kural = "^[a-zA-ZiıİçÇşŞğĞÜüÖö0-9 ]*$"; // esnaf isim olarak sadece harf ve rakam girebilir
        if (esnisim.isEmpty()) {
            esnafisim.setError("Bu Alan Boş Bırakılamaz");
            return false;
        } else if (esnisim.length() > 30) { //esnaf isim verisi 30 karakterden fazlaysa hata mesajı gönder ve geçilmesine izin verme
            esnafisim.setError("Çok Uzun İsim");
            return false;
        } else if (!esnisim.matches(kural)) { //sadece harf ve rakam girilmesini sağla
            esnafisim.setError("Sadece Harf giriniz");
            return false;
        } else {
            esnafisim.setError(null);       //eğer girilen veri hatasızsa hata mesajını kaldır
            return true;                 //true olarak dönüş değerini ver
        }
    }

    private boolean validateSifre() {
        if (esnSfr.isEmpty()) {
            esnafSfr.setError("Bu Alan Boş Bırakılamaz");
            return false;
        } else if (esnSfr.length() > 30) { //sifre verisi 30 karakterden fazlaysa hata mesajı gönder ve geçilmesine izin verme
            esnafSfr.setError("Çok Uzun Şifre");
            return false;
        } else if (esnSfr.length() < 6) { //sifre verisi 6 karakterden küçükse hata mesajı gönder ve geçilmesine izin verme
            esnafSfr.setError("Şifre 6 karakterden uzun olmalı");
            return false;
        } else {
            esnafSfr.setError(null);       //eğer girilen veri hatasızsa hata mesajını kaldır
            return true;                 //true olarak dönüş değerini ver
        }

    }

    private boolean validateAdres() {
        if (esnAdres.isEmpty()) {
            esnafAdres.setError("Bu Alan Boş Bırakılamaz");
            return false;
        } else if (esnAdres.length() > 200) { //Adres verisi 200 karakterden fazlaysa hata mesajı gönder ve geçilmesine izin verme
            esnafAdres.setError("Çok Uzun Adres");
            return false;
        } else {
            esnafAdres.setError(null);       //eğer girilen veri hatasızsa hata mesajını kaldır
            return true;                 //true olarak dönüş değerini ver
        }

    }

    private boolean validateTelefonNo() {
        String kural = "^[0-9]+$"; // telefon numarası için sadece 0-9 arasında sayılar girilebilir
        if (esnTel.isEmpty()) {
            esnafTel.setError("Bu Alan Boş Bırakılamaz"); //alan boşsa hata mesajı gönderir ve boş bırakılamaz der
            return false;
        } else if (esnTel.length() < 10) {
            esnafTel.setError("Telefon Numarası 10 Rakamdan Oluşmalıdır"); //telefon numarası 10 haneli olmalıdır
            return false;
        } else if (!esnTel.matches(kural)) {         //sadece rakam girilmesini sağlar
            esnafTel.setError("Sadece 0-9 Arasında Rakam Giriniz");
            return false;
        } else {
            esnafTel.setError(null);
            return true;
        }
    }

    
}