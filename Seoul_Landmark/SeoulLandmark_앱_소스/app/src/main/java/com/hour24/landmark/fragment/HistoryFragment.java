package com.hour24.landmark.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.model.HistoryModel;
import com.hour24.landmark.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends BaseFragment implements View.OnClickListener {

    private MainActivity activity;

    private RelativeLayout rlOkData;
    private RelativeLayout rlNoData;
    private RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;

    private List<HistoryModel> historys;

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        initLayout(view);
        initVariable();
        eventListener();
    }

    private void initLayout(View view) {
        rlOkData = (RelativeLayout) view.findViewById(R.id.rl_ok_data);
        rlNoData = (RelativeLayout) view.findViewById(R.id.rl_no_data);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    private void initVariable() {

        try {

            historys = new ArrayList<>();

            SharedPreferences pref = activity.getSharedPreferences(MainConst.LANDMARK_HISTORY, MODE_PRIVATE);
            String data = pref.getString(MainConst.LANDMARK_HISTORY_DATA_KEY, "");
            if ("".equals(data)) {
                rlOkData.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
            } else {
                rlOkData.setVisibility(View.VISIBLE);
                rlNoData.setVisibility(View.GONE);

                JSONArray jsonArray = new JSONArray(data);

                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    HistoryModel model = new HistoryModel();
                    model.setTitle(jsonObject.getString("title"));
                    model.setImage(Utils.setStringToBitmap(jsonObject.getString("image")));
                    model.setDate(Utils.getLongToStringFormat(jsonObject.getLong("date"), "yyyy.MM.dd"));
                    historys.add(model);
                }


                adapter = new RecyclerViewAdapter(historys);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));

                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eventListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<HistoryModel> records;

        public RecyclerViewAdapter(List<HistoryModel> records) {
            this.records = records;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {
            final HistoryModel record = records.get(position);

            holder.image.setImageBitmap(record.getImage());
            holder.title.setText(record.getTitle());
            holder.date.setText(record.getDate());

            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 재 검색
                    // MainActivity > PictureFragment
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.callVisionApi(MainConst.REQ_RE_SEARCH_HISTORY, record.getImage());
                            getFragmentManager().popBackStack();
                        }
                    });
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout main;
            ImageView image;
            TextView title;
            TextView date;

            public ViewHolder(View itemView) {
                super(itemView);
                main = (LinearLayout) itemView.findViewById(R.id.ll_main);
                image = (ImageView) itemView.findViewById(R.id.iv_image);
                title = (TextView) itemView.findViewById(R.id.tv_title);
                date = (TextView) itemView.findViewById(R.id.tv_date);
            }
        }
    }
}
