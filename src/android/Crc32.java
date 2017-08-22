package org.apache.cordova;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

import android.net.Uri;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi.OpenForReadResult;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.CRC32;

import android.util.Log;

public class Crc32 extends CordovaPlugin {

    private static final String LOG_TAG = "Crc32";

    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        if ("crc32".equals(action)) {
            crc32(args, callbackContext);
            return true;
        }

        return false;
    }

    private void crc32(final CordovaArgs args, final CallbackContext callbackContext) {
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                crc32Sync(args, callbackContext);
            }
        });
    }

    private void crc32Sync(CordovaArgs args, CallbackContext callbackContext) {
        try {

            String fileName = args.getString(0);

            Uri fileUri = getUriForArg(fileName);

            CordovaResourceApi resourceApi = webView.getResourceApi();

            File tempFile = resourceApi.mapUriToFile(fileUri);
            if (tempFile == null || !tempFile.exists()) {
                String errorMessage = "File " + fileName + " does not exist";
                callbackContext.error(errorMessage);
                Log.e(LOG_TAG, errorMessage);
                return;
            }

            OpenForReadResult fileRead = resourceApi.openForRead(fileUri);

            InputStream inputStream = new BufferedInputStream(fileRead.inputStream);
            CRC32 crc = new CRC32();
            for (int count; (count = inputStream.read()) != -1; ) {
                crc.update(count);
            }
            
            long crcValue = crc.getValue();
            String finalCrc = Long.toHexString(crcValue);

            callbackContext.success(finalCrc);

        } catch (Exception e) {
            String errorMessage = "An error occurred during the process.";
            callbackContext.error(errorMessage);
            Log.e(LOG_TAG, errorMessage, e);
        }
    }

    private Uri getUriForArg(String arg) {
        CordovaResourceApi resourceApi = webView.getResourceApi();
        Uri target = Uri.parse(arg);
        return resourceApi.remapUri(target.getScheme() != null ? target : Uri.fromFile(new File(arg)));
    }

}