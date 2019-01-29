package apps.testosterol.birthdayreminder.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import apps.testosterol.birthdayreminder.R;

public class DialogUtilPrompt {

    public static void showPermissionDialog(Activity activity, String title, String rationale, OnDialogClickCallback onDialogClickCallback) {
        if (activity != null && !activity.isFinishing()) {
            showDialog(activity, title, rationale, activity.getString(R.string.ok), activity.getString(R.string.cancel), false, onDialogClickCallback);
        }
    }



    public static void showNeverAskAgainDialogWriteExternalStorage(Context context, final OnDialogClickCallback onDialogClickCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        Resources res = context.getResources();
        builder.theme(Theme.LIGHT)
                .title(res.getString(R.string.permission_title))
                .content(res.getString(R.string.permission_phone_rationale_write_external_storage))
                //.typeface(medium, regular)
                .positiveText(res.getString(R.string.permission_go_to_settings))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (onDialogClickCallback != null) {
                            onDialogClickCallback.onPositiveClick(dialog);
                        }
                    }
                }).negativeText(res.getString(R.string.cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (onDialogClickCallback != null) {
                            onDialogClickCallback.onNegativeClick(dialog);
                        }
                    }
                })
                .cancelable(false)
                .show();
    }


    private static void showDialog(Context context, String title, String message, String positiveText, String negativeText, boolean cancelable, final OnDialogClickCallback onDialogClickCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.theme(Theme.LIGHT)
                .title(title)
                .content(message)
                //.typeface(medium, regular)
                .positiveText(positiveText)
                .negativeText(negativeText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (onDialogClickCallback != null) {
                            onDialogClickCallback.onPositiveClick(dialog);
                        }
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (onDialogClickCallback != null) {
                    onDialogClickCallback.onNegativeClick(dialog);
                }
            }
        })
                .cancelable(cancelable)
                .show();
    }

    public static void dismissDialogWithCheck(Dialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {

                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();

                // if the Context used here was an activity AND it hasn't been finished or destroyed
                // then dismiss it
                if (context instanceof Activity) {

                    // Api >=17
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                            dismissWithTryCatch(dialog);
                        }
                    } else {

                        // Api < 17. Unfortunately cannot check for isDestroyed()
                        if (!((Activity) context).isFinishing()) {
                            dismissWithTryCatch(dialog);
                        }
                    }
                } else
                    // if the Context used wasn't an Activity, then dismiss it too
                    dismissWithTryCatch(dialog);
            }
            dialog = null;
        }
    }

    private static void dismissWithTryCatch(Dialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        } finally {
            dialog = null;
        }
    }

    public interface OnDialogClickCallback {
        void onPositiveClick(MaterialDialog dialog);

        void onNegativeClick(MaterialDialog dialog);
    }
}
