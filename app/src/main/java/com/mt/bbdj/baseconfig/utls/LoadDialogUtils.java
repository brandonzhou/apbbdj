package com.mt.bbdj.baseconfig.utls;

import android.app.Activity;

import com.mt.bbdj.baseconfig.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/7/19
 * Description :  加载框
 */
public class LoadDialogUtils {

    private static LoadingDialog loadingDialog;

    private static LoadDialogUtils loadDialogUtils;

    private static List<LoadingDialog> loadingDialogList;

    public LoadDialogUtils() {
        loadingDialogList = new ArrayList<>();
    }

    public static LoadDialogUtils getInstance(){
        if(loadDialogUtils == null) {
            loadDialogUtils = new LoadDialogUtils();
        }
        return loadDialogUtils;
    }

    public static void showLoadingDialog(Activity context) {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(context)
                .setCancelable(true)
                .setCancelOutside(false);
        loadingDialog =  loadBuilder.create();
        loadingDialog.show();

        loadingDialogList.add(loadingDialog);
    }

    public static void cannelLoadingDialog() {

        if (loadingDialog != null && loadingDialogList.size() != 0) {
            for (LoadingDialog loadingDialog : loadingDialogList) {
                loadingDialog.cancel();
                loadingDialog = null;
            }
        }
       /* if (loadingDialog != null) {
            loadingDialog.cancel();
            loadingDialog = null;
        }*/
    }


}
