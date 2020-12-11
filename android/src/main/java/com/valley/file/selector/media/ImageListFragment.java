package com.valley.file.selector.media;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.valley.file.selector.ChooserConfig;
import com.valley.file.selector.capacitorpluginfileselector.R;
import com.valley.file.selector.media.preview.ZoomMediaLoader;

public class ImageListFragment extends Fragment {

    private static final String TAG = "ImageListFragment";

    private View root;

    private Context mContext;

    public FileListCursorAdapter mAdapter;

    public ChooserConfig mChooserConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZoomMediaLoader.getInstance().init(new ImageZoomLoader());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.image_list, container, false);
        mContext = getContext();
        GridView fileListView = root.findViewById(R.id.file_list);
        fileListView.setAdapter(mAdapter);
//        mAdapter.changeCursor(imageCursor);
//        mFileInteractionHelper.setCursor(mAdapter);
        showEmptyView(mAdapter.getCount() == 0);
        return root;
    }

    private void showEmptyView(boolean show) {
        View emptyView = root.findViewById(R.id.image_empty_view);
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
