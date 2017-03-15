package datapole.ocrtext;

import android.net.Uri;
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
        TextView txtName;
        ImageView txtShare;
//        TextView txtPosition;
//        TextView txtCompany;

        public DataObjectHolder(View itemView) {
            super(itemView);
            imageDrawable = (ImageView) itemView.findViewById(R.id.img_text);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtShare = (ImageView) itemView.findViewById(R.id.share);

            txtShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                            // TO SHARE THE FILE ONCE YOU CAN ACTUALLY SAVE IT LOL
                }
            });

            Log.d(TAG, "dataObjHolderALLCARDS");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            SharedPreferences pref = v.getContext().getSharedPreferences("txtURI", 0);
            Log.d(TAG,"path:: "+pref.getString("path" + String.valueOf(getAdapterPosition()), "0"));

//            Intent geoIntent = new Intent(
//                    android.content.Intent.ACTION_VIEW, Uri
//                    .parse(pref.getString("path" + String.valueOf(getAdapterPosition()), "0")));        // intent to open that file
//            v.getContext().startActivity(geoIntent);

//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("*/*");
//            String[] mimetypes = {"image/*", "video/*"};
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//            v.getContext().startActivity(intent);

            int ind = pref.getInt("ind",0);
//            Intent i = new Intent();
//            i.setAction(android.content.Intent.ACTION_VIEW);
//            i.setData(Uri.parse(pref.getString("path"+String.valueOf(ind-1-getAdapterPosition()),null)));
//            v.getContext().startActivity(i);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(pref.getString("path"+String.valueOf(ind-1-getAdapterPosition()),null));
            intent.setDataAndType(uri, "text/plain");
            v.getContext().startActivity(intent);

//            myClickListener.onItemClick(getAdapterPosition(), v);
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

//    SharedPreferences txtURI = view1.getContext().getSharedPreferences("txtURI", 0);
//    final int ind = txtURI.getInt("ind", 0);

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        Log.d(TAG, "mCardValS: " + mCardSet.get(position).getTxtName());
        holder.imageDrawable.setImageURI(Uri.parse(mCardSet.get(position).getmDrawableImage()));       // setImageResource from url ka code
        holder.txtDate.setText(mCardSet.get(position).getTxtDate());
        holder.txtName.setText(mCardSet.get(position).getTxtName());
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