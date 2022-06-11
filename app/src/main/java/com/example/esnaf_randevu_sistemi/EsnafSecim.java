package com.example.esnaf_randevu_sistemi;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EsnafSecim extends AppCompatActivity {
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<String> esnaflarId,esnaflarAd,esnaflarAdres,esnaflarCalismaSaatleri;
    private SearchView searchView;
    FirebaseFirestore db;
    GetEsnafAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esnaf_secim);
        context=EsnafSecim.this;
        db = FirebaseFirestore.getInstance();  //Veritabanı için gerekli kod
        recyclerView = findViewById(R.id.esnafListe);
        esnaflarId = new ArrayList<String>();
        esnaflarAd = new ArrayList<String>();
        esnaflarCalismaSaatleri = new ArrayList<String>();
        esnaflarAdres = new ArrayList<String>();


        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //recyclerView ın layout' unu Linear olarak ayarlıyoruz
        getEsnafData(); // Veritabanından Esnafların bilgilerini çeker
        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { //searchView'daki değişimler için Listener kullandık
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {  //searchView' ın text i değiştiğinde içerisine yazılmış değeri adapter a göndererek
                adapter.getFilter().filter(newText);            //sonuçların filtrelenmesini sağlıyoruz
                return false;
            }
        });
    }

    public void getEsnafData() {          //esnaflar collection ' ından esnafların ismi,acilis kapanis saati ve adresi gibi bilgileri alıyoruz
        db.collection("esnaflar")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document :task.getResult()){
                                esnaflarId.add(document.getId());
                                esnaflarAd.add(document.getString("esnafisim"));
                                esnaflarCalismaSaatleri.add(document.getString("esnafacilissaati")+":00"+"-"+document.getString("esnafkapanissaati")+":00");
                                esnaflarAdres.add(document.getString("esnafadres"));

                            }
                        }

                        adapter=new GetEsnafAdapter(esnaflarId,esnaflarAd,esnaflarCalismaSaatleri,esnaflarAdres,context);//veriler geldikten sonra adaptere yolla
                        recyclerView.setAdapter(adapter); //recyclerViewin Adapterini belirliyoruz

                    }
                });

    }

}