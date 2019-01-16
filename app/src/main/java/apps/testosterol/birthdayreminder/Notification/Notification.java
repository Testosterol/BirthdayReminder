package apps.testosterol.birthdayreminder.Notification;

import android.os.Build;
import android.support.v7.widget.RecyclerView;

import java.util.Date;

 public class Notification {

    private String num, notificationDailyWeeklyMonthly, name;
    private boolean isEmailNotification;
    private Date date;

    String image;
    String phone;

    public Notification(){}

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


     public void setName(String name) {
         this.name = name;
     }

     public void setImage(String image){this.image = image;}

     public void setPhone(String phone) {this.phone = phone;}

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPhone() {
        return phone;
    }

}