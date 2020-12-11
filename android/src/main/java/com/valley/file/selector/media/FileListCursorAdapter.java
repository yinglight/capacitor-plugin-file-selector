package com.valley.file.selector.media;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import androidx.fragment.app.Fragment;

import com.valley.file.selector.ChooserConfig;
import com.valley.file.selector.FileInfo;
import com.valley.file.selector.FileInteractionHelper;
import com.valley.file.selector.Util;
import com.valley.file.selector.capacitorpluginfileselector.R;
import com.valley.file.selector.media.preview.GPreviewBuilder;
import com.valley.file.selector.media.preview.enitity.UserViewInfo;

import java.util.Collection;
import java.util.HashMap;

public class FileListCursorAdapter extends CursorAdapter {

    public static final int COLUMN_ID = 0;

    public static final int COLUMN_PATH = 1;

    public static final int COLUMN_SIZE = 2;

    public static final int COLUMN_DATE = 3;

    private final LayoutInflater mFactory;

    private FileIconHelper mFileIcon;

    private HashMap<Integer, FileInfo> mFileNameList = new HashMap<Integer, FileInfo>();

    private Context mContext;

    private FileInteractionHelper mFileInteractionHelper;

    private Util.Category mCategory;

    private Fragment mFragment;

    private ChooserConfig mChooserConfig;

    public FileListCursorAdapter(Fragment fragment, Context context, ChooserConfig chooserConfig, Cursor cursor,
                                 FileIconHelper fileIcon, FileInteractionHelper fileInteraction,
                                 Util.Category category) {
        super(context, cursor, false /* auto-requery */);
        mFactory = LayoutInflater.from(context);
        mFileIcon = fileIcon;
        mFileInteractionHelper = fileInteraction;
        mCategory = category;
        mFragment = fragment;
        mContext = context;
        mChooserConfig = chooserConfig;
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        FileInfo fileInfo = getFileItem(cursor.getPosition());
        if (fileInfo == null) {
            // file is not existing, create a fake info
            fileInfo = new FileInfo();
            fileInfo.dbId = cursor.getLong(COLUMN_ID);
            fileInfo.filePath = cursor.getString(COLUMN_PATH);
            fileInfo.fileName = Util.getNameFromFilepath(fileInfo.filePath);
            fileInfo.fileSize = cursor.getLong(COLUMN_SIZE);
            fileInfo.ModifiedDate = cursor.getLong(COLUMN_DATE);
        }
        FileListItem.setupFileListItemInfo(mContext, view, fileInfo, mFileIcon);
        if (mCategory == Util.Category.image) {
            view.findViewById(R.id.file_checkbox).setOnClickListener(
                    new FileListItem.FileItemOnClickListener(mFragment, mChooserConfig, mFileInteractionHelper));
            // 图片预览
            view.findViewById(R.id.file_icon).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileInfo tag = (FileInfo) v.getTag();
                            GPreviewBuilder.from(mFragment.getActivity())
                                    .setSingleData(new UserViewInfo(tag.filePath))
                                    .setCurrentIndex(0)
                                    .setDrag(true,0.6f)
                                    .setSingleShowType(false)
                                    .start();
                        }
                    });
        } else {
            view.setOnClickListener(
                    new FileListItem.FileItemOnClickListener(mFragment, mChooserConfig, mFileInteractionHelper));
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        if (mCategory == Util.Category.image) {
            view = mFactory.inflate(R.layout.image_item, parent, false);
        } else {
            view = mFactory.inflate(R.layout.file_item, parent, false);
        }
        return view;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        mFileNameList.clear();
        super.changeCursor(cursor);
    }

    public Collection<FileInfo> getAllFiles() {
        if (mFileNameList.size() == getCount())
            return mFileNameList.values();

        Cursor cursor = getCursor();
        if (cursor.moveToFirst()) {
            do {
                Integer position = Integer.valueOf(cursor.getPosition());
                if (mFileNameList.containsKey(position))
                    continue;
                FileInfo fileInfo = getFileInfo(cursor);
                if (fileInfo != null) {
                    mFileNameList.put(position, fileInfo);
                }
            } while (cursor.moveToNext());
        }

        return mFileNameList.values();
    }

    public FileInfo getFileItem(int pos) {
        Integer position = Integer.valueOf(pos);
        if (mFileNameList.containsKey(position))
            return mFileNameList.get(position);

        Cursor cursor = (Cursor) getItem(pos);
        FileInfo fileInfo = getFileInfo(cursor);
        if (fileInfo == null)
            return null;

        fileInfo.dbId = cursor.getLong(COLUMN_ID);
        mFileNameList.put(position, fileInfo);
        return fileInfo;
    }

    private FileInfo getFileInfo(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? null : Util
                .GetFileInfo(cursor.getString(COLUMN_PATH));
    }
}
