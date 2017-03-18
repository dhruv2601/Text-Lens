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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by dhruv on 15/3/17.
 */

/* Copyright: You can use the code as you want, just let me know about it :).
*
*  email: dhruvrathi15@gmail.com
*
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
        ImageView imgInfo;

        public String readFromFile(Context context, String path) {

            String ret = "";
            try {
                InputStream inputStream = context.openFileInput(path);

                if (inputStream != null) {
                    FileInputStream fis = new FileInputStream(new File(path));  // 2nd line
                    InputStreamReader inputStreamReader = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }
            return ret;
        }

        public DataObjectHolder(final View itemView) {
            super(itemView);
            imageDrawable = (ImageView) itemView.findViewById(R.id.img_text);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtShare = (ImageView) itemView.findViewById(R.id.share);
            imgInfo = (ImageView) itemView.findViewById(R.id.info);

            imgInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // show info dialog
                }
            });

            imageDrawable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref = itemView.getContext().getSharedPreferences("txtURI", 0);
                    int ind = pref.getInt("ind", 0);
                    Uri uri = Uri.parse(pref.getString("uri" + String.valueOf(ind - 1 - getAdapterPosition()), null));
                    Log.d(TAG, "uri:: " + uri);

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "image/*");
                    itemView.getContext().startActivity(intent);
                }
            });

            txtShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref = itemView.getContext().getSharedPreferences("txtURI", 0);
                    int ind = pref.getInt("ind", 0);
                    String temp = pref.getString("path" + String.valueOf(ind - 1 - getAdapterPosition()), null);
                    String temp1 = "";
                    temp += ".txt";
                    Uri uri = Uri.parse(temp);
                    Log.d(TAG, "uri:: " + uri);
//                    for (int i = 7; i < temp.length(); i++) {
//                        temp1 += temp.charAt(i);
//                    }
//                    Uri uri = Uri.parse(temp1);
//                    Log.d(TAG, "uri:: " + uri);

                    String data = readFromFile(view.getContext(), String.valueOf(uri));
                    Log.d(TAG, "data: " + data);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);

                    shareIntent.setType("text/html");
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, ("https://play.google.com/store/apps/details?id=datapole.ocrtext" + "\n"));   // instead send the description here

                    shareIntent.putExtra(Intent.EXTRA_TEXT, data + "\n\n" + "Download the app at https://play.google.com/store/apps/details?id=datapole.ocrtext");
                    view.getContext().startActivity(Intent.createChooser(shareIntent, "Share scanned text"));

                    // TO SHARE THE FILE ONCE YOU CAN ACTUALLY SAVE IT LOL
                }
            });

            Log.d(TAG, "dataObjHolderALLCARDS");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            SharedPreferences pref = v.getContext().getSharedPreferences("txtURI", 0);
            Log.d(TAG, "path:: " + pref.getString("path" + String.valueOf(getAdapterPosition()), "0"));

            int ind = pref.getInt("ind", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(pref.getString("path" + String.valueOf(ind - 1 - getAdapterPosition()), null));
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