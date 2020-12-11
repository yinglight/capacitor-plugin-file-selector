package com.valley.file.selector.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.valley.file.selector.Util;

import java.util.Iterator;

public class FileCategoryHelper {
    private static final String TAG = "FileCategoryHelper";

    private Context mContext;

    public enum SortMethod {
        name, size, date, type
    }

    public FileCategoryHelper(Context context) {
        mContext = context;
    }


    public Cursor query(Util.Category type) {
        Uri uri;
        String selection = buildSelectionByCategory(type);
        if (type == Util.Category.image) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Files.getContentUri("external");
        }
        String sortOrder = buildSortOrder(SortMethod.date);
        Cursor cursor;
        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED
        };
        cursor = mContext.getContentResolver().query(uri, columns, selection, null, sortOrder);
        return cursor;
    }

    private String buildSelectionByCategory(Util.Category cat) {
        String selection;
        switch (cat) {
            case audiovisual:
                selection = buildAudiovisualSelection();
                break;
            case doc:
                selection = buildDocSelection();
                break;
            case other:
                selection = buildZipSelection();
                break;
            default:
                selection = null;
        }
        return selection;
    }

    private String buildAudiovisualSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = Util.sAudiovisualMimeTypesSet.iterator();
        while (iter.hasNext()) {
            selection.append("(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1);
    }

    private String buildDocSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = Util.sDocMimeTypesSet.iterator();
        while (iter.hasNext()) {
            selection.append("(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1);
    }

    private String buildZipSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = Util.sZipFileMimeType.iterator();
        while (iter.hasNext()) {
            selection.append("(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1);
    }

    private String buildSortOrder(SortMethod sort) {
        String sortOrder = null;
        switch (sort) {
            case name:
                sortOrder = MediaStore.Files.FileColumns.TITLE + " asc";
                break;
            case size:
                sortOrder = MediaStore.Files.FileColumns.SIZE + " asc";
                break;
            case date:
                sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc";
                break;
            case type:
                sortOrder = MediaStore.Files.FileColumns.MIME_TYPE + " asc, " + MediaStore.Files.FileColumns.TITLE + " asc";
                break;
        }
        return sortOrder;
    }
}
