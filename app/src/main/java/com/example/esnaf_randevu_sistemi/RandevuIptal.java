package com.example.esnaf_randevu_sistemi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RandevuIptal extends AppCompatActivity {
    TextView secilentarihsaat, musteriad, musteritel;
    String uId, secilenTarihDbFormat, secilenTarih, secilenSlotText;
    Button deleteBtn;
    Integer secilenSlot;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randevu_iptal);
        db = FirebaseFirestore.getInstance();
        Bundle extras = getIntent().getExtras(); //seçilen esnafın idsini bu sayfaya taşıyorum
        uId = extras.getString("uId");
        secilenTarihDbFormat = extras.getString("secilenTarihDbFormat");
        secilenTarih = extras.getString("secilenTarih");
        secilenSlot = (Integer) extras.get("secilenSlot");
        secilenSlotText = extras.getString("secilenSlotText");
        getRandevuOlusturanBilgi(uId, secilenTarihDbFormat, secilenSlot);

        secilentarihsaat = findViewById(R.id.secilenTarihSaat);
        musteriad = findViewById(R.id.musteriAd);
        musteritel = findViewById(R.id.musteriTel);
        deleteBtn = findViewById(R.id.randevuIptal);
        secilentarihsaat.setText(secilenTarih + "   " + secilenSlotText);
        Log.d("secilenslot", String.valueOf(secilenSlot));


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(secilenSlot);
            }
        });


    }
    EsnafRandevuGoruntule esnf=new EsnafRandevuGoruntule();
    private void deleteData(Integer secilenSlotId) {
        db.collection("esnaflar")
                .document(uId).collection(secilenTarihDbFormat).document(String.valueOf(secilenSlotId)).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RandevuIptal.this, " Randevu İptal Edildi", Toast.LENGTH_SHORT).show();

                            //Intent randevuIptal_esnafRandevuGoruntule = new Intent(getApplicationContext(), EsnafRandevuGoruntule.class);
                            //startActivity(randevuIptal_esnafRandevuGoruntule);
                            //esnf.tarihButon.setText(secilenTarih);
                            finish();
                        }

                    }
                });

    }


    public void getRandevuOlusturanBilgi(String docPath, String secilmisTarih, Integer secilmisSlot) { //veritabanına kayıtlı olan esnafın çalışma saatlerini ve randevu aralığı bilgilerini alır
        DocumentReference docRef = db.collection("esnaflar").document(docPath).collection(secilmisTarih).document(secilmisSlot.toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("docdenme", "data: " + document.get("musteriadsoyad"));
                    String musteriAdSoyad = (String) document.get("musteriadsoyad");
                    String musteriTelefon = (String) document.get("musteritelefon");
                    musteriad.setText(musteriAdSoyad);         //üzerine basılan randevunun hangi kişiye ait olduğunun bilgisini göster
                    musteritel.setText("0" + musteriTelefon);
                }
            }
        });
    }


}