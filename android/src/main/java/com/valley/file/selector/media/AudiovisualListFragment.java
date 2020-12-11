package com.valley.file.selector.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.valley.file.selector.ChooserConfig;
import com.valley.file.selector.capacitorpluginfileselector.R;

public class AudiovisualListFragment extends Fragment {

    private static final String TAG = "AudiovisualListFragment";

    private View root;

    public FileListCursorAdapter mAdapter;

    public ChooserConfig mChooserConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.audio_list, container, false);
        ListView fileListView = root.findViewById(R.id.file_list);
        fileListView.setAdapter(mAdapter);
//        mAdapter.changeCursor(audioCursor);
//        mFileInteractionHelper.setCursor(mAdapter);
        showEmptyView(mAdapter.getCount() == 0);
        return root;
    }

    private void showEmptyView(boolean show) {
        View emptyView = root.findViewById(R.id.empty_view);
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
