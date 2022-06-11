package com.example.esnaf_randevu_sistemi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GetEsnafAdapter extends RecyclerView.Adapter<GetEsnafAdapter.ViewHolder> implements Filterable {
    public ArrayList<String> esnaflarId, esnaflarAd, esnaflarCalismaSaat, esnaflarAdres, esnaflarAll;
    public ArrayList<String> filteredListAd, filteredListCalismaSaat, filteredListAdres;
    public ArrayList<Integer> filtrelenenlerinOrjID;
    public String secileninId; //listede seçilen esnafın id'sini tutacak değişken
    public int i = 0;
    private Context mcontext;
    private LayoutInflater mInflater;

    public GetEsnafAdapter(ArrayList<String> esnaflarIdsi, ArrayList<String> esnaflarAdi, ArrayList<String> esnaflarCalismaSaati, ArrayList<String> esnaflarAdresi, Context context) {
        mcontext=context;
        mInflater=LayoutInflater.from(mcontext);
        esnaflarId = esnaflarIdsi;   //veritabanından gelen id, ad, çalışma saati, adres bilgilerinin tutalacağı arrayler
        esnaflarAd = esnaflarAdi;
        esnaflarCalismaSaat = esnaflarCalismaSaati;
        esnaflarAdres = esnaflarAdresi;

        filteredListAd = esnaflarAdi;
        esnaflarAll = new ArrayList<>(esnaflarAd);
        filtrelenenlerinOrjID = new ArrayList<>();
        filteredListAd = new ArrayList<>();
        filteredListCalismaSaat = new ArrayList<>();
        filteredListAdres = new ArrayList<>();
        filtrelenenlerinOrjID = new ArrayList<>();
    }

    @NonNull
    @Override
    public GetEsnafAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.esnafrow, parent, false); //esnafrow.xml layout' unu view olarak aldık
        //filteredListCalismaSaat=esnaflarCalismaSaat;
        //Log.d("filter","crt:"+filteredListCalismaSaat.toString());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GetEsnafAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.secilenEsnaf = position; //listedeki bir esnafa tıklandığında seçilen esnafın id sini bulmak için position'ı secilenEsnafa atıyoruz
        //Log.d("filtered",filteredListCalismaSaat.toString());
        holder.esnafad.setText(esnaflarAd.get(position));
        holder.esnafcalismasaat.setText(esnaflarCalismaSaat.get(position));
        holder.esnafadres.setText(esnaflarAdres.get(position));

        if (filteredListCalismaSaat.isEmpty()) { //filtrelenmiş item yoksa birşey yapma filtelenmiş item varsa listeye uygula
            //Log.d("filter","filterListBoş");
        } else {                                //değerleri filtrelenmiş değerlerle değiştir.
            holder.esnafcalismasaat.setText(filteredListCalismaSaat.get(position));
            holder.esnafadres.setText(filteredListAdres.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return esnaflarAd.size();  //listeye veritabanında bulunan esnaf sayısı kadar esnafrow.xml layoutunu ekle
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {  //SearchView dan girilen harf veya kelimelere göre ArrayListte bulunan Esnafisim değişkenlerini filtreler
        @Override
        public FilterResults performFiltering(CharSequence charSequence) {

            filtrelenenlerinOrjID.clear();
            filteredListCalismaSaat.clear(); //yazılan harf değiştiğinde arraylist değişeceği için her değiştiğinde listeler sıfırlanır
            filteredListAdres.clear();
            esnaflarAd.clear();
            if (charSequence.toString().isEmpty()) { //eğer searchview a herhangi bir değer girilmemişse filtrelemeden döndür
                filteredListAd.addAll(esnaflarAll);
                filtrelenenlerinOrjID.clear();
            } else {
                for (String esnaf : esnaflarAll) {
                    if (esnaf.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredListAd.add(esnaf);
                        filtrelenenlerinOrjID.add(i);      // değişen elemanların listedeki hangi id li elemanlar olduğunu bulur ve ArrayListe atar
                    }                             // örneğin [aa,bb,cc,dd,ee] diye bir listemize "c" harfi girilmişse degisecekler=[3] olur
                    i++;
                }
                //Log.d("degisenler", filtrelenenlerinOrjID.toString());  //filtrelenenlerinOrjID->değişen elemanların orjinal listedeki yerlerinin bilgisini tutar
                i = 0;
            }

            for (int j = 0; j < (filtrelenenlerinOrjID.size()); j++) { //arraylistin filtelendikten sonraki halininin adres ve çalışma saati bilgilerini alır
                filteredListCalismaSaat.add(esnaflarCalismaSaat.get(filtrelenenlerinOrjID.get(j)));
                filteredListAdres.add(esnaflarAdres.get(filtrelenenlerinOrjID.get(j)));
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredListAd;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            esnaflarAd = filteredListAd;
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView esnafad, esnafadres, esnafcalismasaat;
        public int secilenEsnaf;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            esnafad = (TextView) itemView.findViewById(R.id.esnafAd); //row.xml içindeki ikinci textView
            esnafcalismasaat = (TextView) itemView.findViewById(R.id.esnafCalismaSaat); //row.xml içindeki ikinci textView
            esnafadres = (TextView) itemView.findViewById(R.id.esnafAdres); //row.xml içindeki ikinci textView
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFilter();
                    if (filtrelenenlerinOrjID.isEmpty()) { //elemana tıklandığında fitreleme yoksa, secileninID yi Değişmemiş listeye göre yap
                        secileninId = esnaflarId.get(secilenEsnaf);
                    } else {   //elemana tıklandığında filtreleme varsa, secileninID yi filtrelemiş listeden getirerek yap
                        secileninId = esnaflarId.get(filtrelenenlerinOrjID.get(secilenEsnaf));
                    }
                    Log.d("click", secileninId);
                    //intent ile secileninId yi diğer sayfaya gonder
                    Intent esnafSecim_tarihSaatSecim=new Intent(mcontext.getApplicationContext(),TarihSaatSecim.class);
                    esnafSecim_tarihSaatSecim.putExtra("secileninId",secileninId);//secilen kişinin idsini sonraki sayfaya aktar
                    mcontext.startActivity(esnafSecim_tarihSaatSecim);

                }
            });
        }
    }
}
