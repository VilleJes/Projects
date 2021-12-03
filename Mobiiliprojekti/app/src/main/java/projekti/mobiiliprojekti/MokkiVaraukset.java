package projekti.mobiiliprojekti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

class MokkiVarauksetAdapter extends Fragment {
    private View VarauksetView;
    private RecyclerView varauksetList;

    private DatabaseReference invoiceRef;
    private FirebaseAuth mAuth;
    private String currentUserID="";


    public MokkiVarauksetAdapter() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        VarauksetView = inflater.inflate(R.layout.activity_varaukset_adapter, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        invoiceRef = FirebaseDatabase.getInstance().getReference().child("Invoices").child(currentUserID).child("Tilaukset");

        varauksetList = VarauksetView.findViewById(R.id.recyclerViewAdapter);
        varauksetList.setLayoutManager(new LinearLayoutManager(getContext()));


        return VarauksetView;
    }

    public static class  VarauksetViewHolder extends RecyclerView.ViewHolder
    {
        public TextView otsikkoText, hintaText, paivatText, vuokraajaText, spostiText, nroText;
        public ImageView vuokraImage;


        public VarauksetViewHolder(@NonNull View itemView)
        {
            super(itemView);

            otsikkoText = itemView.findViewById(R.id.TextViewOtsikkoAdapter);
            hintaText = itemView.findViewById(R.id.TextViewHintaAdapter);
            paivatText = itemView.findViewById(R.id.TextViewPaivatAdapter);
            vuokraajaText = itemView.findViewById(R.id.TextViewVuokraajaAdapter);
            spostiText = itemView.findViewById(R.id.TextViewVuokraajaSpostiAdapter);
            nroText = itemView.findViewById(R.id.TextViewVuokraajaNroAdapter);
            vuokraImage = itemView.findViewById(R.id.imageViewMokkiKuvaAdapter);
        }
    }
}


public class MokkiVaraukset extends AppCompatActivity {

    private final FirebaseAuth mauth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mauth.getCurrentUser();
    private DatabaseReference invoiceRef;
    private LinearLayoutManager linearLayoutManager2;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef =  storage.getReference();

    private ImageView profiiliKuva;
    private ImageView takaisinBtn;

    private RecyclerView userInvoicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mokki_varaukset);
        profiiliKuva = findViewById(R.id.profiiliKuva);
        takaisinBtn = findViewById(R.id.goBack);

        invoiceRef = FirebaseDatabase.getInstance().getReference().child("Invoices").child(currentUser.getUid()).child("Tilaukset");

        storageRef.child("ProfilePictures/"+currentUser.getUid()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(getApplicationContext()).load(uri.toString()).circleCrop().into(profiiliKuva);
                })
                .addOnFailureListener(e -> {
                    profiiliKuva.setImageResource(R.mipmap.ic_launcher);
                });

        takaisinBtn.setOnClickListener(v -> {
            finish();
        });

        Init();
    }
    private void Init() {
        userInvoicesList = findViewById(R.id.recyclerViewAdapter);

        linearLayoutManager2 = new LinearLayoutManager(this);
        userInvoicesList.setLayoutManager(linearLayoutManager2);
    }

    public void onClick_Usermenu(View view) {
        PopupMenu popup = new PopupMenu(this, profiiliKuva);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.user:
                    Intent userIntent = new Intent(this,ProfiiliActivity.class);
                    startActivity(userIntent);
                    finish();
                    break;
                case R.id.chat:
                    Intent chatIntent = new Intent(this,ChatActivity.class);
                    startActivity(chatIntent);
                    finish();
                    break;
                case R.id.logout:
                    mauth.signOut();
                    Intent signOutIntent = new Intent(this, LoginActivity.class);
                    startActivity(signOutIntent);
                    finish();
                    break;
            }
            return false;
        });
        if(mauth.getCurrentUser() != null) {
            popup.inflate(R.menu.menu_list);
            if (mauth.getCurrentUser().getDisplayName() != null) {
                popup.getMenu().findItem(R.id.user).setTitle(mauth.getCurrentUser().getDisplayName());
            } else {
                popup.getMenu().findItem(R.id.user).setTitle(mauth.getCurrentUser().getEmail());
            }
            popup.show();
        }
        else if(mauth.getCurrentUser() == null) {
            Intent kirjauduIntent = new Intent(this, LoginActivity.class);
            startActivity(kirjauduIntent);
            finish();
        }
    }

    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Invoices> options =
                new FirebaseRecyclerOptions.Builder<Invoices>()
                        .setQuery(invoiceRef, Invoices.class)
                        .build();

        FirebaseRecyclerAdapter<Invoices, MokkiVarauksetAdapter.VarauksetViewHolder> adapter =
                new FirebaseRecyclerAdapter<Invoices, MokkiVarauksetAdapter.VarauksetViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final MokkiVarauksetAdapter.VarauksetViewHolder holder, int position, @NonNull Invoices model)
                    {
                        final String usersIDs = getRef(position).getKey();

                        invoiceRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {

                                        final String otsikko = dataSnapshot.child("otsikko").getValue().toString();
                                        final String hinta = dataSnapshot.child("hinta").getValue().toString();
                                        final String paivat = dataSnapshot.child("paivamaarat").getValue().toString();
                                        final String sposti = dataSnapshot.child("asiakkaan sposti").getValue().toString();
                                        final String nro = dataSnapshot.child("asiakkaan nro").getValue().toString();
                                        final String image = dataSnapshot.child("mokkikuva").getValue().toString();
                                        Log.d("tag", otsikko);
                                        Log.d("tag", hinta);
                                        Picasso.get().load(image).into(holder.vuokraImage);
                                        holder.otsikkoText.setText(otsikko);
                                        holder.hintaText.setText("Hinta / pv: " + hinta);
                                        holder.nroText.setText(nro);
                                        holder.paivatText.setText(paivat);
                                        holder.spostiText.setText(sposti);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MokkiVarauksetAdapter.VarauksetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_varaukset_adapter, viewGroup, false);
                        return new MokkiVarauksetAdapter.VarauksetViewHolder(view);
                    }
                };

        userInvoicesList.setAdapter(adapter);
        adapter.startListening();
    }
}