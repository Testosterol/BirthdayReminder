package apps.testosterol.birthdayreminder.Main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.R;
import apps.testosterol.birthdayreminder.Reminder.Fragments.CreateReminderFragment;
import apps.testosterol.birthdayreminder.Reminder.Fragments.ReminderInfoFragment;
import apps.testosterol.birthdayreminder.SettingsFragment;
import apps.testosterol.birthdayreminder.Util.DialogUtilPrompt;

import static apps.testosterol.birthdayreminder.Util.Util.dpToPx;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
                                                                CreateReminderFragment.OnFragmentInteractionListener,
                                                                ReminderInfoFragment.OnFragmentInteractionListener,
                                                                NavigationView.OnNavigationItemSelectedListener,
                                                                SettingsFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IMG_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReminderDatabase.createDatabase(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    DialogUtilPrompt.showNeverAskAgainDialogWriteExternalStorage(this, new DialogUtilPrompt.OnDialogClickCallback() {
                        @Override
                        public void onPositiveClick(MaterialDialog dialog) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                        @Override
                        public void onNegativeClick(MaterialDialog dialog) {
                            DialogUtilPrompt.dismissDialogWithCheck(dialog);
                        }
                    });
                } else {
                    Toast toast = Toast.makeText(this, R.string.permission_phone_write_external_storage_toast, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, dpToPx(70));
                    toast.show();
                }
            }else{
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_RESULT);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
       /* if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }*/
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

}
