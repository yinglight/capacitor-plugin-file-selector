package com.valley.file.selector.media;


import android.content.Context;
import android.widget.ImageView;

import com.valley.file.selector.FileInfo;
import com.valley.file.selector.Util;
import com.valley.file.selector.capacitorpluginfileselector.R;

import java.util.HashMap;

public class FileIconHelper {

    private static final String TAG = "FileIconHelper";

    private static HashMap<String, Integer> fileExtToIcons = new HashMap<>();

    private FileIconLoader mIconLoader;
    private Util.Category mType;

    public FileIconHelper(Context context, Util.Category type) {
        mIconLoader = new FileIconLoader(context, type);
        mType = type;
        addItem(new String[]{
                "mp3", "wav", "wma", "mid"
        }, R.drawable.file_icon_audio);
        addItem(new String[]{
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "avi"
        }, R.drawable.file_icon_video);
        addItem(new String[]{
                "xls", "xlt", "xlsx", "xltx"
        }, R.drawable.file_icon_excel);
        addItem(new String[]{
                "ppt", "pot", "pps", "pptx", "potx", "ppsx"
        }, R.drawable.file_icon_powerpoint);
        addItem(new String[]{
                "doc", "dot", "docx", "dotx"
        }, R.drawable.file_icon_word);
        addItem(new String[]{
                "pdf"
        }, R.drawable.file_icon_pdf);
        addItem(new String[]{
                "zip", "rar"
        }, R.drawable.file_icon_zip);
    }

    private void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }

    public int getFileIcon(String ext) {
        Integer i = fileExtToIcons.get(ext.toLowerCase());
        if (i != null) {
            return i.intValue();
        } else {
            int id = 0;
            if (mType == Util.Category.image) {
                id = R.drawable.file_icon_picture;
            } else {
                id = R.drawable.file_icon_default;
            }
            return id;
        }

    }

    public void setIcon(FileInfo fileInfo, ImageView fileImage) {
        String filePath = fileInfo.filePath;
        long fileId = fileInfo.dbId;
        String extFromFilename = Util.getExtFromFilename(filePath);
        int id = getFileIcon(extFromFilename);
        fileImage.setImageResource(id);
        mIconLoader.cancelRequest(fileImage);

        if (mType == Util.Category.image) {
            boolean set = mIconLoader.loadIcon(fileImage, filePath, fileId);
            if (!set) {
                int drawable = R.drawable.file_icon_picture;
                fileImage.setImageResource(drawable);
            }
        }
    }
}
