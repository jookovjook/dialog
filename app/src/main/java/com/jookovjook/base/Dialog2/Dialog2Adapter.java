package com.jookovjook.base.Dialog2;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jookovjook.base.R;

import java.util.List;

public class Dialog2Adapter extends RecyclerView.Adapter<Dialog2Adapter.MyViewHolder> {

    private List<Dialog2Provider> moviesList;

    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dialog_card_text;
        public RelativeLayout dialog_card;
        public ImageButton btn;

        public MyViewHolder(View view) {
            super(view);
            dialog_card_text = (TextView) view.findViewById(R.id.dialog_card_text);
            dialog_card = (RelativeLayout) view.findViewById(R.id.dialog_card);
            btn = (ImageButton) view.findViewById(R.id.card_button);
        }
    }


    public Dialog2Adapter(List<Dialog2Provider> moviesList, Context context) {
        this.moviesList = moviesList;
        this.mContext = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog2_card_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Dialog2Provider dialog2Provider = moviesList.get(position);
        holder.dialog_card_text.setText(dialog2Provider.getText());
        int type = dialog2Provider.getType();

        if(position == 0) {
            if (type == 2) {
                holder.dialog_card.setGravity(Gravity.LEFT);
                holder.dialog_card.setPadding(dpToPx(16), 0, dpToPx(80), dpToPx(16));
            } else {
                holder.dialog_card.setGravity(Gravity.RIGHT);
                holder.dialog_card.setPadding(dpToPx(80), 0, dpToPx(16), dpToPx(16));
            }
        }else {

            if (type == 2) {
                holder.dialog_card.setGravity(Gravity.LEFT);
                holder.dialog_card.setPadding(dpToPx(16), 0, dpToPx(80), 0);
            } else {
                holder.dialog_card.setGravity(Gravity.RIGHT);
                holder.dialog_card.setPadding(dpToPx(80), 0, dpToPx(16), 0);
            }
        }
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContext instanceof Dialog2Activity){
                    showPopupMenu(view, position);
                }
            }
        });
        holder.dialog_card.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view) {
                final Dialog2Provider dialog2Provider = moviesList.get(position);
                ((Dialog2Activity)mContext).playMethod(dialog2Provider.getText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    private void showPopupMenu(View v, int pos) {
        final int pos2 = pos;
        PopupMenu popupMenu = new PopupMenu(this.mContext, v);
        popupMenu.inflate(R.menu.dialog2_card_popupmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final Dialog2Provider dialog2Provider = moviesList.get(pos2);
                switch (item.getItemId()) {
                    case R.id.menu1_copy:
                        ((Dialog2Activity)mContext).copyMethod(dialog2Provider.getText());
                        return true;
                    case R.id.menu2_add:
                        ((Dialog2Activity)mContext).addCardMethod(dialog2Provider.getText());
                        return true;
                    case R.id.menu3_del:
                        //show allert
                        ((Dialog2Activity)mContext).deleteMethod(dialog2Provider.get_id());
                        moviesList.remove(pos2);
                        notifyDataSetChanged();
                        return true;
                    case R.id.menu4_play:
                        ((Dialog2Activity)mContext).playMethod(dialog2Provider.getText());
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}