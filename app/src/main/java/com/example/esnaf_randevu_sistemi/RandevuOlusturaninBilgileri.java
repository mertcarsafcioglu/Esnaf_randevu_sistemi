package com.example.esnaf_randevu_sistemi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RandevuOlusturaninBilgileri extends AppCompatActivity {
public String secileninId,secilenTarihDbFormat,secilenTarih;  //önceki sayfadan gelen değerleri tutacak olan değişkenler
public Integer secilenSlot;
public String secilenSlotText;

    TextInputEditText adsoyad,telefonno;
    AppCompatButton randevutamamlabuton;
    public String adSoyadDeger;
    public String telefonNoDeger;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randevu_olusturanin_bilgileri);

        Bundle extras =getIntent().getExtras(); //Intent ile gönderilen verileri alıyorum
        secileninId=extras.getString("secileninId");
        secilenTarihDbFormat=extras.getString("secilenTarihDbFormat");
        secilenTarih=extras.getString("secilenTarih");
        secilenSlot=extras.getInt("secilenSlot");
        secilenSlotText=extras.getString("secilenSlotText");
        Log.d("secilen",secileninId+"-"+secilenTarih+"-"+secilenTarihDbFormat+"-"+secilenSlot);

        db = FirebaseFirestore.getInstance();                        //Veritabanı için gerekli kod satırı
        adsoyad=findViewById(R.id.adSoyad);                          //EditText'in tanımlanması
        telefonno=findViewById(R.id.telefonNo);                      //EditText'in tanımlanması
        randevutamamlabuton=findViewById(R.id.randevuTamamlaButon);  //butonun tanımlanması


        randevutamamlabuton.setOnClickListener(new View.OnClickListener() { //randevu tamamla butonuna basıldığında yapılacaklar
            @Override
            public void onClick(View view) {
                if(!validateAdSoyad() | !validateTelefonNo()){  // hatalı girilen satırları kontrol etmek için
                    return;
                }

                if(validateAdSoyad()==true && validateTelefonNo()==true) { //eğer girilen verilerde hata yoksa sonraki sayfaya geçilir.
                    addData(); //Hata olmaması durumunda addData içerisine girer ve girilen verileri veritabanına kaydeder
                    Intent randevuOlusturaninBilgileri_randevuOlusturuldu=new Intent(getApplicationContext(),RandevuOlusturuldu.class);
                    randevuOlusturaninBilgileri_randevuOlusturuldu.putExtra("RandevuOlusturanAdSoyad",adSoyadDeger);
                    randevuOlusturaninBilgileri_randevuOlusturuldu.putExtra("RandevuTarih",secilenTarih);
                    randevuOlusturaninBilgileri_randevuOlusturuldu.putExtra("RandevuSaat",secilenSlotText);
                    startActivity(randevuOlusturaninBilgileri_randevuOlusturuldu);

                }
            }
        });
    }
    private void addData() {

        Map<String, Object> kayitYeniMusteri = new HashMap<>();   //Map nesnesi oluşturulur
        kayitYeniMusteri.put("musteriadsoyad", adSoyadDeger);  //Map nesnesine veriler yerleştirilir
        kayitYeniMusteri.put("slot", secilenSlot);
        kayitYeniMusteri.put("musteritelefon", telefonNoDeger);
        kayitYeniMusteri.put("randevutarih", secilenTarih);
        kayitYeniMusteri.put("randevusaat", secilenSlotText);
        Log.d("test", kayitYeniMusteri.toString());

        DocumentReference docRef=db.collection("esnaflar").document(secileninId) // verinin kaydedileceği yerin adresi
                .collection(secilenTarihDbFormat).document(secilenSlot.toString());

        docRef.set(kayitYeniMusteri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(RandevuOlusturaninBilgileri.this, "Veriler Başarıyla Kaydedildi", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {  //veri kaydedilemezse hata mesajı
                Toast.makeText(RandevuOlusturaninBilgileri.this, "Veri Kaydedilemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validateAdSoyad(){
        adSoyadDeger=adsoyad.getText().toString();  //editText in değerini adsoyadDeger değişkenine ata
        String kural="^[a-zA-ZiıİçÇşŞğĞÜüÖö ]*$"; // ad soyad olarak sadece harfleri girebilir
        if(adSoyadDeger.isEmpty()){
            adsoyad.setError("Bu Alan Boş Bırakılamaz");
            return false;
        }else if(adSoyadDeger.length()>30){ //ad soyad verisi 30 karakterden fazlaysa hata mesajı gönder ve geçilmesine izin verme
            adsoyad.setError("Çok Uzun Ad Soyad");
            return false;
        }
        else if(!adSoyadDeger.matches(kural)){ //sadece harf girilmesini sağla
            adsoyad.setError("Sadece Harf giriniz");
            return false;
        }
        else{
            adsoyad.setError(null);       //eğer girilen veri hatasızsa hata mesajını kaldır
            return true;                 //true olarak dönüş değerini ver
        }
    }
    private boolean validateTelefonNo(){
        telefonNoDeger=telefonno.getText().toString(); //editText'in değerini telefonNoDeger değişkenine ata
        String kural="^[0-9]+$"; // telefon numarası için sadece 0-9 arasında sayılar girilebilir
        if(telefonNoDeger.isEmpty()){
            telefonno.setError("Bu Alan Boş Bırakılamaz"); //alan boşsa hata mesajı gönderir ve boş bırakılamaz der
            return false;
        }else if(telefonNoDeger.length()<10){
            telefonno.setError("Telefon Numarası 10 Rakamdan Oluşmalıdır"); //telefon numarası 10 haneli olmalıdır
            return false;
        }else if(!telefonNoDeger.matches(kural)){         //sadece rakam girilmesini sağlar
            telefonno.setError("Sadece 0-9 Arasında Rakam Giriniz");
            return false;
        }
        else{
            telefonno.setError(null);
            return true;
        }
    }
}