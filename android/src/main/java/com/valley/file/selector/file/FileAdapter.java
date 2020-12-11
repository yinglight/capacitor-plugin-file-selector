package com.valley.file.selector.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.valley.file.selector.ChooserConfig;
import com.valley.file.selector.FileInfo;
import com.valley.file.selector.FileInteractionHelper;
import com.valley.file.selector.capacitorpluginfileselector.R;

import java.util.ArrayList;

import static com.valley.file.selector.Util.convertStorage;
import static com.valley.file.selector.Util.formatDateString;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    public ArrayList<FileInfo> fileList;
    private FileInteractionHelper mFileInteractionHelper;
    private Context mContext;
    private ChooserConfig mChooserConfig;

    public FileAdapter(Context context, ChooserConfig chooserConfig, ArrayList<FileInfo> fileList, FileInteractionHelper fileInteractionHelper) {
        this.fileList = fileList;
        this.mFileInteractionHelper = fileInteractionHelper;
        mContext = context;
        mChooserConfig = chooserConfig;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View fileView;
        ImageView fileImage;
        TextView fileName;
        ImageView checkbox;
        TextView fileSize;
        TextView fileModifyTime;

        public ViewHolder(View itemView) {
            super(itemView);
            fileView = itemView;
            fileImage = itemView.findViewById(R.id.file_icon);
            fileName = itemView.findViewById(R.id.file_name);
            checkbox = itemView.findViewById(R.id.file_checkbox);
            fileSize = itemView.findViewById(R.id.file_size);
            fileModifyTime = itemView.findViewById(R.id.modified_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.fileView.setOnClickListener(new View.OnClickListener() {
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
                if (mFileInteractionHelper.getSelectedFileList().size() > 0) {
                    rootView.findViewById(fakeR.getId("id", "pick_operation_bar")).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(fakeR.getId("id", "pick_operation_bar")).setVisibility(View.GONE);
                }*/
            }
        });
        return holder;
    }

    public void fileItemReverse(FileInfo tag, ImageView img, View v) {
        tag.Selected = !tag.Selected;
        if (mFileInteractionHelper.onCheckItem(tag, v)) {
            img.setImageResource(tag.Selected ? R.drawable.btn_check_on_holo_light : R.drawable.btn_check_off_holo_light);
        } else {
            tag.Selected = !tag.Selected;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileInfo file = fileList.get(position);
        if (file.fileName.endsWith(".pdf")) {
            holder.fileImage.setImageResource(R.drawable.file_icon_pdf);
        } else if (file.fileName.endsWith(".doc") || file.fileName.endsWith(".docx")) {
            holder.fileImage.setImageResource(R.drawable.file_icon_word);
        } else if (file.fileName.endsWith(".xls") || file.fileName.endsWith(".xlsx")) {
            holder.fileImage.setImageResource(R.drawable.file_icon_excel);
        } else if (file.fileName.endsWith(".ppt") || file.fileName.endsWith(".pptx")) {
            holder.fileImage.setImageResource(R.drawable.file_icon_powerpoint);
        } else if (file.fileName.endsWith(".rar")) {
            holder.fileImage.setImageResource(R.drawable.file_icon_rar);
        } else if (file.fileName.endsWith(".zip")) {
            holder.fileImage.setImageResource(R.drawable.file_icon_zip);
        }
        holder.fileName.setText(file.fileName);
        holder.fileModifyTime.setText(formatDateString(mContext, file.ModifiedDate));
        holder.fileSize.setText(convertStorage(file.fileSize));
        holder.checkbox.setVisibility(View.VISIBLE);
        holder.checkbox.setImageResource(file.Selected ? R.drawable.btn_check_on_holo_light
                : R.drawable.btn_check_off_holo_light);
        holder.checkbox.setTag(file);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
