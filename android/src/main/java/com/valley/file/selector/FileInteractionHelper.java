package com.valley.file.selector;

import android.content.Context;
import android.view.View;

import com.valley.file.selector.media.FileListCursorAdapter;

import java.util.ArrayList;

public class FileInteractionHelper {
    private static FileInteractionHelper instance = new FileInteractionHelper();

    private Context context;

    private FileListCursorAdapter cursor;

    private static ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<>();

    private FileInteractionHelper () {

    }

    public static FileInteractionHelper getInstance() {
        return instance;
    }

    public FileListCursorAdapter getCursor() {
        return cursor;
    }

    public void setCursor(FileListCursorAdapter cursor) {
        this.cursor = cursor;
    }

    public ArrayList<FileInfo> getSelectedFileList() {
        return mCheckedFileNameList;
    }

    public boolean onCheckItem(FileInfo f, View v) {
        if (f.Selected) {
            mCheckedFileNameList.add(f);
        } else {
            mCheckedFileNameList.remove(f);
        }
        return true;
    }

    public void clearSelection() {
        if (mCheckedFileNameList.size() > 0) {
            for (FileInfo f : mCheckedFileNameList) {
                if (f == null) {
                    continue;
                }
                f.Selected = false;
            }
            mCheckedFileNameList.clear();
        }
    }

    public void onOperationSelectAll() {
        mCheckedFileNameList.clear();
        for (FileInfo f : cursor.getAllFiles()) {
            f.Selected = true;
            mCheckedFileNameList.add(f);
        }
    }

    public boolean isSelectedAll() {
        return cursor.getCount() != 0 && mCheckedFileNameList.size() == cursor.getCount();
    }
}
