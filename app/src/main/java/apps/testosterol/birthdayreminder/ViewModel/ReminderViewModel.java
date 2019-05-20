package apps.testosterol.birthdayreminder.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import apps.testosterol.birthdayreminder.Database.ReminderRepository;
import apps.testosterol.birthdayreminder.Reminder.Reminder;

/*
 * Denis created this class on the 20/05/2019
 */

public class ReminderViewModel extends AndroidViewModel {

    private LiveData<List<Reminder>> allReminders;
    private ReminderRepository repository;

    public ReminderViewModel(@NonNull Application application) {
    super(application);
        repository = new ReminderRepository(application);
        allReminders = repository.getAllReminders();
    }

    public LiveData<List<Reminder>> getAllReminders(){
        return allReminders;
    }
}
