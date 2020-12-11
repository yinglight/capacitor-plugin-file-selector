package com.valley.file.selector.media;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.valley.file.selector.ChooserConfig;
import com.valley.file.selector.FileInfo;
import com.valley.file.selector.FileInteractionHelper;
import com.valley.file.selector.Util;
import com.valley.file.selector.capacitorpluginfileselector.R;

public class FileListItem {
    public static void setupFileListItemInfo(Context context, View view,
                                             FileInfo fileInfo, FileIconHelper fileIcon) {
        ImageView checkbox = view.findViewById(R.id.file_checkbox);
        checkbox.setVisibility(View.VISIBLE);
        checkbox.setImageResource(fileInfo.Selected ? R.drawable.btn_check_on_holo_light
                : R.drawable.btn_check_off_holo_light);
        checkbox.setTag(fileInfo);
        ImageView icon = view.findViewById(R.id.file_icon);
        icon.setTag(fileInfo);
        view.setSelected(fileInfo.Selected);
        Util.setText(view, R.id.file_name, fileInfo.fileName);
        /*Util.setText(view, fakeR.getId("id", "file_count"), fileInfo.IsDir ? "(" + fileInfo.Count + ")" : "");*/
        Util.setText(view, R.id.modified_time, Util.formatDateString(context, fileInfo.ModifiedDate));
        Util.setText(view, R.id.file_size, (fileInfo.IsDir ? "" : Util.convertStorage(fileInfo.fileSize)));

        ImageView lFileImage = view.findViewById(R.id.file_icon);

        fileIcon.setIcon(fileInfo, lFileImage);
    }

    public static class FileItemOnClickListener implements OnClickListener {
        private Context mContext;
        private FileInteractionHelper mFileInteractionHelper;
        private Fragment mFragment;
        private ChooserConfig mChooserConfig;

        public FileItemOnClickListener(Fragment fragment, ChooserConfig chooserConfig, FileInteractionHelper mFileInteractionHelper) {
            mFragment = fragment;
            mContext = fragment.getContext();
            this.mFileInteractionHelper = mFileInteractionHelper;
            mChooserConfig = chooserConfig;
        }

        @Override
        public void onClick(View v) {
            ImageView img = v.findViewById(R.id.file_checkbox);
            assert (img != null && img.getTag() != null);

            FileInfo tag = (FileInfo) img.getTag();

            // 单选部分
            if (!mChooserConfig.multiple && mFileInteractionHelper.getSelectedFileList().size() > 0) {
                if (tag.Selected) {
                    fileItemReverse(tag, img, v);
                } else {
                    Toast.makeText(mContext, "不能多选", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // 多选部分
            else if (mFileInteractionHelper.getSelectedFileList().size() >= mChooserConfig.max) {
                if (tag.Selected) {
                    fileItemReverse(tag, img, v);
                } else {
                    Toast.makeText(mContext, "做多选择" + mChooserConfig.max, Toast.LENGTH_SHORT).show();
                }
                return;
            }
            fileItemReverse(tag, img, v);

            /*View rootView = v.getRootView();
            if (mFileInteractionHelper.getSelectedFileList().size() > 0){
                rootView.findViewById(R.id.pick_operation_bar).setVisibility(View.VISIBLE);
            } else {
                rootView.findViewById(R.id.pick_operation_bar).setVisibility(View.GONE);
            }*/
        }

        public void fileItemReverse(FileInfo tag, ImageView img, View v) {
            tag.Selected = !tag.Selected;
            if (mFileInteractionHelper.onCheckItem(tag, v)) {
                img.setImageResource(tag.Selected ? R.drawable.btn_check_on_holo_light
                        : R.drawable.btn_check_off_holo_light);
            } else {
                tag.Selected = !tag.Selected;
            }
        }
    }
}
