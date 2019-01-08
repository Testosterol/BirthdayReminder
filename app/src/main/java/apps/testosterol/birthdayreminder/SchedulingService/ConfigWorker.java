package apps.testosterol.birthdayreminder.SchedulingService;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ConfigWorker extends Worker {

    private static final String TAG = ConfigWorker.class.getSimpleName();

    public ConfigWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Method to execute the work for dispatching config / initializing config.
     *
     * @return Result of execution.
     */
    @NonNull
    @Override
    public Result doWork () {

        return Result.success();
    }
}