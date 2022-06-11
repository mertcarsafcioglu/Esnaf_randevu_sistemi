package com.example.esnaf_randevu_sistemi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void buton_click(View view) {
        switch (view.getId()) {
            case R.id.randevu_olustur:
                Intent ilkSayfa_esnafSecim = new Intent(getApplicationContext(),EsnafSecim.class);
                startActivity(ilkSayfa_esnafSecim);
                break;

            case R.id.esnaf_kayit:
                Intent ilkSayfa_esnafKayit = new Intent(getApplicationContext(),EsnafKayit.class);
                startActivity(ilkSayfa_esnafKayit);
                break;

            case R.id.esnaf_giris:
                Intent ilkSayfa_esnafGiris = new Intent(getApplicationContext(),EsnafGiris.class);
                startActivity(ilkSayfa_esnafGiris);
                break;
        }

    }
}