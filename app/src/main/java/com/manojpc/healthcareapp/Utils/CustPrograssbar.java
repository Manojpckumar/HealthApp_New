package com.manojpc.healthcareapp.Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class CustPrograssbar {
    public static ProgressDialog progressDialog;

    public void progressCreate(Context context) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            } else {
                progressDialog = new ProgressDialog(context);
                if (progressDialog != null && !progressDialog.isShowing()) {

                    progressDialog.setMessage("Progress...");
                    progressDialog.show();
                }
            }
        } catch (Exception e) {

        }
    }

    public void close() {
        if (progressDialog != null) {
            try {
                progressDialog.cancel();
            } catch (Exception e) {
            }
        }
    }

    public void setCancel(boolean v){
        progressDialog.setCancelable(v);

    }
}
