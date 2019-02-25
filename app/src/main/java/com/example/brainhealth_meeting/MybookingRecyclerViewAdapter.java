package com.example.brainhealth_meeting;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.brainhealth_meeting.bookingFragment.OnListFragmentInteractionListener;
import com.example.brainhealth_meeting.SimpleProfileContent.SimpleProfileItem;

import java.io.InputStream;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SimpleProfileItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MybookingRecyclerViewAdapter extends RecyclerView.Adapter<MybookingRecyclerViewAdapter.ViewHolder> {

    private final List<SimpleProfileItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MybookingRecyclerViewAdapter(List<SimpleProfileItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (holder.mNameView != null)
            holder.mNameView.setText(mValues.get(position).user_name);
        if (holder.mAgeView != null)
            holder.mAgeView.setText(String.valueOf(mValues.get(position).user_age));
        if (holder.mPhotoView != null)
            holder.mPhotoView.setImageBitmap(mValues.get(position).user_photo);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mAgeView;
        public final ImageView mPhotoView;
        public SimpleProfileItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.simple_profile_name);
            mAgeView = view.findViewById(R.id.simple_profile_age);
            mPhotoView = view.findViewById(R.id.simple_profile_photo);
        }

    }
}
