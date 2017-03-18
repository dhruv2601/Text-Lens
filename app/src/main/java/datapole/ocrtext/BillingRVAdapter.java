package datapole.ocrtext;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dhruv on 3/1/17.
 */

/* Copyright: You can use the code as you want, just let me know about it :).
*
*  email: dhruvrathi15@gmail.com
*
*/

public class BillingRVAdapter extends RecyclerView
        .Adapter<BillingRVAdapter
        .DataObjectHolder> {

    public static final String TAG = "myRecViewAdapter";
    private ArrayList<String> mCardSet;
    private static BillingRVAdapter.MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView txtNumCards;
        AppCompatTextView btnCost;

        public DataObjectHolder(View itemView) {
            super(itemView);

            txtNumCards = (TextView) itemView.findViewById(R.id.txt_num_cards);
            btnCost = (AppCompatTextView) itemView.findViewById(R.id.card_rates);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(BillingRVAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public BillingRVAdapter(ArrayList<String> myCardSet) {
        mCardSet = myCardSet;
        this.context = context;
    }

    @Override
    public BillingRVAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_billing, parent, false);

        BillingRVAdapter.DataObjectHolder dataObjectHolder = new BillingRVAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(BillingRVAdapter.DataObjectHolder holder, int position) {
        if (position == 0) {
            holder.txtNumCards.setText("100 Cards");
            holder.btnCost.setText(mCardSet.get(0));
        } else if (position == 1) {
            holder.txtNumCards.setText("250 Cards");
            holder.btnCost.setText(mCardSet.get(1));
        } else if (position == 2) {
            holder.txtNumCards.setText("400 Cards");
            holder.btnCost.setText(mCardSet.get(2));
        } else if (position == 3) {
            holder.txtNumCards.setText("500 Cards");
            holder.btnCost.setText(mCardSet.get(3));
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}