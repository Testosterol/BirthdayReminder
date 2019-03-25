package apps.testosterol.birthdayreminder.Util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import java.util.Objects;

import apps.testosterol.birthdayreminder.R;

public class Util {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void dontCoverTopOfTheScreenWithApp(View view, Activity activtyContext){
        int flags = Objects.requireNonNull(view.getSystemUiVisibility());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        view.setSystemUiVisibility(flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(activtyContext).getWindow().setStatusBarColor(Color.WHITE);
        }

    }

    public static String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+ Objects.requireNonNull(R.class.getPackage()).getName()+"/" +resourceId).toString();
    }

}
