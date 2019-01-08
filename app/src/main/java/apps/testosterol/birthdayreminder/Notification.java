package apps.testosterol.birthdayreminder;

import android.os.Build;
import android.support.v7.widget.RecyclerView;

import java.util.Date;

class Notification {

    private String num, notificationDailyWeeklyMonthly, name;
    private boolean isEmailNotification;
    private Date date;

    Notification(){}

    Notification(String number, String notificationRegularity, boolean isEmail, String remindantName, Date birthdayDate){
        this.num = number;
        this.notificationDailyWeeklyMonthly = notificationRegularity;
        this.isEmailNotification = isEmail;
        this.date = birthdayDate;
        this.name = remindantName;
    }

    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

        }else{

        }
    }

}