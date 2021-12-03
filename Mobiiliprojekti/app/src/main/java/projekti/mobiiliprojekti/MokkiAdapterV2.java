package projekti.mobiiliprojekti;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;;

public class MokkiAdapterV2 extends RecyclerView.Adapter<MokkiAdapterV2.MokkiviewHolderV2>
{
    private final Context mContext;
    private List<MokkiItem> mMokkiList;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void addFavoriteButtonClick(int position);
        void removeFavoriteButtonClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mOnItemClickListener = listener;
    }

    public MokkiAdapterV2(Context context, List<MokkiItem> mokkiItems)
    {
        mContext = context;
        mMokkiList = mokkiItems;
    }

    @Override
    public void onBindViewHolder(@NonNull MokkiviewHolderV2 holder, int position) {
        MokkiItem mokkiItemCurrent = mMokkiList.get(position);
        holder.textViewOtsikko.setText(mokkiItemCurrent.getOtsikko());
        holder.textViewHinta.setText(String.valueOf(mokkiItemCurrent.getHinta()));
        Picasso.get().load(mokkiItemCurrent.getMokkiImage()).placeholder(R.mipmap.ic_launcher)
                .fit().centerCrop().into(holder.imageViewKuva);
    }

    @Override
    public int getItemCount() {
        return mMokkiList.size();
    }

    public static class MokkiviewHolderV2 extends RecyclerView.ViewHolder
    {
        public TextView textViewOtsikko;
        public TextView textViewHinta;
        public ImageView imageViewKuva;
        ImageView imageViewDelete;
        public Button bOmatMokit;
        private FirebaseAuth mauth = FirebaseAuth.getInstance();        //suosikki
        private FirebaseUser currentUser = mauth.getCurrentUser();
        ToggleButton favoriteButton;

        public MokkiviewHolderV2(View itemView, OnItemClickListener listener)
        {
            super(itemView);

            textViewOtsikko = itemView.findViewById(R.id.TextViewOtsikko);
            textViewHinta = itemView.findViewById(R.id.TextViewHinta);
            imageViewKuva = itemView.findViewById(R.id.imageViewMokkiKuva);
            //imageViewDelete = itemView.findViewById(R.id.ImageViewDelete);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);

            bOmatMokit = itemView.findViewById(R.id.bOmatMökit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            /*imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });*/
            //suosikkinappi toimintaa..
            favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        Log.d("tag", "favoritebutton on chekattu");
                        favoriteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(listener != null){
                                    int position = getAdapterPosition();
                                    if(position != RecyclerView.NO_POSITION){
                                        listener.addFavoriteButtonClick(position);
                                    }
                                }
                            }
                        });
                    }
                    else {
                        Log.d("tag", "favoritebutton ei oo chekattu");
                        favoriteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(listener != null){
                                    int position = getAdapterPosition();
                                    if(position != RecyclerView.NO_POSITION){
                                        listener.removeFavoriteButtonClick(position);
                                    }
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public  MokkiviewHolderV2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.mokki_item, parent, false);
        MokkiviewHolderV2 mvh = new MokkiviewHolderV2(v, mOnItemClickListener);
        return mvh;
    }

    public void filteredList(ArrayList<MokkiItem> filteredList)
    {
        mMokkiList = filteredList;
        notifyDataSetChanged();
    }

}
