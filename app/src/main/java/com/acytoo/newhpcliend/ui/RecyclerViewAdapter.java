package com.acytoo.newhpcliend.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acytoo.newhpcliend.MyApplication;
import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.utils.ImageSaver;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //[id]#[priority]#[date]#[plan]#[source]#[done]#_&
    private Context mContext;
    private ArrayList<String> mPlan;
    private ArrayList<String> mImages;
    private ArrayList<Integer> mID;
    private ArrayList<String> mDate;
    private ArrayList<String> mSource;
    private ArrayList<Integer> mDone;



    public RecyclerViewAdapter(Context mContext, ArrayList<String> mPlan, ArrayList<String> mImages,
                               ArrayList<Integer> mID, ArrayList<String> mDate, ArrayList<String> mSource, ArrayList<Integer> mDone) {
        this.mContext = mContext;
        this.mPlan = mPlan;
        this.mImages = mImages;
        this.mID = mID;
        this.mDate = mDate;
        this.mSource = mSource;
        this.mDone = mDone;
        Log.d("mipmap", "RecyclerView constructor");
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        Log.d("mipmap", "on create RecyclerView");
        return new ViewHolder(view);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("mipmap", "on bind");

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);
        holder.txt_plan_detail.setText(mPlan.get(position));
        holder.txt_source_detail.setText(mSource.get(position));
        holder.txt_date_detail.setText(mDate.get(position));
        try {
            int resID = MyApplication.getInstance().getResources()
                    .getIdentifier("check_" + mDone.get(position), "mipmap",
                            MyApplication.getInstance().getPackageName());
            Log.d("mipmap", "the id is " + resID + " the done is " + mDone.get(position));
            holder.img_done_detail.setImageResource(resID);
        } catch (Exception e) {
            Log.d("mipmap", e.toString());
        }



        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mID.get(position));
                Intent editActivity = new Intent(mContext, EditPlanActivity.class);
                editActivity.putExtra("id", mID.get(position));
                mContext.startActivity(editActivity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPlan.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView txt_plan_detail;
        TextView txt_date_detail;
        TextView txt_source_detail;
        ImageView img_done_detail;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            txt_plan_detail = itemView.findViewById(R.id.txt_plan_detail);
            txt_date_detail = itemView.findViewById(R.id.txt_date_detail);
            txt_source_detail = itemView.findViewById(R.id.txt_source_detail);
            img_done_detail = itemView.findViewById(R.id.img_done_detail);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}













