package projekti.mobiiliprojekti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.timessquare.CalendarPickerView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LaitaVuokralle extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();


    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef =  storage.getReference();
    private final StorageReference mokkiRef = storageRef.child("Mökkien kuvia/");

    private DatabaseReference dbVarmistamatonMokki;


    private Uri mImageUri;
    private ProgressBar uploadImageProgressBar;

    private EditText editOtsikko;
    private EditText editHinta;
    private EditText editOsoite;
    private Spinner editHuoneet;
    private EditText editNeliot;
    private EditText editLammitys;
    private Spinner editVesi;
    private Spinner editSauna;
    private  EditText editKuvaus;
    //private CalendarView setDateDalendar;
    private TextView textViewDates;
    private TextView textViewVuokrattavissa;
    private TextView textViewVuokraAika;
    private TextView bTakaisinMokkiListaan;

    private TextView bUploadImage;
    private TextView bChooseimage;
    private TextView bAsetaVuokralle;

    private ImageView ImageViewUpload;

    private String eOtsikko;
    private int eHinta;
    private String eOsoite;
    private String eHuoneet;
    private int eNeliot;
    private String eLammitys;
    private String eVesi;
    private String eSauna;
    private String eKuvaus;
    private String UID;
    private String eID;
    private String eVuokraaja;
    private String eOtsikkoID;
    private String MokkiKuva;

    /*
    private  ArrayList<String> dateList;
    private String selectedYear;
    private String selectedMonth;
    private String selectedDay;
    private String selectedDate;
    private String dates;
     */

    //KALENTERI VARIT
    List<Date> dateList;
    ArrayList<String> stringDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laita_vuokralle);

        dbVarmistamatonMokki = FirebaseDatabase.getInstance().getReference("Varmistamattomat mökit/" + currentUser.getUid());

        bTakaisinMokkiListaan = findViewById(R.id.bTakaisinMokkiListaan);
        bAsetaVuokralle = findViewById(R.id.bAsetaVuokralle);
        bChooseimage = findViewById(R.id.bChooseImage);
        bUploadImage = findViewById(R.id.bUpload);
        ImageViewUpload = findViewById(R.id.ImageViewUpload);
        uploadImageProgressBar = findViewById(R.id.UploadImageProgressBar);
        //setDateDalendar = findViewById(R.id.date_pick_calendar);
        textViewDates = findViewById(R.id.textViewDates);
        textViewVuokrattavissa = findViewById(R.id.textViewVuokrattavissa);
        textViewVuokraAika = findViewById(R.id.textViewVuokraAika);

        bUploadImage.setVisibility(View.GONE);
        bAsetaVuokralle.setVisibility(View.GONE);

        //bChooseimage.setVisibility(View.GONE);


        bAsetaVuokralle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eHinta = Integer.parseInt(editHinta.getText().toString());
                eNeliot = Integer.parseInt(editNeliot.getText().toString());
                if(eOtsikko.matches("") || eHinta == 0 || eOsoite.matches("")
                    || eLammitys.matches("") || eNeliot == 0 || eKuvaus.matches("")){
                    Toast.makeText(LaitaVuokralle.this, "Täytä kaikki tiedot mökistäsi", Toast.LENGTH_SHORT).show();
                }else{
                    uploadImage();
                }
            }
        });
        bChooseimage.setOnClickListener(v -> OpenImageChooser());

        bAsetaVuokralle.setVisibility(View.GONE);

        bUploadImage.setOnClickListener(v -> uploadImage());

        bTakaisinMokkiListaan.setOnClickListener(View -> {
            Intent takaisinMokkiListaan = new Intent(this, Mokki_List.class);
            startActivity(takaisinMokkiListaan);
        });

        Intent takaisinIlmoitukseen = getIntent();
        editOtsikko = findViewById(R.id.EditOtsikko);
        eOtsikko = editOtsikko.getText().toString();
        eOtsikko = takaisinIlmoitukseen.getStringExtra("eOtsikko");
        editOtsikko.setText(eOtsikko);

        editHinta = findViewById(R.id.EditHinta);
        editHinta.setText(String.valueOf(0));
        eHinta = Integer.parseInt(editHinta.getText().toString());
        eHinta = takaisinIlmoitukseen.getIntExtra("eHinta",0);
        editHinta.setText(String.valueOf(eHinta));

        editOsoite = findViewById(R.id.EditOsoite);
        eOsoite = editOsoite.getText().toString();
        eOsoite = takaisinIlmoitukseen.getStringExtra("eOsoite");
        editOsoite.setText(eOsoite);

        editHuoneet = findViewById(R.id.HuoneMaaraSpinner);
        eHuoneet = editHuoneet.getSelectedItem().toString();

        editNeliot = findViewById(R.id.EditNelioMaara);
        editNeliot.setText(String.valueOf(0));
        eNeliot = Integer.parseInt(editNeliot.getText().toString());
        eNeliot = takaisinIlmoitukseen.getIntExtra("eNeliot",0);
        editNeliot.setText(String.valueOf(eNeliot));

        editLammitys = findViewById(R.id.EditLammitys);
        eLammitys = editLammitys.getText().toString();
        eLammitys = takaisinIlmoitukseen.getStringExtra("eLammitys");
        editLammitys.setText(eLammitys);

        editVesi = findViewById(R.id.VesiSpinner);
        eVesi = editVesi.getSelectedItem().toString();

        editSauna = findViewById(R.id.SaunaSpinner);
        eSauna = editSauna.getSelectedItem().toString();

        editKuvaus = findViewById(R.id.EditKuvaus);
        eKuvaus = editKuvaus.getText().toString();
        eKuvaus = takaisinIlmoitukseen.getStringExtra("eKuvaus");
        editKuvaus.setText(eKuvaus);

        checkText();
        setDates();
    }

    private void AsetaVuokralle()
    {
        Intent varmistaIntent = new Intent(this, IlmoitusVarmistus.class);

        //Toast.makeText(this, "Mökki lisätty vuokralle", Toast.LENGTH_LONG).show();

        varmistaIntent.putExtra("eOtsikko", eOtsikko);
        varmistaIntent.putExtra("eHinta", eHinta);
        varmistaIntent.putExtra("eOsoite", eOsoite);
        varmistaIntent.putExtra("eHuoneet", eHuoneet);
        varmistaIntent.putExtra("eNeliot", eNeliot);
        varmistaIntent.putExtra("eLammitys", eLammitys);
        varmistaIntent.putExtra("eVesi", eVesi);
        varmistaIntent.putExtra("eSauna", eSauna);
        varmistaIntent.putExtra("eKuvaus", eKuvaus);
        varmistaIntent.putExtra("eUID", UID);
        varmistaIntent.putExtra("dates", stringDates);

        startActivity(varmistaIntent);
    }

    /*private void asetaMokki()
    {
        eVuokraaja = currentUser.getDisplayName();
        eID = currentUser.getUid();
        eOtsikkoID = UID;
        MokkiKuva = mImageUri.toString();

        eOtsikkoID = dbVarmistamatonMokki.push().getKey();

        MokkiItem mokki = new MokkiItem(MokkiKuva, eOtsikko, eHinta, eOsoite, eHuoneet, eNeliot, eLammitys,
                eVesi, eSauna, eKuvaus, eOtsikkoID, eVuokraaja, eID, dates);

        dbVarmistamatonMokki.child(eOtsikkoID).setValue(mokki);
        Log.d("naytaaa",eOtsikko);
        AsetaVuokralle();
    }*/

    private void OpenImageChooser()
    {
        Intent chooseImageIntent = new Intent();
        chooseImageIntent.setType("image/*");
        chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri), null, options);
                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;
                if (imageHeight >= 1) {
                        bAsetaVuokralle.setVisibility(View.VISIBLE);
                        if (imageWidth >= 1) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                            ImageViewUpload.setImageBitmap(bitmap);
                        }
                     else {
                        Toast.makeText(getApplicationContext(), "Virhe kuvan kanssa, syötä toinen kuva", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Virhe kuvan kanssa, syötä toinen kuva", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void uploadImage() {
        if (getfileExtension(mImageUri).equals("jpg") || getfileExtension(mImageUri).equals("png") || getfileExtension(mImageUri).equals("jpeg")) {
            Log.e("Tag", getfileExtension(mImageUri));
            if (mImageUri != null) {
                Log.d("osoiteString",eOtsikko);

                UID = UUID.randomUUID().toString();

                mokkiRef.child("/" + currentUser.getUid() + UID).putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uploadImageProgressBar.setProgress(0);
                            }
                        }, 5000);
                        mokkiRef.child("Mökkien kuvia/"  + currentUser.getUid() + UID).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                   Picasso.get().load(uri).fit().centerCrop().into(ImageViewUpload);
                                });
                        Toast.makeText(getApplicationContext(), "Kuva lisätty", Toast.LENGTH_LONG).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Virhe kuvan latauksessa", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                uploadImageProgressBar.setProgress((int)progress);
                                if(progress == 100){
                                    AsetaVuokralle();
                                    //asetaMokki();
                                }
                            }
                        });
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Virheellinen tiedostomuoto",Toast.LENGTH_SHORT).show();
        }
    }

    private void setDates()
    {
        //KALENTERI
        stringDates = new ArrayList<>();
        dateList = new ArrayList<>();
        Date today = new Date();
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, 2);

        CalendarPickerView datePicker = findViewById(R.id.kalenteri);
        datePicker.init(today, nextMonth.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                dateList = datePicker.getSelectedDates();
                Log.d("TAG", "stringDates = " + stringDates);
                stringDates.clear();

                for(Date i : dateList) {

                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                    String str = fmt.format(i);
                    stringDates.add(str + ":0");
                }
                Log.d("TAG", "stringDates = " + stringDates);

                String tmpStr = stringDates.get(0);
                tmpStr = tmpStr.substring(0, tmpStr.length() - 2);
                Log.d("TAG", "tmpStr = " + tmpStr);

                String tmpStr2 = stringDates.get(stringDates.size() - 1);
                tmpStr2 = tmpStr2.substring(0, tmpStr2.length() - 2);
                Log.d("TAG", "tmpStr = " + tmpStr2);

                textViewVuokrattavissa.setText("Vuokrattavissa alkaen:");
                textViewVuokraAika.setText(tmpStr + " - " + tmpStr2);
                dateList.clear();
            }
            @Override
            public void onDateUnselected(Date date) {

            }
        });
/*

        dateList = new ArrayList<String>();

        setDateDalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = String.valueOf(year);
                selectedMonth = String.valueOf(month + 1);
                selectedDay = String.valueOf(dayOfMonth);

                selectedDate = selectedYear + "/" + selectedMonth + "/" + selectedDay;

                if(!dateList.contains(selectedDate)){
                    dateList.add(selectedDate);
                }else if(dateList.contains(selectedDate)){
                    dateList.remove(selectedDate);
                }

                StringBuilder builder = new StringBuilder();
                for(String s : dateList){
                    builder.append(s).append(" ");
                }
                textViewDates.setText(builder.toString());

                Log.d("asd", String.valueOf(dateList));
                Log.d("dates", String.valueOf(dates));
                dates = dateList.toString().replaceAll("\\[", "").replaceAll("\\(", "")
                        .replaceAll("\\]", "").replaceAll("\\)", "");
            }
        });
        */
    }



    private void checkText() {
        editOtsikko.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                eOtsikko = editOtsikko.getText().toString();
            }
        });


        editOsoite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                eOsoite = editOsoite.getText().toString();
            }
        });

        editHuoneet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    eHuoneet = editHuoneet.getSelectedItem().toString();
                }else if(position == 1){
                    eHuoneet = editHuoneet.getSelectedItem().toString();
                }
                else if(position == 2){
                    eHuoneet = editHuoneet.getSelectedItem().toString();
                }
                else if(position == 3){
                    eHuoneet = editHuoneet.getSelectedItem().toString();
                }
                else if(position == 4){
                    eHuoneet = editHuoneet.getSelectedItem().toString();
                }
                else if(position == 5){
                    eHuoneet = editHuoneet.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        editLammitys.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                eLammitys = editLammitys.getText().toString();
            }
        });

        editVesi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    eVesi = editVesi.getSelectedItem().toString();
                }else if(position == 1){
                    eVesi = editVesi.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editSauna.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    eSauna = editSauna.getSelectedItem().toString();
                }else if(position == 1){
                    eSauna = editSauna.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editKuvaus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                eKuvaus = editKuvaus.getText().toString();
            }
        });

    }
}