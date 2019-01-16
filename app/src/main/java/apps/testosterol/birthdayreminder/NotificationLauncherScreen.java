package apps.testosterol.birthdayreminder;

public class NotificationLauncherScreen {
    
    private String name, birthdayDate, notificationDate;

    public NotificationLauncherScreen() {
    }

    public NotificationLauncherScreen(String name, String birthdayDate, String notificationDate) {
        this.name = name;
        this.birthdayDate = birthdayDate;
        this.notificationDate = notificationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }
}