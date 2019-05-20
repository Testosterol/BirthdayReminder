package apps.testosterol.birthdayreminder.Database;

/*
 * Denis created this class on the 20/05/2019
 */

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import apps.testosterol.birthdayreminder.Reminder.Reminder;

public class ReminderRepository {

    private DaoAccess daoAccess;

    public ReminderRepository(Application application){
        ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(application);
        daoAccess = reminderDatabase.daoAccess();
    }

    public void insert(Reminder reminder){
        new InsertReminderAsyncTask(daoAccess).execute(reminder);
    }
    public void delete(Reminder reminder){
        new DeleteReminderAsyncTask(daoAccess).execute(reminder);
    }
    public void update(Reminder reminder){
        new UpdateReminderAyncTask(daoAccess).execute(reminder);
    }


    public LiveData<List<Reminder>> getAllReminders(){
        return daoAccess.fetchAllNotifications();
    }


    private static class InsertReminderAsyncTask extends AsyncTask<Reminder, Void, Void>{
        private DaoAccess daoAccess;

        private InsertReminderAsyncTask(DaoAccess daoAccess){
            this.daoAccess = daoAccess;
        }

        @Override
        protected Void doInBackground(Reminder... reminders) {
            daoAccess.insertOnlySingleNotification(reminders[0]);
            return null;
        }
    }

    private static class DeleteReminderAsyncTask extends AsyncTask<Reminder, Void, Void> {
        private DaoAccess daoAccess;

        private DeleteReminderAsyncTask(DaoAccess daoAccess){
            this.daoAccess = daoAccess;
        }
        @Override
        protected Void doInBackground(Reminder... reminders) {
            daoAccess.deleteNotification(reminders[0]);
            return null;
        }
    }

    private static class UpdateReminderAyncTask extends AsyncTask<Reminder, Void, Void>{

        private DaoAccess daoAccess;

        private UpdateReminderAyncTask(DaoAccess daoAccess){
            this.daoAccess = daoAccess;
        }

        @Override
        protected Void doInBackground(Reminder... reminders) {
            daoAccess.updateNotification(reminders[0]);
            return null;
        }
    }

}
