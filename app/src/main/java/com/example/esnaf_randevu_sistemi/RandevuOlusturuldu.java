package com.example.esnaf_randevu_sistemi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RandevuOlusturuldu extends AppCompatActivity {

    TextView randevuOlusturanBilgi,randevuBilgileri;
    AppCompatButton anasayfayaDon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randevu_olusturuldu);

        Bundle extras =getIntent().getExtras(); //Intent ile gönderilen verileri alıyorum
        String randevuOlusturanAdSoyad = extras.getString("RandevuOlusturanAdSoyad");
        String randevuTarih=extras.getString("RandevuTarih");
        String randevuSaat=extras.getString("RandevuSaat");
        Log.d("sonsayfa",randevuOlusturanAdSoyad+"-"+randevuTarih+"-"+randevuSaat);

        randevuOlusturanBilgi=findViewById(R.id.randevu_olusturan_bilgi);
        randevuBilgileri=findViewById(R.id.randevu_bilgileri);
        anasayfayaDon=findViewById(R.id.anasayfaDon);

        randevuOlusturanBilgi.setText(randevuOlusturanAdSoyad);
        randevuBilgileri.setText(randevuTarih+" "+randevuSaat);



        anasayfayaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent randevuOlusturuldu_mainActivity =new Intent(getApplicationContext(),MainActivity.class);
                startActivity(randevuOlusturuldu_mainActivity);
            }
        });
    }
}