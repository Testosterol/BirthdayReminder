package apps.testosterol.birthdayreminder.Notification;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import apps.testosterol.birthdayreminder.Database.DatabaseNotifications;
import apps.testosterol.birthdayreminder.MainActivity;
import apps.testosterol.birthdayreminder.R;

public class NotificationActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = NotificationActivity.class.getName();
    private static final int IMG_RESULT = 1;
    EditText name, birthday, notificationDate;
    ImageView profilePicture;
    Button save, reset;
    boolean isFabOpen = false;
    FloatingActionButton menu;
    Notification notification;
    Integer id;
    int mYear, mMonth, mDay;
    Animation fab_open, fab_close;
    String path;


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.notification_activity);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profilePicture = findViewById(R.id.profilePicture);
        name = findViewById(R.id.nameNotification);
        birthday = findViewById(R.id.birthdayDateNotification);
        notificationDate = findViewById(R.id.notificationDate);

        menu = findViewById(R.id.add2);
        save = findViewById(R.id.saveReminder);
        reset = findViewById(R.id.resetButton);

        save.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);

        fab_open = AnimationUtils.loadAnimation(this, R.anim.add_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.add_close);

        Intent intent = getIntent();

        notification = (Notification) intent.getSerializableExtra("notification");

        name.setText(notification.getName());
        birthday.setText(notification.getBirthday());
        notificationDate.setText(notification.getNotificationDate());
        id = notification.getId();

       String imagePath = notification.getImage();

        Glide.with(this)
                .load(imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserImage();
            }
        });

        notificationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Notification date edit click");
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NotificationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format(Locale.ENGLISH, "%02d-%02d-%d", dayOfMonth, (monthOfYear + 1),year );
                                notificationDate.setText(formattedDate);
                                setNewNotification(year, monthOfYear, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Birthday edit click");
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NotificationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format(Locale.ENGLISH, "%02d-%02d-%d", dayOfMonth, (monthOfYear + 1),year );
                                birthday.setText(formattedDate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenu();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChanges();
            }
        });
    }

    private void setNewNotification(int year, int month, int day) {
        Calendar todayDate = Calendar.getInstance();
        Calendar notificationDate = Calendar.getInstance();
        notificationDate.set(year, month, day);
        long dateOfNotificationMillis = notificationDate.getTimeInMillis();
        long todayDateMillis = todayDate.getTimeInMillis();
        long dateDifferenceInMillis = dateOfNotificationMillis - todayDateMillis;
        long notificationDelayInDays = TimeUnit.MILLISECONDS.toDays(dateDifferenceInMillis);

        if(notificationDelayInDays < 0){
            Toast toast = Toast.makeText(this, R.string.past_date_year, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, dpToPx(70));
            toast.show();
        }else{
            // todo: reschedule notification based on id

        }
    }

    private void resetChanges() {
        name.setText(notification.getName());
        birthday.setText(notification.getBirthday());
        notificationDate.setText(notification.getNotificationDate());
        id = notification.getId();
        String imagePath = notification.getImage();

        Glide.with(this)
                .load(imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);

    }

    private void saveChanges() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getUserImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMG_RESULT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();
        path = getPath(selectedImage);
        Glide.with(this)
                .load(path)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);
        //notification.setImage(path);
        //DatabaseNotifications.getDatabaseNotifications(this).updateEventPicture(id,path);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor =getContentResolver().query(uri, projection, null,null,null);
        int column_index = 0;
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return "";
    }

    private void openMenu(){
        save.setScaleX(1f);
        save.setScaleY(1f);
        reset.setScaleX(1f);
        reset.setScaleY(1f);
        save.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
        if (isFabOpen) {
            save.startAnimation(fab_close);
            reset.startAnimation(fab_close);
            save.setClickable(false);
            reset.setClickable(false);
            isFabOpen = false;
            menu.animate().rotation(0);
        } else {
            save.startAnimation(fab_open);
            reset.startAnimation(fab_open);
            save.setClickable(true);
            reset.setClickable(true);
            isFabOpen = true;
            menu.animate().rotation(45);
        }
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
