package projekti.mobiiliprojekti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.ArrayList;


public class MokkiAdapter extends RecyclerView.Adapter<MokkiAdapter.MokkiViewHolder> {
    private final ArrayList<MokkiItem> mMokki_scroll_list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    public static class MokkiViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mImageView;
        public TextView mTextViewOtsikko;
        public TextView mTextViewKuvaus;
        public TextView mTextViewHinta;

        public MokkiViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.imageView);
            mTextViewOtsikko = itemView.findViewById(R.id.TextViewOtsikko);
            //mTextViewKuvaus = itemView.findViewById(R.id.TextViewKuvaus);
            mTextViewHinta = itemView.findViewById(R.id.TextViewHinta);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }

    public MokkiAdapter(ArrayList<MokkiItem> mokki_scroll_list)
    {
        mMokki_scroll_list = mokki_scroll_list;
    }

    @Override
    public MokkiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mokki_item, parent, false);
        MokkiViewHolder mvh = new MokkiViewHolder(v, mListener);
        return mvh;
    }

    //position = kohde mitä klikataan
    //tällä haetaan position variaabelilla haetaan kohde ja sitä tarvitaan ainakun listaobjekteja haetaan
    @Override
    public void onBindViewHolder(MokkiViewHolder holder, int position) {
        MokkiItem currentMokki = mMokki_scroll_list.get(position);

        //holder.mImageView.setImageResource(currentMokki.getMokkiImage());
        holder.mTextViewOtsikko.setText(currentMokki.getOtsikko());
        //holder.mTextViewKuvaus.setText(currentMokki.getKuvaus());
        holder.mTextViewHinta.setText(currentMokki.getHinta());
    }

    @Override
    public int getItemCount() {
        return mMokki_scroll_list.size();
    }
}
