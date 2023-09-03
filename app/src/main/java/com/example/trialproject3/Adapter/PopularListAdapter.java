package com.example.trialproject3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.trialproject3.Activity.DetailActivity;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.R;

import java.util.ArrayList;

public class PopularListAdapter extends RecyclerView.Adapter<PopularListAdapter.Viewholder> {
    ArrayList<PopularDomain> items;
    Context context;

    public PopularListAdapter(ArrayList<PopularDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PopularListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pop_list,parent,false);
        context=parent.getContext();
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularListAdapter.Viewholder holder, int position) {holder.titleTxt.setText(items.get(position).getTitle());
    holder.feeTxt.setText("₱"+items.get(position).getPrice());
    holder.scoreTxt.setText(""+items.get(position).getScore());

        
    int drawableResourceId=holder.itemView.getResources().getIdentifier(items.get(position).getPicUrl(),
            "drawable",holder.itemView.getContext().getPackageName());

    Glide.with(holder.itemView.getContext())
            .load(drawableResourceId)
            .transform(new GranularRoundedCorners(36,36,0,0))
            .into(holder.pic);

    holder.itemView.setOnClickListener(v -> {
        Intent intent=new Intent(holder.itemView.getContext(), DetailActivity.class);
        intent.putExtra("object", items.get(position));
        holder.itemView.getContext().startActivity(intent);
    });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt,feeTxt,scoreTxt;
        ImageView pic;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxt=itemView.findViewById(R.id.titleTxt2);
            feeTxt=itemView.findViewById(R.id.feesTxt);
            scoreTxt=itemView.findViewById(R.id.score1Txt);
            pic=itemView.findViewById(R.id.pic1);
        }
    }
}
