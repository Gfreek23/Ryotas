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
import com.example.trialproject3.Domain.CategoryDomain;
import com.example.trialproject3.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Viewholder2> {
    ArrayList<CategoryDomain> items2;
    Context context;

    public CategoryAdapter(ArrayList<CategoryDomain> items2) {
        this.items2 = items2;
    }

    @NonNull
    @Override
    public CategoryAdapter.Viewholder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pop_list,parent,false);
        context=parent.getContext();
        return new Viewholder2(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.Viewholder2 holder, int position) {
        holder.titleTxt.setText(items2.get(position).getTitle());

        int drawableResourceId=holder.itemView.getResources().getIdentifier(items2.get(position).getPicUrl(),
                "drawable",holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(36,36,0,0))
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent=new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("object", items2.get(position));
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return items2.size();
    }

    public class Viewholder2 extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;
        public Viewholder2(@NonNull View itemView) {
            super(itemView);

            titleTxt=itemView.findViewById(R.id.categ);
            pic=itemView.findViewById(R.id.pic1);
        }
    }
}
