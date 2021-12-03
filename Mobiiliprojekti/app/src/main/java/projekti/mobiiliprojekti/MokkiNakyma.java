package projekti.mobiiliprojekti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.timessquare.CalendarPickerView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MokkiNakyma extends AppCompatActivity {

    private TextView bTakaisinMokkilistaan;
    private TextView bVuokraa;
    private TextView bMuokkaa;
    private ImageView ImageViewDelete;
    private TextView bChat;
    private TextView bTilanne;
    private Context mContext;

    private String setVisibility = "";

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef =  storage.getReference();
    private DatabaseReference fbDatabaseRef;
    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();
    private String deleteKey;

    private String MokkiImage;
    private String MokkiOtsikko;
    private int MokkiHinta;
    private String MokkiOsoite;
    private String MokkiHuoneet;
    private int MokkiNelio;
    private String MokkiLammitys;
    private String MokkiVesi;
    private String MokkiSauna;
    private String MokkiKuvaus;
    private String OtsikkoID;
    private String Vuokraaja;
    private String VuokraajaID;
    private String mDates;

    //private String gtDates;

    private List<String> splitDates;
    /*
    private List<String> splitString;
    private List<String> dateList;
    private String selectedYear;
    private String selectedMonth;
    private String selectedDay;
    private String selectedDate;

     */
    private String replace;

    private TextView textViewSelectedDates;

    //private CalendarView calendarDates;

    //KALENTERI VARIT
    private boolean textViewFlag = true;
    List<Date> listDates;
    List<Date> tmpDates;
    List<Date> highlightedDates;
    ArrayList<String> stringDates;
    ArrayList<String> valitut_sDates;
    ArrayList<String> vapaat_sDates;
    ArrayList<String> final_sDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mokki_nakyma);

        fbDatabaseRef = FirebaseDatabase.getInstance().getReference("Vuokralla olevat mökit/");

        Intent intent = getIntent();
        MokkiItem mokkiItem = intent.getParcelableExtra("Mokki");

        MokkiImage = mokkiItem.getMokkiImage();
        MokkiOtsikko = mokkiItem.getOtsikko();
        MokkiHinta = mokkiItem.getHinta();
        MokkiOsoite = mokkiItem.getOsoite();
        MokkiHuoneet = mokkiItem.getHuoneMaara();
        MokkiNelio = mokkiItem.getNelioMaara();
        MokkiLammitys = mokkiItem.getLammitys();
        MokkiVesi = mokkiItem.getVesi();
        MokkiSauna = mokkiItem.getSauna();
        MokkiKuvaus = mokkiItem.getKuvaus();
        OtsikkoID = mokkiItem.getOtsikkoID();
        Vuokraaja = mokkiItem.getVuokraaja();
        VuokraajaID = mokkiItem.getVuokraajaID();
        mDates = mokkiItem.getmDates();

        //String Mokkiomistaja = mokkiItem.getOmistaja();

        ImageView imageViewMokki = findViewById(R.id.ImageMokkiNakyma);
        Picasso.get().load(mokkiItem.getMokkiImage()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(imageViewMokki);

        TextView textViewOtsikko = findViewById(R.id.OtsikkoMokkiNakyma);
        textViewOtsikko.setText(MokkiOtsikko);

        TextView textViewHinta = findViewById(R.id.HintaMokkiNakyma);
        textViewHinta.setText(String.valueOf(MokkiHinta));

        TextView textViewOsoite = findViewById(R.id.OsoiteMokkiNakyma);
        textViewOsoite.setText(MokkiOsoite);

        TextView textViewHuoneet = findViewById(R.id.HuoneetMokkiNakyma);
        textViewHuoneet.setText(MokkiHuoneet);

        TextView textViewNeliot = findViewById(R.id.NeliotMokkiNakyma);
        textViewNeliot.setText(String.valueOf(MokkiNelio));

        TextView textViewLammitys = findViewById(R.id.LammitysMokkiNakyma);
        textViewLammitys.setText(MokkiLammitys);

        TextView textViewVesi = findViewById(R.id.VesiMokkiNakyma);
        textViewVesi.setText(MokkiVesi);

        TextView textViewSauna = findViewById(R.id.SaunaMokkiNakyma);
        textViewSauna.setText(MokkiSauna);

        TextView textViewValitsePaiva = findViewById(R.id.textViewValitsePaivamaara);

        TextView textViewDates = findViewById(R.id.textViewDates);
        if(mDates != null){
            replace = mDates.replaceAll("\\[", "").replaceAll("\\(", "")
                    .replaceAll("\\]", "").replaceAll("\\)", "")
                    .replaceAll(" ", "");
            //textViewDates.setText(replace);
            //Log.d("dfg", mDates);
            //Log.e("asd", replace);
            splitDates = Arrays.asList(replace.split(",", -1));
        }
        //Log.d("dfg", mDates);
        //Log.e("asd", replace);


        //calendarDates = findViewById(R.id.date_pick_calendar);
        textViewSelectedDates = findViewById(R.id.textViewSelectedDates);

        TextView textViewKuvaus = findViewById(R.id.KuvausMokkiNakyma);
        textViewKuvaus.setText(MokkiKuvaus);

        bVuokraa = findViewById(R.id.bVuokraa);
        bMuokkaa = findViewById(R.id.bMuokkaa);
        bChat = findViewById(R.id.chatBtn);
        bTilanne = findViewById(R.id.varauksetBtn);
        ImageViewDelete = findViewById(R.id.ImageViewDelete);

        //bVuokraa.setVisibility(View.GONE);
        bMuokkaa.setVisibility(View.GONE);
        ImageViewDelete.setVisibility(View.GONE);

        //gtDates = intent.getStringExtra("dates");

        setVisibility = intent.getStringExtra("setVisibility");
        if(setVisibility.matches("omatMokit")){
            bVuokraa.setVisibility(View.GONE);
            bMuokkaa.setVisibility(View.VISIBLE);
            ImageViewDelete.setVisibility(View.VISIBLE);
            bChat.setVisibility(View.GONE);
            textViewDates.setVisibility(View.GONE);
            //calendarDates.setVisibility(View.GONE);
            bTilanne.setVisibility(View.GONE);

            bTilanne.setOnClickListener(v -> {
                Intent varausIntent = new Intent(this,MokkiVaraukset.class);
                startActivity(varausIntent);
            });
        }else if(setVisibility.matches("kaikkiMokit")){
            if(currentUser != null) {
                bVuokraa.setVisibility(View.VISIBLE);
            }
            else {
                bVuokraa.setVisibility(View.GONE);
            }
            bMuokkaa.setVisibility(View.GONE);
            textViewDates.setVisibility(View.VISIBLE);
            //calendarDates.setVisibility(View.VISIBLE);
            //ImageViewDelete.setVisibility(View.GONE);
        }else if(setVisibility.matches("vuokratutMokit")) {
            bVuokraa.setVisibility(View.GONE);
            bMuokkaa.setVisibility(View.GONE);
            ImageViewDelete.setVisibility(View.GONE);
            bChat.setVisibility(View.VISIBLE);
            //calendarDates.setVisibility(View.GONE);
            //textViewDates.setText(gtDates);
            /*
            textViewPaivat.setText("Valitsemasi paivat: ");
            textViewValitsePaiva.setVisibility(View.GONE);

             */
        }
        Log.d("Tag",setVisibility);


        deleteKey = intent.getStringExtra("deleteKey");
        ImageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbDatabaseRef.child(deleteKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MokkiNakyma.this, "Mokki poistettu", Toast.LENGTH_LONG).show();
                        StorageReference imageRef = storage.getReferenceFromUrl(MokkiImage);
                        imageRef.delete();
                        ImageViewDelete.setVisibility(View.GONE);
                        bMuokkaa.setVisibility(View.GONE);
                        bChat.setVisibility(View.GONE);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MokkiNakyma.this, "Ei onnistunut", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



        bTakaisinMokkilistaan = findViewById(R.id.bTakaisinMokkiListaan);

        bTakaisinMokkilistaan.setOnClickListener(View ->{
            Intent mokkiIntent = new Intent(this,Mokki_List.class);
            startActivity(mokkiIntent);
        });
        bChat.setOnClickListener(View -> {
            Intent chatIntent = new Intent(this,ChatActivity.class);
            chatIntent.putExtra("ID",VuokraajaID);
            chatIntent.putExtra("name",Vuokraaja);
            startActivity(chatIntent);
        });

            bVuokraa.setOnClickListener(v -> {
                if(valitut_sDates.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Valitse päivämäärät jotka haluat vuokrata", Toast.LENGTH_LONG).show();
                } else {
                    Intent vuokraIntent = new Intent(this, CheckoutActivity.class);
                    vuokraIntent.putExtra("name", Vuokraaja);
                    vuokraIntent.putExtra("hinta",MokkiHinta);
                    vuokraIntent.putExtra("otsikko",MokkiOtsikko);
                    vuokraIntent.putExtra("osoite",MokkiOsoite);
                    vuokraIntent.putExtra("id",VuokraajaID);
                    vuokraIntent.putExtra("image",MokkiImage);
                    vuokraIntent.putStringArrayListExtra("paivat", (ArrayList<String>) valitut_sDates);
                    vuokraIntent.putStringArrayListExtra("dbpaivat", (ArrayList<String>) final_sDates);
                    vuokraIntent.putExtra("id",VuokraajaID);
                    vuokraIntent.putExtra("image",MokkiImage);
                    vuokraIntent.putExtra("otsikkoID", OtsikkoID);
                    startActivity(vuokraIntent);
                }

        });

        bMuokkaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muokkaaIlmoitusta();
            }
        });

        //KALENTERI
        listDates = new ArrayList<>();
        highlightedDates = new ArrayList<>();
        tmpDates = new ArrayList<>();
        stringDates = new ArrayList<>();
        valitut_sDates = new ArrayList<>();
        vapaat_sDates = new ArrayList<>();
        final_sDates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (String s : splitDates) {
            ParsePosition pos = new ParsePosition(0);
            Date date = sdf.parse(s, pos);
            listDates.add(date);
            if(!s.endsWith(":1")) {
                highlightedDates.add(date);
            }
        }

        Collections.sort(listDates);

        CalendarPickerView datePicker = findViewById(R.id.kalenteri);
        Calendar firstDay = Calendar.getInstance();
        Date tmpDate = listDates.get(0);
        firstDay.add(Calendar.DATE, - 1);
        firstDay.setTime(tmpDate);

        Calendar lastDay = Calendar.getInstance();
        Date tmpDate2 = listDates.get(listDates.size() - 1);
        lastDay.setTime(tmpDate2);
        lastDay.add(Calendar.DATE, + 1);

        if(setVisibility.matches("omatMokit")){
            datePicker.init(firstDay.getTime(), lastDay.getTime())
                    .displayOnly();
            textViewSelectedDates.setVisibility(View.GONE);
            textViewValitsePaiva.setVisibility(View.GONE);
        } else {
            datePicker.init(firstDay.getTime(), lastDay.getTime())
                    .inMode(CalendarPickerView.SelectionMode.RANGE);
        }

        datePicker.highlightDates(highlightedDates);

        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                valitut_sDates.clear();
                vapaat_sDates.clear();
                final_sDates.clear();

                bVuokraa.setClickable(true);
                bVuokraa.setBackground(getDrawable(R.drawable.button_enabled));

                //VALITAAN DATET
                tmpDates = datePicker.getSelectedDates();
                Log.d("TAG", "listDates = " + tmpDates);

                //VALITUT DATET STRINGEIKSI
                for(Date i : tmpDates) {
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                    String str = fmt.format(i);
                    Log.d("TAG", str);
                    valitut_sDates.add(str);
                }

                //TEXTVIEWILLE KASA EHTOJA
                if(textViewFlag && valitut_sDates.size() == 1) {
                    textViewSelectedDates.setText(valitut_sDates.get(0));
                    if(valitut_sDates.size() == 1) {  textViewFlag = true;  }
                    else {  textViewFlag = false;  }
                } else {
                    textViewSelectedDates.setText(valitut_sDates.get(0) + " - "
                            + valitut_sDates.get(valitut_sDates.size() - 1));
                    textViewFlag = true;
                }
                Log.d("valitut", "tmpStringDates selected dates = " + valitut_sDates.toString());

                //VÄLIAIKASEEN LISTAAN VAPAAT PÄIVÄT
                for (String s : splitDates) {
                    String tmpStr = s;
                    if(!tmpStr.endsWith(":1")) {
                        tmpStr = tmpStr.substring(0, tmpStr.length() - 2);
                        vapaat_sDates.add(tmpStr);
                    } else if (tmpStr.endsWith(":1")){
                        final_sDates.add(tmpStr);
                    }
                    //VERRATAAN VARATTUJA PÄIVIÄ VAPAISIIN
                    for (String s2 : valitut_sDates) {
                        //String tmpStr2 = s2 + ":1";
                        if(s.equals(s2 + ":1")){
                            Toast.makeText(getApplicationContext(), "Et voi valita jo varattua päivämäärää", Toast.LENGTH_SHORT).show();
                            bVuokraa.setClickable(false);
                            bVuokraa.setBackground(getDrawable(R.drawable.button_disabled));
                            break;
                        } else {
                            if(s2.equals(tmpStr)) {
                                vapaat_sDates.remove(tmpStr);
                                tmpStr = tmpStr + ":1";
                                final_sDates.add(tmpStr);
                            }
                        }
                    }
                }
                //LISTATAAN FINAALI PÄIVÄT JOTKA HEITETÄÄN DATABASEEN :0 JA :1 PÄÄTTEILLÄ
                for(String s : vapaat_sDates) {
                    if (!s.endsWith(":1")) {
                        String tmpStr = s + ":0";
                        final_sDates.add(tmpStr);
                    }
                }

                Log.d("TAG", "splitDates  = " + splitDates.toString());
                Log.d("TAG", "tmpStringDates2  = " + vapaat_sDates.toString());
                Log.d("TAG", "tmpStringDates  = " + valitut_sDates.toString());
                Log.d("TAG", "finalStringDates  = " + final_sDates.toString());
                Log.d("TAG", "otsikkoid = " + OtsikkoID);
            }
            @Override
            public void onDateUnselected(Date date) {

            }
        });
        //KALENTERI LOPPU

        //setDates();

    }
/*
    private void setDates()
    {

        dateList = new ArrayList<String>();

        calendarDates.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = String.valueOf(year);
                selectedMonth = String.valueOf(month + 1);
                selectedDay = String.valueOf(dayOfMonth);

                selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;

                if(!dateList.contains(selectedDate)){
                    if(splitDates.contains(selectedDate)){
                        dateList.add(selectedDate);
                    }
                }else if(dateList.contains(selectedDate)){
                    dateList.remove(selectedDate);
                }

                StringBuilder builder = new StringBuilder();
                for(String s : dateList){
                    builder.append(s).append(" ");
                }
                textViewSelectedDates.setText(builder.toString());

                Log.d("datelist", String.valueOf(dateList));
                Log.d("splitdate", String.valueOf(splitDates));
            }
        });
    }

 */

    public void onClick_Takaisin(View view) {
        Intent i = new Intent(this, Mokki_List.class);
        startActivity(i);
    }

    private void muokkaaIlmoitusta()
    {
        Intent muokkaaIntent = new Intent(this, MuokkausActivity.class);

        //Toast.makeText(this, "Mökki lisätty vuokralle", Toast.LENGTH_LONG).show();

        muokkaaIntent.putExtra("eKuva", MokkiImage);
        muokkaaIntent.putExtra("eOtsikko", MokkiOtsikko);
        muokkaaIntent.putExtra("eHinta", MokkiHinta);
        muokkaaIntent.putExtra("eOsoite", MokkiOsoite);
        muokkaaIntent.putExtra("eHuoneet", MokkiHuoneet);
        muokkaaIntent.putExtra("eNeliot", MokkiNelio);
        muokkaaIntent.putExtra("eLammitys", MokkiLammitys);
        muokkaaIntent.putExtra("eVesi", MokkiVesi);
        muokkaaIntent.putExtra("eSauna", MokkiSauna);
        muokkaaIntent.putExtra("eKuvaus", MokkiKuvaus);
        muokkaaIntent.putExtra("muokkaaKey", deleteKey);
        muokkaaIntent.putExtra("dates", String.valueOf(splitDates));
        //muokkaaIntent.putExtra("eUID", UID);


        startActivity(muokkaaIntent);
    }

}
