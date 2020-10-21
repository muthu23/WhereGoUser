package com.wherego.delivery.user.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wherego.delivery.user.Models.Locations;
import com.wherego.delivery.user.R;

import java.util.ArrayList;
import java.util.Random;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
    private int lastPosition = -1;

    private static final String TAG = "LocationsAdapter";
    boolean[] selectedService;
    android.app.AlertDialog confirmation_dialogue;
    private ArrayList<Locations> listModels;
    private Context context;
    private LocationsListener locationsListener;
    char alphabet = 'A';

    public LocationsAdapter(ArrayList<Locations> listModel, Context context) {
        this.listModels = listModel;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locations_list_item, parent, false);
        return new ViewHolder(v);
    }

    public void setLocationsListener(LocationsListener locationsListener) {
        this.locationsListener = locationsListener;
    }

    public void setListModels(ArrayList<Locations> listModels) {
        this.listModels = listModels;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Locations locations = listModels.get(position);

        char upper = (char) ('A' + position);
        holder.lettersTxt.setText("" + upper);

        if (position == 0) {
            holder.closeImg.setVisibility(View.INVISIBLE);
            holder.lnr_source.setVisibility(View.VISIBLE);
            holder.srcTxt.setText(listModels.get(0).getsAddress());
        } else {
            holder.lnr_source.setVisibility(View.GONE);
            holder.closeImg.setVisibility(View.VISIBLE);
            holder.srcTxt.setClickable(false);
        }


        if (locations.getdAddress() != null) {
            holder.destTxt.setText(locations.getdAddress());
        } else {
            holder.destTxt.setText("");
        }

        if (locations.getDescription() != null) {
            holder.goodTxt.setText(locations.getDescription());
        } else {
            holder.goodTxt.setText("");
        }
     //   setAnimation(holder.itemView, position);
        holder.destTxt.setTag(locations);
        holder.srcTxt.setTag(locations);
        holder.goodTxt.setTag(locations);
        holder.closeImg.setTag(locations);

    }
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }
    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public void showGoodsDialog(final Locations locations) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getResources().getString(R.string.app_name));
        alert.setIcon(R.mipmap.ic_launcher);

        View custom = LayoutInflater.from(context).inflate(R.layout.custom_edit, null);
        final EditText input = custom.findViewById(R.id.desc);
        String goods = locations.getDescription();
        if (goods != null && !goods.equalsIgnoreCase("null") && goods.length() > 0) {
            input.setText(goods);
        }
        alert.setView(custom);
        alert.setPositiveButton(context.getResources().getString(R.string.ok), (dialog, whichButton) -> {
            if (input.getText().length() > 0) {
                locations.setDescription(input.getText().toString());
                locationsListener.onGoodsClick(locations);
            }
            dialog.cancel();

        });
        alert.setNegativeButton(context.getResources().getString(R.string.cancel), (dialog, whichButton) ->
                dialog.cancel());

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

        Button buttonbackground = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonbackground.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

        Button buttonbackground1 = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonbackground1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    public interface LocationsListener {
        void onCloseClick(Locations locations);

        void onSrcClick(Locations locations);

        void onDestClick(Locations locations);

        void onGoodsClick(Locations locations);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView srcTxt, destTxt, goodTxt, lettersTxt;
        ImageView closeImg;
        LinearLayout lnr_source;

        public ViewHolder(View itemView) {
            super(itemView);
            srcTxt = itemView.findViewById(R.id.src_txt);
            destTxt = itemView.findViewById(R.id.dest_txt);
            goodTxt = itemView.findViewById(R.id.good_txt);
            lettersTxt = itemView.findViewById(R.id.letters_txt);
            closeImg = itemView.findViewById(R.id.close_img);
            lnr_source = itemView.findViewById(R.id.lnr_source);

            srcTxt.setOnClickListener(this);
            destTxt.setOnClickListener(this);
            goodTxt.setOnClickListener(this);
            closeImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (locationsListener != null) {
                final Locations locations = (Locations) v.getTag();
                if (v == closeImg) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.app_name))
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(context.getResources().getString(R.string.alert));
                    builder.setCancelable(false);

                    builder.setPositiveButton(context.getResources().getString(R.string.yes), (dialog, which) ->
                            locationsListener.onCloseClick(locations));

                    builder.setNegativeButton(context.getResources().getString(R.string.no), (dialog, which) ->
                            dialog.dismiss());

                    confirmation_dialogue = builder.create();
                    confirmation_dialogue.show();

                    Button buttonbackground = confirmation_dialogue.getButton(DialogInterface.BUTTON_NEGATIVE);
                    buttonbackground.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    buttonbackground.setBackground(null);

                    Button buttonbackground1 = confirmation_dialogue.getButton(DialogInterface.BUTTON_POSITIVE);
                    buttonbackground1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    buttonbackground1.setBackground(null);

                } else if (v == srcTxt) {
                    locationsListener.onSrcClick(locations);
                } else if (v == destTxt) {
                    locationsListener.onDestClick(locations);
                } else if (v == goodTxt) {
                    showGoodsDialog(locations);
                }
            }
        }
    }
}
