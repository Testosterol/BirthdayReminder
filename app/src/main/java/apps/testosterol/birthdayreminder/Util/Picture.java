package apps.testosterol.birthdayreminder.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import apps.testosterol.birthdayreminder.R;

public class Picture extends Activity {


    private static int IMG_RESULT = 1;

    private void getUserImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMG_RESULT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);

            //ImageView my_img_view = findViewById (R.id.imageView1);

            //my_img_view.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            String profilePicture = Base64.encodeToString(image,Base64.DEFAULT);
            //    saveProfilePicture(profilePicture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
