package com.valley.file.selector;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.valley.file.selector.capacitorpluginfileselector.R;
import com.valley.file.selector.media.AudiovisualListFragment;
import com.valley.file.selector.media.FileCategoryHelper;
import com.valley.file.selector.media.FileIconHelper;
import com.valley.file.selector.media.FileListCursorAdapter;
import com.valley.file.selector.media.ImageListFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private FileInteractionHelper interactionHelper;
    private ChooserConfig mChooserConfig;

    @StringRes
    private int[] TAB_TITLES;
    private Context mContext;

    public SectionsPagerAdapter(Context context, ChooserConfig chooserConfig, FragmentManager fm, FileInteractionHelper fh) {
        super(fm);
        TAB_TITLES = new int[]{
                R.string.tab_document,
                R.string.tab_picture,
                R.string.tab_audiovisual,
                R.string.tab_other};
        mContext = context;
        interactionHelper = fh;
        interactionHelper.clearSelection();
        mChooserConfig = chooserConfig;
    }

    @Override
    public Fragment getItem(int position) {
        /*if (position == 0) {
            FileListFragment fileListFragment = new FileListFragment();
            fileListFragment.mFileInteractionHelper = interactionHelper;
            fileListFragment.mChooserConfig = mChooserConfig;
            return fileListFragment;
        } else {*/
        FileCategoryHelper fileCategoryHelper = new FileCategoryHelper(mContext);
        if (position == 0) {
            AudiovisualListFragment audiovisualListFragment = new AudiovisualListFragment();
            audiovisualListFragment.mChooserConfig = mChooserConfig;
            Cursor cursor = fileCategoryHelper.query(Util.Category.doc);
            FileIconHelper fileIconHelper = new FileIconHelper(mContext, Util.Category.doc);
            audiovisualListFragment.mAdapter = new FileListCursorAdapter(audiovisualListFragment, mContext, mChooserConfig,
                    cursor, fileIconHelper, interactionHelper, Util.Category.doc);
            interactionHelper.setCursor(audiovisualListFragment.mAdapter);
            return audiovisualListFragment;
        } else if (position == 1) {
            ImageListFragment imageListFragment = new ImageListFragment();
            imageListFragment.mChooserConfig = mChooserConfig;
            Cursor query = fileCategoryHelper.query(Util.Category.image);
            FileIconHelper fileIconHelper = new FileIconHelper(mContext, Util.Category.image);
            imageListFragment.mAdapter = new FileListCursorAdapter(imageListFragment, mContext, mChooserConfig,
                    query, fileIconHelper, interactionHelper, Util.Category.image);
            interactionHelper.setCursor(imageListFragment.mAdapter);
            return imageListFragment;
        } else if (position == 2) {
            AudiovisualListFragment audiovisualListFragment = new AudiovisualListFragment();
            audiovisualListFragment.mChooserConfig = mChooserConfig;
            Cursor cursor = fileCategoryHelper.query(Util.Category.audiovisual);
            FileIconHelper fileIconHelper = new FileIconHelper(mContext, Util.Category.audiovisual);
            audiovisualListFragment.mAdapter = new FileListCursorAdapter(audiovisualListFragment, mContext, mChooserConfig,
                    cursor, fileIconHelper, interactionHelper, Util.Category.audiovisual);
            interactionHelper.setCursor(audiovisualListFragment.mAdapter);
            return audiovisualListFragment;
        } else  {
            AudiovisualListFragment audiovisualListFragment = new AudiovisualListFragment();
            audiovisualListFragment.mChooserConfig = mChooserConfig;
            Cursor cursor = fileCategoryHelper.query(Util.Category.other);
            FileIconHelper fileIconHelper = new FileIconHelper(mContext, Util.Category.other);
            audiovisualListFragment.mAdapter = new FileListCursorAdapter(audiovisualListFragment, mContext, mChooserConfig,
                    cursor, fileIconHelper, interactionHelper, Util.Category.other);
            interactionHelper.setCursor(audiovisualListFragment.mAdapter);
            return audiovisualListFragment;
        }
        /*else {
            OtherListFragment otherListFragment = new OtherListFragment();
            otherListFragment.mChooserConfig = mChooserConfig;
            otherListFragment.mFileInteractionHelper = interactionHelper;
            return otherListFragment;
        }}*/

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

}