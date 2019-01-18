package apps.testosterol.birthdayreminder.Notification;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import apps.testosterol.birthdayreminder.MainActivity;
import apps.testosterol.birthdayreminder.R;

public class NotificationActivity extends AppCompatActivity implements Serializable {

    private static final int IMG_RESULT = 1;
    EditText name, birthday, notificationDate;
    ImageView profilePicture;
    Notification notification;

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

        Intent intent = getIntent();

        notification = (Notification) intent.getSerializableExtra("notification");

        name.setText(notification.getName());
        birthday.setText(notification.getBirthday());
        notificationDate.setText(notification.getNotificationDate());

        byte[] imageByteArray = Base64.decode(notification.getImage(), Base64.DEFAULT);

        Glide.with(this)
                .load(imageByteArray)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserImage();
            }
        });
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
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);

            Glide.with(this)
                    .load(bitmap)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            String profilePicture = Base64.encodeToString(image,Base64.DEFAULT);
            notification.setImage(profilePicture);
            //    saveProfilePicture(profilePicture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
