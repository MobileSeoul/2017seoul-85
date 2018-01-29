package com.hour24.landmark.viewholder;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.api.services.vision.v1.model.WebImage;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.fragment.PhotoViewFragment;
import com.hour24.landmark.model.VisionModel;
import com.hour24.landmark.util.Utils;
import com.hour24.landmark.util.view.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ViewHolderImage {

    private Activity activity;
    private View view;
    private FragmentManager fragmentManager;

    private RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;

    private int width;
    private List<WebImage> records;

    public ViewHolderImage(MainActivity activity, View view, FragmentManager fragmentManager) {
        this.activity = activity;
        this.view = view;
        this.fragmentManager = fragmentManager;
        initLayout();
        initVariable();
    }

    private void initLayout() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    private void initVariable() {
        width = (int) Utils.getDPfromPX(activity, 130);
    }

    public void bind(int position, VisionModel record, int viewType) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        if (viewType == MainConst.VIEW_TYPE_MATCH_IMAGE) {
            records = record.getFullMatchingImages();
        } else {
            records = record.getVisuallySimilarImage();
        }
        adapter.notifyDataSetChanged();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        public RecyclerViewAdapter() {
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recognize_item_image_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {
            WebImage record = records.get(position);
            Picasso.with(activity).load(record.getUrl()).centerCrop().resize(width, width).transform(new CircleTransform()).into(holder.imageVIew);

            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PhotoViewFragment fragment = new PhotoViewFragment();
                            fragment.setRecords(records, position);
                            Utils.replaceFragment(fragmentManager, fragment);
                        }
                    }, 500);

                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public RelativeLayout main;
            public ImageView imageVIew;

            public ViewHolder(View itemView) {
                super(itemView);
                main = (RelativeLayout) itemView.findViewById(R.id.main);
                imageVIew = (ImageView) itemView.findViewById(R.id.image_view);
            }
        }
    }
}
