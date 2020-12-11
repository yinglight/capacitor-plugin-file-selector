package com.valley.file.selector;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

public class Util {
    public static ArrayList<FileInfo> documentList = new ArrayList<>();

    public static ArrayList<FileInfo> otherList = new ArrayList<>();

    private static final String TAG = "Util";

    public enum SortMethod {
        name, size, date, type
    }

    public enum Category {
        image, audiovisual, doc, other
    }

    public static HashSet<String> sAudiovisualMimeTypesSet = new HashSet<String>() {
        {
            add("audio/midi");
            add("audio/mpeg");
            add("audio/x-ms-wma");
            add("audio/x-wav");
            add("video/mp4");
            add("video/x-ms-wmv");
            add("video/mpeg");
            add("video/m4v");
            add("video/3gpp");
            add("video/x-msvideo");
        }
    };

    public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
        {
            add("application/pdf");
            add("application/vnd.ms-powerpoint");
            add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            add("application/msword");
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            add("application/vnd.ms-excel");
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
    };

    public static HashSet<String> sZipFileMimeType = new HashSet<String>() {
        {
            add("application/zip");
            add("application/rar");
        }
    };

    public static boolean isDocument(File file) {
        boolean status = file.getName().endsWith(".pdf")
                || file.getName().endsWith(".doc")
                || file.getName().endsWith(".docx")
                || file.getName().endsWith(".xls")
                || file.getName().endsWith(".xlsx")
                || file.getName().endsWith(".ppt")
                || file.getName().endsWith(".pptx");
        return status;
    }

    public static boolean otherTypeFilter(File file) {
        boolean status = file.getName().endsWith(".zip")
                || file.getName().endsWith(".rar");
        return status;
    }

    public static FileInfo GetFileInfo(String filePath) {
        File lFile = new File(filePath);
        if (!lFile.exists())
            return null;

        FileInfo lFileInfo = new FileInfo();
        lFileInfo.canRead = lFile.canRead();
        lFileInfo.canWrite = lFile.canWrite();
        lFileInfo.isHidden = lFile.isHidden();
        lFileInfo.fileName = lFile.getName();
        lFileInfo.ModifiedDate = lFile.lastModified();
        lFileInfo.IsDir = lFile.isDirectory();
        lFileInfo.filePath = filePath;
        lFileInfo.fileSize = lFile.length();
        if (lFileInfo.IsDir) {
            int lCount = 0;
            File[] files = lFile.listFiles();

            // null means we cannot access this dir
            if (files == null) {
                return null;
            }

            for (File child : files) {
                lCount++;
            }
            lFileInfo.Count = lCount;

        } else {

            lFileInfo.fileSize = lFile.length();

        }
        return lFileInfo;
    }

    // storage, G M K B
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static String formatDateString(Context context, long time) {
        DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat
                .getTimeFormat(context);
        Date date = new Date(time);
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    public static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    public static boolean setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    public static ArrayList<FileInfo> initDocuments(String type) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
            getFiles(externalStorageDirectory, type);
            Collections.sort(documentList, new SortByDate());
            Collections.sort(otherList, new SortByDate());
        if (type.equals("doc")) {
            return documentList;
        } else {
            return otherList;
        }
    }

    public static void getFiles(File dir, String type) {
        File fileList[] = dir.listFiles();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    getFiles(fileList[i], type);
                } else {
                    if (isDocument(fileList[i]) && type.equals("doc")) {
                        documentList.add(GetFileInfo(fileList[i].getPath()));
                    }
                    if (otherTypeFilter(fileList[i]) && type.equals("other")) {
                        otherList.add(GetFileInfo(fileList[i].getPath()));
                    }
                }
            }
        }
    }

}

class SortByDate implements Comparator<FileInfo> {

    @Override
    public int compare(FileInfo o1, FileInfo o2) {
        if (o1.ModifiedDate < o2.ModifiedDate) {
            return 1;
        }
        return -1;
    }
}