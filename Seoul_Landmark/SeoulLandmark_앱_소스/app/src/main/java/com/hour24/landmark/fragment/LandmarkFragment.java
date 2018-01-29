package com.hour24.landmark.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.WebEntity;
import com.google.api.services.vision.v1.model.WebImage;
import com.google.api.services.vision.v1.model.WebPage;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.model.VisionModel;
import com.hour24.landmark.util.Utils;
import com.hour24.landmark.viewholder.ViewHolderEmpty;
import com.hour24.landmark.viewholder.ViewHolderFooter;
import com.hour24.landmark.viewholder.ViewHolderHeader;
import com.hour24.landmark.viewholder.ViewHolderImageMatch;
import com.hour24.landmark.viewholder.ViewHolderLandmark;
import com.hour24.landmark.viewholder.ViewHolderImage;
import com.hour24.landmark.viewholder.ViewHolderVisitSeoulNet;
import com.hour24.landmark.viewholder.ViewHolderWebEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LandmarkFragment extends Fragment implements View.OnClickListener {

    public interface GetLandMarkDataCallBack {
        public void getLandMarkData(BatchAnnotateImagesResponse response);
    }

    private MainActivity activity;

    private RelativeLayout rlOkData;
    private RelativeLayout rlNoData;
    private RecyclerView recyclerView;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton fabHistory;

    private RecyclerViewAdapter adapter;

    private List<EntityAnnotation> landmarkAnnotations;
    private List<WebImage> fullMatchingImages;
    private List<WebImage> visuallySimilarImages;
    private List<WebPage> pagesWithMatchingImages;
    private List<WebEntity> webEntities;
    private List<VisionModel> visions;

    public LandmarkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_land_mark, container, false);
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
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        fabHistory = (FloatingActionButton) view.findViewById(R.id.fab_history);

        View[] views = {fabHistory};
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    private void initVariable() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_history:
                HistoryFragment fragment = new HistoryFragment();
                Utils.replaceFragment(activity.getSupportFragmentManager(), fragment);
                fabMenu.collapseImmediately();
                break;
        }
    }

    // 데이터 유무에 따른 레이아웃 처리
    public void setVisibleDataLayout(boolean isVisible) {
        if (rlOkData != null && rlNoData != null) {
            if (isVisible) {
                rlOkData.setVisibility(View.VISIBLE);
                rlNoData.setVisibility(View.GONE);
            } else {
                rlOkData.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    private void eventListener() {
        activity.getLandMarkData(new GetLandMarkDataCallBack() {
            @Override
            public void getLandMarkData(BatchAnnotateImagesResponse response) {

                landmarkAnnotations = response.getResponses().get(0).getLandmarkAnnotations();
                pagesWithMatchingImages = response.getResponses().get(0).getWebDetection().getPagesWithMatchingImages();
                webEntities = response.getResponses().get(0).getWebDetection().getWebEntities();
                fullMatchingImages = response.getResponses().get(0).getWebDetection().getFullMatchingImages();
                visuallySimilarImages = response.getResponses().get(0).getWebDetection().getVisuallySimilarImages();

                visions = new ArrayList<>();

                // viewType 가공
                if (landmarkAnnotations != null && landmarkAnnotations.size() > 0) {
                    visions.add(setHeader(getString(R.string.recognize_item_header_land_mark)));
                    EntityAnnotation data = landmarkAnnotations.get(0);
                    VisionModel model = new VisionModel();
                    model.setViewType(MainConst.VIEW_TYPE_LAND_MARK);
                    model.setLandmarkAnnotation(data);
                    visions.add(model);
                    visions.add(setFooter());
                }

                // visit seoul net
                {
                    if (landmarkAnnotations != null && landmarkAnnotations.size() > 0) {
                        visions.add(setHeader(getString(R.string.recognize_item_header_visit_seoul_net)));
                        EntityAnnotation data = landmarkAnnotations.get(0);
                        VisionModel model = new VisionModel();
                        model.setViewType(MainConst.VIEW_TYPE_VISIT_SEOUL_NET);
                        model.setLandmarkAnnotation(data);
                        visions.add(model);
                        visions.add(setFooter());
                    }
                }

                if (pagesWithMatchingImages != null && pagesWithMatchingImages.size() > 0) {
                    visions.add(setHeader(getString(R.string.recognize_item_header_image_match)));

                    for (int i = 0; i < pagesWithMatchingImages.size(); i++) {
                        WebPage data = pagesWithMatchingImages.get(i);

                        VisionModel model = new VisionModel();
                        model.setViewType(MainConst.VIEW_TYPE_IMAGE_MATCH);
                        model.setPagesWithMatchingImage(data);
                        visions.add(model);

                        // 크롤링 데이터를 미리 만든다.
                        setCrawling(visions.size() - 1, data, (i == (pagesWithMatchingImages.size() - 1)));

                    }
                    visions.add(setFooter());
                }

                if (webEntities != null && webEntities.size() > 0) {
                    visions.add(setHeader(getString(R.string.recognize_item_header_web_search)));
                    for (WebEntity data : webEntities) {
                        VisionModel model = new VisionModel();
                        model.setViewType(MainConst.VIEW_TYPE_WEB_ENTITY);
                        model.setWebEntity(data);
                        visions.add(model);
                    }
                    visions.add(setFooter());
                }

                if (fullMatchingImages != null && fullMatchingImages.size() > 0) {
                    visions.add(setHeader(getString(R.string.recognize_item_header_match_image)));
                    VisionModel model = new VisionModel();
                    model.setViewType(MainConst.VIEW_TYPE_MATCH_IMAGE);
                    model.setFullMatchingImages(fullMatchingImages);
                    visions.add(model);
                    visions.add(setFooter());
                }

                if (visuallySimilarImages != null && visuallySimilarImages.size() > 0) {
                    visions.add(setHeader(getString(R.string.recognize_item_header_similar_image)));
                    VisionModel model = new VisionModel();
                    model.setViewType(MainConst.VIEW_TYPE_SIMILAR_IMAGE);
                    model.setFullMatchingImages(visuallySimilarImages);
                    visions.add(model);
                    visions.add(setFooter());
                }

                adapter = new RecyclerViewAdapter(visions);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));

                adapter.notifyDataSetChanged();
            }
        });
    }

    public VisionModel setHeader(String title) {
        VisionModel model = new VisionModel();
        model.setViewType(MainConst.VIEW_TYPE_HEADER);
        model.setHeader(title);
        return model;
    }

    public VisionModel setFooter() {
        VisionModel model = new VisionModel();
        model.setViewType(MainConst.VIEW_TYPE_FOOTER);
        return model;
    }

    private void setCrawling(int i, final WebPage data, boolean isFinal) {
        new AsyncTask<String, Void, VisionModel>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected VisionModel doInBackground(String... params) {

                String position = params[0];
                String url = params[1];
                boolean isFinal = Boolean.parseBoolean(params[2]);

                VisionModel data = new VisionModel();
                data.setPageUrl(url);
                data.setViewType(MainConst.VIEW_TYPE_IMAGE_MATCH);
                data.setCrawling(true);

                try {
                    data.setPosition(Integer.parseInt(position));

                    Document doc = Jsoup.connect(url).get();
                    // title
                    String title = doc.title();
                    if (title != null || title.length() > 1) {
                        data.setHeadTitle(doc.title());
                    }

                    // favicon
                    Element faviconFirst = doc.head().select("link[href~=.*\\.ico]").first();
                    if (faviconFirst != null) {
                        String favicon = faviconFirst.attr("href");
                        data.setHeadFavicon(favicon);
                    }

                    // og:title
                    Elements ogTitle = doc.select("meta[property=og:title]");
                    if (ogTitle != null) {
                        data.setOgTitle(ogTitle.attr("content"));
                    }

                    // og:description
                    Elements ogDescription = doc.select("meta[property=og:description]");
                    if (ogDescription != null) {
                        data.setOgDescription(ogDescription.attr("content"));
                    }

                    // og:image
                    Elements ogImage = doc.select("meta[property=og:image]");
                    if (ogImage != null) {
                        data.setOgImage(ogImage.attr("content"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(VisionModel result) {
                visions.set(result.getPosition(), result);
                adapter.notifyDataSetChanged();
            }
        }.execute(String.valueOf(i), data.getUrl(), String.valueOf(isFinal));
    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<VisionModel> records;

        public RecyclerViewAdapter(List<VisionModel> records) {
            this.records = records;
        }

        @Override
        public int getItemViewType(int position) {
            return records.get(position).getViewType();
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int resId = 0;
            switch (viewType) {
                case MainConst.VIEW_TYPE_HEADER:
                    resId = R.layout.recognize_common_item_header;
                    break;
                case MainConst.VIEW_TYPE_FOOTER:
                    resId = R.layout.recognize_common_item_footer;
                    break;
                case MainConst.VIEW_TYPE_LAND_MARK:
                    resId = R.layout.recognize_item_land_mark;
                    break;
                case MainConst.VIEW_TYPE_VISIT_SEOUL_NET:
                    resId = R.layout.recognize_item_visit_seoul_net;
                    break;
                case MainConst.VIEW_TYPE_IMAGE_MATCH:
                    resId = R.layout.recognize_item_page_match;
                    break;
                case MainConst.VIEW_TYPE_WEB_ENTITY:
                    resId = R.layout.recognize_item_web_entity;
                    break;
                case MainConst.VIEW_TYPE_MATCH_IMAGE:
                    resId = R.layout.recognize_item_image;
                    break;
                case MainConst.VIEW_TYPE_SIMILAR_IMAGE:
                    resId = R.layout.recognize_item_image;
                    break;
                default:
                    resId = R.layout.recognize_item_empty;
                    break;
            }
            return new RecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            VisionModel record = records.get(position);

            switch (record.getViewType()) {
                case MainConst.VIEW_TYPE_HEADER:
                    holder.viewHolderHeader.bind(position, record);
                    break;
                case MainConst.VIEW_TYPE_FOOTER:
                    holder.viewHolderFooter.bind(position, record);
                    break;
                case MainConst.VIEW_TYPE_LAND_MARK:
                    holder.viewHolderLandmark.bind(position, record);
                    break;
                case MainConst.VIEW_TYPE_VISIT_SEOUL_NET:
                    holder.viewHolderVisitSeoulNet.bind(position, record);
                    break;
                case MainConst.VIEW_TYPE_IMAGE_MATCH:
                    holder.viewHolderImageMatch.bind(position, record);
                    break;
                case MainConst.VIEW_TYPE_WEB_ENTITY:
                    holder.viewHolderWebEntity.bind(position, record);
                    break;
                case MainConst.VIEW_TYPE_MATCH_IMAGE:
                    holder.viewHolderImage.bind(position, record, MainConst.VIEW_TYPE_MATCH_IMAGE);
                    break;
                case MainConst.VIEW_TYPE_SIMILAR_IMAGE:
                    holder.viewHolderImage.bind(position, record, MainConst.VIEW_TYPE_MATCH_IMAGE);
                    break;
                default:
                    holder.viewHolderEmpty.bind();
                    break;
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            ViewHolderHeader viewHolderHeader;
            ViewHolderFooter viewHolderFooter;

            ViewHolderLandmark viewHolderLandmark;
            ViewHolderVisitSeoulNet viewHolderVisitSeoulNet;
            ViewHolderImageMatch viewHolderImageMatch;
            ViewHolderWebEntity viewHolderWebEntity;
            ViewHolderImage viewHolderImage;

            ViewHolderEmpty viewHolderEmpty;

            public ViewHolder(View itemView) {
                super(itemView);
                viewHolderHeader = new ViewHolderHeader(itemView);
                viewHolderFooter = new ViewHolderFooter(itemView);

                viewHolderLandmark = new ViewHolderLandmark(activity, itemView, getActivity().getSupportFragmentManager());
                viewHolderVisitSeoulNet = new ViewHolderVisitSeoulNet(activity, itemView, getActivity().getSupportFragmentManager());
                viewHolderImageMatch = new ViewHolderImageMatch(activity, itemView, getActivity().getSupportFragmentManager());
                viewHolderWebEntity = new ViewHolderWebEntity(activity, itemView, getActivity().getSupportFragmentManager());
                viewHolderImage = new ViewHolderImage(activity, itemView, getActivity().getSupportFragmentManager());

                viewHolderEmpty = new ViewHolderEmpty();
            }
        }
    }
}
