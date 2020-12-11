package com.valley.file.selector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

@NativePlugin(
        requestCodes = {FileSelector.REQUEST_FILE_PICK}
)
public class FileSelector extends Plugin {
    protected static final int REQUEST_FILE_PICK = 200; // Unique request code

    private ProgressDialog loginDialog;

    @PluginMethod
    public void chooser(PluginCall call) {
        showLoginDialog();
        saveCall(call);
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        intent.putExtra("MULTIPLE", call.getBoolean("multiple", false));
        intent.putExtra("MAX", call.getInt("max", 10));
        startActivityForResult(call, intent, REQUEST_FILE_PICK);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        // Get the previously saved call
        PluginCall savedCall = getSavedCall();

        if (savedCall == null) {
            return;
        }
        if (requestCode == REQUEST_FILE_PICK && resultCode == RESULT_OK) {
            int sync = data.getIntExtra("bigdata:synccode", -1);
            final Bundle bigData = ResultIPC.get().getLargeData(sync);
            ArrayList<String> fileNames = bigData.getStringArrayList("MULTIPLEFILENAMES");
            JSArray res = new JSArray(fileNames);
            JSObject json = new JSObject();
            json.put("files", res);
            savedCall.resolve(json);
        }
        dismissDialog();
    }

    private void showLoginDialog() {
        loginDialog = new ProgressDialog(getContext());
        loginDialog.setTitle("提示");
        loginDialog.setMessage("加载中,请稍后...");
        loginDialog.setCancelable(false);
        loginDialog.show();
    }

    private void dismissDialog() {
        if (loginDialog != null && loginDialog.isShowing()) {
            loginDialog.dismiss();
        }
    }
}
