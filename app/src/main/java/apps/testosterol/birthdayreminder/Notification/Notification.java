package apps.testosterol.birthdayreminder.Notification;

import android.net.Uri;

import java.io.Serializable;

public class Notification implements Serializable {
     private  Integer _id;
     private String notificationDate;
    private String name;
    private String birthdayDate;
    private String image;
    private Integer id;
    private boolean event_email;
  
    public Notification(){}

    Notification(Integer id, String image, String notificationDate, boolean isEmail, String remindantName, String birthdayDate){
        this._id = id;
        this.image = image;
        this.notificationDate = notificationDate;
        this.event_email = isEmail;
        this.birthdayDate = birthdayDate;
        this.name = remindantName;
    }

    public void setId(Integer id){this._id = id;}

    public void setName(String name) { this.name = name; }

    public void setImage(String image){this.image = image;}

    public void setBirthday(String birthday){this.birthdayDate = birthday;}

    public void setNotificationDate(String birthdayDate) {this.notificationDate = birthdayDate;}

    public void setEventEmail(boolean isEmail){this.event_email = isEmail;}

    public String getBirthday(){ return birthdayDate; }

    public String getName() { return name; }

    public String getImage() { return image; }

    public String getNotificationDate() { return notificationDate; }

    public Integer getId(){ return _id;}

    public boolean getEmail(){ return event_email; }

 }