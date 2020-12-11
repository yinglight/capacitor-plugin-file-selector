package com.valley.file.selector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.valley.file.selector.capacitorpluginfileselector.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FileInteractionHelper interactionHelper = FileInteractionHelper.getInstance();

    private ChooserConfig chooserConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_selector_activity_main);
        boolean multiple = getIntent().getBooleanExtra("MULTIPLE", false);
        int max = getIntent().getIntExtra("MAX", 50);
        getPermission();
        chooserConfig = new ChooserConfig(multiple, max);
        updateData();
        View viewById = findViewById(R.id.button_pick_confirm);
        viewById.setOnClickListener(v -> {
            Intent intent = new Intent();
            Bundle res = new Bundle();
            ArrayList<FileInfo> selectedFileList = interactionHelper.getSelectedFileList();
            ArrayList<String> multipleNames = new ArrayList<>();
            for (int i = 0; i < selectedFileList.size(); i++) {
                multipleNames.add(selectedFileList.get(i).filePath);
            }
            res.putStringArrayList("MULTIPLEFILENAMES", multipleNames);
            int sync = ResultIPC.get().setLargeData(res);
            intent.putExtra("bigdata:synccode", sync);
            setResult(RESULT_OK, intent);
            finish();
        });
        View buttonCancel = findViewById(R.id.button_pick_cancel);
        buttonCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        /*View updateDataView = findViewById(fakeR.getId("id", "update_data"));
        updateDataView.setOnClickListener(v-> {
            updateData();
        });*/
    }

    private void getPermission() {
        int readExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readExternalStorage != PackageManager.PERMISSION_GRANTED || writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    111);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                getPermission();
            }
        }
    }

    private void updateData() {
        MediaScannerConnection.scanFile(this
                , new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()}
                , new String[]{"image/jpeg"}, (path, uri) -> {
                    Log.i("cxmyDev", "onScanCompleted : " + path);
                });
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, chooserConfig, getSupportFragmentManager(), interactionHelper);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

}