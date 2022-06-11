package com.example.esnaf_randevu_sistemi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EsnafRandevuGoruntuleAdapter extends RecyclerView.Adapter<EsnafRandevuGoruntuleAdapter.ViewHolder> {
    ArrayList<String> getTimeSlot;  // saat değerlerimizin olduğu array 9-10 10-11 11-12 şeklinde
    ArrayList<Integer> timeSlotList;
    ArrayList slotControlList;
    static Context mcontext; // activity içerisindeki intent kalıplarına erişmek için kullanıyorum
    private LayoutInflater mInflater;
    public static String uId;
    public static String secilenTarih;
    public static String secilenSlotText=" ";
    public static String secilenTarihDbFormat;
    public int secilenSlotDurum=0;

    public EsnafRandevuGoruntuleAdapter(String girisyapanIdsi,String secilenTarihDbFormati,String secilenTarihi,ArrayList<String> contacts, ArrayList<String> SlotControl, Context context) {
        uId=girisyapanIdsi;
        secilenTarihDbFormat=secilenTarihDbFormati;
        secilenTarih=secilenTarihi;
        getTimeSlot = contacts; //contacts arrayini getTimeSlot arrayine attık
        slotControlList=SlotControl;
        mcontext=context;
        mInflater=LayoutInflater.from(mcontext);
    }

    @NonNull
    @Override
    public EsnafRandevuGoruntuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saatrow, parent, false); //row.xml layout' unu view olarak aldık

        return new EsnafRandevuGoruntuleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EsnafRandevuGoruntuleAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int positionn) {

        holder.secilenSlot=positionn;
        holder.rezervasyonSaat.setText(getTimeSlot.get(positionn));
        holder.rezervasyonDurum.setText("BOS");//başlangıçta durumları boş olarak ayarla
        holder.rezervasyonSaat.setTextColor(Color.parseColor("#FF000000"));  //default renk saat yazısını siyah yap
        holder.rezervasyonDurum.setTextColor(Color.parseColor("#FF4CAF50")); //default renk durum yazısını yeşil yap
        holder.itemView.setClickable(false); //Boş olan saatlere tıklanılmasını engelle
        Log.d("secilenslot", String.valueOf(secilenSlotDurum));
        if (slotControlList.size() == 0) { //liste boşsa hepsini boş yap
            holder.rezervasyonDurum.setText("BOS");
            holder.rezervasyonSaat.setTextColor(Color.parseColor("#FF000000"));  //boş slotların saat rengini siyah yap
            holder.rezervasyonDurum.setTextColor(Color.parseColor("#FF4CAF50")); //boş slotların durum rengini yeşil yap
            holder.itemView.setClickable(false); //Boş olan saatlere tıklanılmasını engelle
        } else {  //liste boş değilse
            for (int i = 0; i < slotControlList.size(); i++) { //listedeki eleman sayısı kadar dön
                if (Integer.parseInt(slotControlList.get(i).toString()) == positionn) { //eğer listedeki elemanın slotu ile position eşitse değerini DOLU olarak değiştir
                    holder.rezervasyonDurum.setText("DOLU");
                    holder.rezervasyonSaat.setTextColor(Color.parseColor("#FFF13C2F")); //dolu slotların rengini kırmızı yap
                    holder.rezervasyonDurum.setTextColor(Color.parseColor("#FFF13C2F")); //dolu slotların rengini kırmızı yap
                    holder.itemView.setClickable(true); //Dolu olan saatlere tıklanılmasını sağla
                }

            }
        }

    }

    @Override
    public int getItemCount() {
        return getTimeSlot.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView rezervasyonSaat, rezervasyonDurum; //row.xml içerisindeki textViewları burada tanımladık
        public View view;
        public int secilenSlot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            view.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    secilenSlotText= (String) rezervasyonSaat.getText();
                    Log.d("saat",secilenSlotText);
                    Log.d("denemeHata",uId+secilenTarihDbFormat+secilenTarih+secilenSlot+secilenSlotText);
                    Log.d("click", String.valueOf(secilenSlot));  //seçilen saat slotunun numarasını yazdır
                    Intent esnafRandevuGoruntule_randevuIptal=new Intent(mcontext.getApplicationContext(),RandevuIptal.class);
                    esnafRandevuGoruntule_randevuIptal.putExtra("uId",uId);//intent ile sonraki sayfa için gerekli bilgileri gönderiyoruz
                    esnafRandevuGoruntule_randevuIptal.putExtra("secilenTarihDbFormat",secilenTarihDbFormat);
                    esnafRandevuGoruntule_randevuIptal.putExtra("secilenTarih",secilenTarih);
                    esnafRandevuGoruntule_randevuIptal.putExtra("secilenSlot",secilenSlot);
                    esnafRandevuGoruntule_randevuIptal.putExtra("secilenSlotText",secilenSlotText);
                    mcontext.startActivity(esnafRandevuGoruntule_randevuIptal);


                }
            });
            rezervasyonSaat = (TextView) itemView.findViewById(R.id.rezervasyonSaat); //row.xml içindeki ilk textView
            rezervasyonDurum = (TextView) itemView.findViewById(R.id.rezervasyonDurum); //row.xml içindeki ikinci textView

        }
    }
}
