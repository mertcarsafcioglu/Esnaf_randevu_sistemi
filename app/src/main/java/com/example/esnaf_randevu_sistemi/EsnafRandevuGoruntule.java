package com.example.esnaf_randevu_sistemi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EsnafRandevuGoruntule extends AppCompatActivity {
    public String uId;
    public int acilisSaati, kapanisSaati, calismaAralikDk;
    private DatePickerDialog tarihSeciciDialog;// kullanıcının randevu tarihi seçtiği kısımdaki tarih ayarlarını yapmamız için gerekli bileşenler
    public Button tarihButon;
    public ImageButton sayfayenile;
    public String secilenTarih;
    public String secilentarihDBFormat;//bu String değişkende veritabanına kaydedeceğimiz formattaki son tarih değeri bulunur örn: "26_04_2022"
    public Context context;

    RecyclerView mRecyclerView;                //arraydeki her veriyi listelememiz için RecyclerView itemını kullandık
    RecyclerView.Adapter esnafRandevuGoruntuleAdapter;    //arraydaki her veriyi tek tek listelemek için adapter kullanmamız gerek

    FirebaseFirestore db;
    ArrayList firebaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esnaf_randevu_goruntule);
        context=EsnafRandevuGoruntule.this;

        Bundle extras =getIntent().getExtras(); //Intent ile gönderilen verileri alıyorum (giriş yapan esnafın id 'sini)
        uId=extras.getString("uId");
        Log.d("secilen",uId);

        acilisSaati = 9;  // default açılış saati
        kapanisSaati = 17;  //default kapanış saati
        calismaAralikDk = 30;  //default çalışma aralığı dk

        initDatePicker();
        db = FirebaseFirestore.getInstance();
        firebaseData = new ArrayList<>();

        tarihButon = findViewById(R.id.Etarihsecici);   //tarih seçeceğimiz butonu tanımladık
        tarihButon.setText(bugununTarihi());          //bugünün tarihini default olarak ayarladık
        sayfayenile=findViewById(R.id.sayfaYenile);

        sayfayenile.setOnClickListener(new View.OnClickListener() {  //sayfayı yenile iconuna tıklandığında listeyi yenile
            @Override
            public void onClick(View view) {
                firebaseData.clear(); //tabloyu sıfırla sorguyu tekrar gönder
                fetchSlotControlData(uId, secilentarihDBFormat);

            }
        });

        Log.d("secilenTarih", secilentarihDBFormat); //seçilen tarihin değerini ekrana yazdır

        getBerberCalismaSaatleri(uId);
    }

    public void getBerberCalismaSaatleri(String docPath) { //veritabanına kayıtlı olan esnafın çalışma saatlerini ve randevu aralığı bilgilerini alır
        DocumentReference docRef = db.collection("esnaflar").document(docPath);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.get("dukkanacilissaat"));

                        acilisSaati = Integer.parseInt(document.get("esnafacilissaati").toString());
                        kapanisSaati = Integer.parseInt(document.get("esnafkapanissaati").toString());
                        calismaAralikDk = Integer.parseInt(document.get("randevuaraligidakika").toString());
                    } else {
                        Log.d("TAG", "Dokuman Getirilemedi");  //hata mesajı
                    }
                } else {
                    Log.d("TAG", "hata : ", task.getException());
                }
                fetchSlotControlData(docPath, secilentarihDBFormat);   //[3,4,5,6] geldiğinde 3 4 5 ve 6. slotlar dolu demektir
            }
        });
    }

    public void fetchSlotControlData(String docPath, String curDay) { //berberin randevu aralıklarının dolu ve boş olmasını ayarlar

        db.collection("esnaflar").document(docPath).collection(curDay)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                firebaseData.add(document.get("slot"));
                            }
                            mRecyclerView = findViewById(R.id.E_recycler_view);  //recyclerView itemını tanımladık
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(EsnafRandevuGoruntule.this, 3); //listeleyeciğimiz array elemanlarının düzenini ayarladık
                            esnafRandevuGoruntuleAdapter = new EsnafRandevuGoruntuleAdapter(docPath,secilentarihDBFormat,secilenTarih,getTimeSet(acilisSaati, kapanisSaati, calismaAralikDk), firebaseData,context); //TimeSlotAdapter isimli adaptor e zaman aralıklarının olduğu array listemizi gönderdik
                            mRecyclerView.setLayoutManager(gridLayoutManager);  //recylerView'in layaut tipini yukarda oluşturduğumuz GridLayoutManager olarak ayarladık
                            mRecyclerView.setAdapter(esnafRandevuGoruntuleAdapter);   //RecyclerView in adapter'inin mAdapter isimli adapter olduğunu söyledik

                            //getTimeSet(8,17,30); //esnafın seçtiği randevu aralığı ayarı Esnaf Acilis Saati:8, Esnaf Kapanis Saati:17, Esnaf Randevu Aralığı:30dk


                        } else {
                        }
                    }
                });
    }


    private String bugununTarihi() {
        Calendar cal = Calendar.getInstance();
        int yil = cal.get(Calendar.YEAR); //güncel yil
        int ay = cal.get(Calendar.MONTH); //güncel ay
        ay = ay + 1;
        int gun = cal.get(Calendar.DAY_OF_MONTH); //güncel gün
        return makeDateString(gun, ay, yil);
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int yil, int ay, int gun) {  // tarih her değiştiğinde içerisine girer
                ay = ay + 1;
                secilenTarih = makeDateString(gun, ay, yil); //makeDateString metoduna datePicker'in yil, ay, gün değerlerini gönder
                tarihButon.setText(secilenTarih); //tarih değerini ekrana yazdırıyoruz

                //Toast.makeText(MainActivity.this, "degisti"+secilentarihDBFormat, Toast.LENGTH_SHORT).show();
                firebaseData.clear(); //tabloyu sıfırla sorguyu tekrar gönder
                fetchSlotControlData(uId, secilentarihDBFormat); //tarih değiştiği için  yeni tarih ile sorguyu yeniden gönderiyorum
            }
        };
        Calendar cal = Calendar.getInstance();
        int yil = cal.get(Calendar.YEAR); // güncel yıl
        int ay = cal.get(Calendar.MONTH); // güncel ay
        int gun = cal.get(Calendar.DAY_OF_MONTH); //güncel gün

        int style = AlertDialog.THEME_HOLO_LIGHT; //style ayarı

        tarihSeciciDialog = new DatePickerDialog(this, style, dateSetListener, yil, ay, gun); //tarih seçici diyalog oluştur
        tarihSeciciDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); //güncel tarihten önceki tarihleri seçimi yasakla
    }

    private String makeDateString(int gun, int ay, int yil) {
        secilentarihDBFormat = gun + "_" + ay + "_" + yil;
        secilenTarih=gun+" "+ayFormati(ay)+" "+yil;
        return gun + " " + ayFormati(ay) + " " + yil;  // return edeceği formatı ayarladık yani şu şekilde -> "7 OCAK 2022"
    }

    public ArrayList<String> getTimeSet(int baslamaSaati, int bitisSaati, int aralikDakika) {
        if (baslamaSaati >= bitisSaati) {
            bitisSaati = bitisSaati + 24;
        }


        ArrayList results = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, baslamaSaati); //başlangıç saati 9
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        int donguSayisi = ((bitisSaati - baslamaSaati) * 60) / aralikDakika;
        for (int i = 0; i < donguSayisi; i++) { //9 dan başlayarak kaç seans olacağı 15 seans
            String frmt = DateFormat.getTimeInstance().format(calendar.getTime());
            String[] saatiBol = frmt.split(":");
            String saat1 = saatiBol[0] + ":" + saatiBol[1];
            calendar.add(Calendar.MINUTE, aralikDakika); //30dk 30dk arttır
            String frmt2 = DateFormat.getTimeInstance().format(calendar.getTime());
            String[] saatiBol2 = frmt2.split(":");
            String saat2 = saatiBol2[0] + ":" + saatiBol2[1];

            String sonuc = saat1 + "-" + saat2;
            results.add(i, sonuc);
        }
        return results;
    }

    private String ayFormati(int ay) {  // SEÇİLEN AY DEĞERİNİN HANGİ AY ADINA DENK GELDİĞİNİ BELİRTTİK
        switch (ay) {
            case 1:
                return "OCAK";
            case 2:
                return "ŞUBAT";
            case 3:
                return "MART";
            case 4:
                return "NİSAN";
            case 5:
                return "MAYIS";
            case 6:
                return "HAZİRAN";
            case 7:
                return "TEMMUZ";
            case 8:
                return "AĞUSTOS";
            case 9:
                return "EYLÜL";
            case 10:
                return "EKİM";
            case 11:
                return "KASIM";
            case 12:
                return "ARALIK";
            default:
                return "OCAK";
        }
    }

    public void acikTarihSecici(View view) {
        tarihSeciciDialog.show();
    }

}