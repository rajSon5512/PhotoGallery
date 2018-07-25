package com.knoxpo.rajivsonawala.photogalleryupdate;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();


    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
//        new FetchItemsTask().execute();
        updateItems();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));



        Log.i(TAG, "onCreateView: Your oncreate View ");


        setupAdapter();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_items,menu);

        MenuItem menuItem=menu.findItem(R.id.menu_item_search);
        final SearchView searchView=(SearchView)menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "onQueryTextSubmit: "+query);
                QueryPreferences.setStoredQuery(getActivity(),query);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                Log.d(TAG, "onQueryTextChange: "+query);
               return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query=QueryPreferences.getStrotedQuery(getActivity());
                searchView.setQuery(query,false);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menu_item_cleaner:
                    QueryPreferences.setStoredQuery(getActivity(),null);
                    updateItems();
                    return true;

                    default:
                        return super.onOptionsItemSelected(item);



        }


    }

    private void updateItems() {
        String query=QueryPreferences.getStrotedQuery(getActivity());
        new FetchItemsTask(query).execute();

    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }


    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.item_image_view);
        }


        public void bindDrawable(Drawable drawable) {

            mImageView.setImageDrawable(drawable);

        }

        public void bindGalleryItem(GalleryItem item) {
            GlideApp.with(getActivity())
                    .load(item.getUrl())
                    .placeholder(R.drawable.ic_image)
                    .dontAnimate()
                    .into(mImageView);

            /*Glide.with(getActivity())
                    .load(item.getUrl())
                    .into(mImageView);*/
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            Log.d(TAG, mGalleryItems.get(position).getUrl());
         //   Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            //photoHolder.bindDrawable(placeholder);
            photoHolder.bindGalleryItem(mGalleryItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private String mQuery;

        public FetchItemsTask(String query){

            mQuery=query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {


            if(mQuery==null){

                return new FlickrFetchr().fetchRecentPhotos();

            }else {

                return new FlickrFetchr().searchPhotos(mQuery);
            }


        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
