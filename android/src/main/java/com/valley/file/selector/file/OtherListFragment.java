package com.valley.file.selector.file;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.valley.file.selector.ChooserConfig;
import com.valley.file.selector.FileInfo;
import com.valley.file.selector.FileInteractionHelper;
import com.valley.file.selector.Util;
import com.valley.file.selector.capacitorpluginfileselector.R;

import java.util.ArrayList;

public class OtherListFragment extends Fragment {
    private static final String TAG = "FileListFragment";

    public FileInteractionHelper mFileInteractionHelper;

    private View root;

    public ArrayList<FileInfo> otherList = new ArrayList<>();

    public ChooserConfig mChooserConfig;

    public FileAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.file_list, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FileAdapter(getContext(), mChooserConfig, otherList, mFileInteractionHelper);
        recyclerView.setAdapter(adapter);
        new android.os.Handler().post(() -> {
            otherList = Util.initDocuments("other");
            adapter.fileList = otherList;
            showEmptyView(otherList.size() == 0, root);
            adapter.notifyDataSetChanged();
        });
        showEmptyView(otherList.size() == 0, root);
        return root;
    }

    private void showEmptyView(boolean show, View root) {
        View emptyView = root.findViewById(R.id.file_empty_view);
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /*public void initDocuments() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        otherList.clear();
        getFiles(externalStorageDirectory);
        Collections.sort(otherList, new SortByDate());
    }

    public void getFiles(File dir) {
        File fileList[] = dir.listFiles();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    getFiles(fileList[i]);
                } else {
                    if (Util.otherTypeFilter(fileList[i])) {
                        otherList.add(Util.GetFileInfo(fileList[i].getPath()));
                    }
                }
            }
        }
    }*/
}

