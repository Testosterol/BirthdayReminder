package apps.testosterol.birthdayreminder;

import java.util.ArrayList;

public class Notification {
    private String mName;
    private boolean mOnline;

    public Notification(String name, boolean online) {
        mName = name;
        mOnline = online;
    }

    public String getName() {
        return mName;
    }

    public boolean isOnline() {
        return mOnline;
    }

    private static int lastContactId = 0;

    public static ArrayList<Notification> createContactsList(int numNotifications) {
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        for (int i = 1; i <= numNotifications; i++) {
            notifications.add(new Notification("Person " + ++lastContactId, i <= numNotifications / 2));
        }

        return notifications;
    }
}