package datapole.ocrtext;

import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

/**
 * Created by dhruv on 15/3/17.
 */

public class allCardRecyclerViewAdapter
        extends RecyclerView
        .Adapter<allCardRecyclerViewAdapter
        .DataObjectHolder> {

    public static final String TAG = "myRecViewAdapter";
    private ArrayList<CardObject1> mCardSet;
    private static MyClickListener myClickListener;
    private Context context;
    public View view1;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView imageDrawable;
        TextView txtDate;
//        TextView txtPosition;
//        TextView txtCompany;

        public DataObjectHolder(View itemView) {
            super(itemView);
            imageDrawable = (ImageView) itemView.findViewById(R.id.img_text);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);

            Log.d(TAG, "dataObjHolderALLCARDS");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), ShowCardDetails.class);
            int pos = getAdapterPosition();
            i.putExtra("CardPosition", pos);

//            i.putExtra("")                                    // yahan pe sending timke par snd the phone no.s and ither details
            v.getContext().startActivity(i);

            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public allCardRecyclerViewAdapter(ArrayList<CardObject1> myCardSet, Context context) {
        mCardSet = myCardSet;
        for (int i = 0; i < myCardSet.size(); i++) {
            Log.d(TAG, "mCardSetVal: " + mCardSet.get(i).getTxtName());
        }
        this.context = context;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_cards_list_cardview, parent, false);

        view1 = view;
        Log.d(TAG, "onCrateViewHolderOfAllCArds");
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    SharedPreferences txtURI = view1.getContext().getSharedPreferences("txtURI", 0);
    final int ind = txtURI.getInt("ind", 0);

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.txtDate.setText(mCardSet.get(position).getTxtName());

        Log.d(TAG, "mCardValS: " + mCardSet.get(position).getTxtName());

//        if (mCardSet.get(position).getmDrawableImage() != 0) {
//            holder.imageDrawable.setImageResource(mCardSet.get(position).getmDrawableImage());
//        } else {
//            char ch = 'A';
//            if (mCardSet.get(position).getTxtName().length() > 0) {
//                ch = mCardSet.get(position).getTxtName().charAt(0);
//            }
//            TextDrawable drawable = TextDrawable.builder().beginConfig().fontSize(100).bold().endConfig()
//                    .buildRect(String.valueOf(ch).toUpperCase(), Color.rgb(0, 105, 0));
//            holder.imageDrawable.setImageDrawable(drawable);
//        }

        holder.imageDrawable.setImageResource(txtURI.getString("uri" +position));       // setImageResource from url ka code

        holder.txtDate.setText(txtURI.getString("date"+position,null));
    }

    public void addItem(CardObject1 cardObject, int index) {
        mCardSet.add(index, cardObject);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mCardSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        int x = 0;
        if (mCardSet == null) {
            x = 0;
        } else {
            x = mCardSet.size();
        }
        return x;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}