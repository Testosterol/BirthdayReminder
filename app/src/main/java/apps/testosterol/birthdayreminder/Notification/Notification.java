package apps.testosterol.birthdayreminder.Notification;

import android.os.Build;
import android.support.v7.widget.RecyclerView;

import java.io.Serializable;
import java.util.Date;

 public class Notification implements Serializable {

    private String notificationDate, name, birthdayDate , image;
    private boolean isEmailNotification;
  
    public Notification(){}

    Notification(String notificationDatebe, boolean isEmail, String remindantName, String birthdayDate){
        this.notificationDate = notificationDatebe;
        this.isEmailNotification = isEmail;
        this.birthdayDate = birthdayDate;
        this.name = remindantName;
    }

     public void setName(String name) { this.name = name; }

     public void setImage(String image){this.image = image;}

     public void setBirthday(String birthday){this.birthdayDate = birthday;}
     
     public void setNotificationDate(String birthdayDate) {this.notificationDate = birthdayDate;}

     public String getBirthday(){ return birthdayDate; }
     
    public String getName() {
        return name;
    }

    public String getImage() { return image; }

    public String getNotificationDate() { return notificationDate; }

}