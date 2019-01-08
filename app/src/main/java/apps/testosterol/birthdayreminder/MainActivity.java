package apps.testosterol.birthdayreminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import apps.testosterol.birthdayreminder.SchedulingService.ConfigWorker;

public class MainActivity extends AppCompatActivity implements LifecycleOwner{

    private static final String TAG = MainActivity.class.getSimpleName();

    Button addReminder, random;
    Animation fab_open, fab_close;
    boolean isFabOpen = false;
    FloatingActionButton add, addNotification;
    Dialog dialog;
    EditText birthDay, name;
    int mYear, mMonth, mDay;
    int saveYear, saveMonth, saveDay;
    RecyclerView mRecyclerView;
    NotificationRecyclerViewAdapter mRecyclerAdapter;
    ArrayList<Notification> myList = new ArrayList<>();

    private static String dateOfNotification,nameOfNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLifecycle().addObserver(LifecycleTracker.getInstance());

        add = findViewById(R.id.Add);
        addReminder = findViewById(R.id.AddReminder);
        random = findViewById(R.id.RandomButton);

        addReminder.setVisibility(View.INVISIBLE);
        random.setVisibility(View.INVISIBLE);

        fab_open = AnimationUtils.loadAnimation(this, R.anim.add_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.add_close);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenu();
            }
        });

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateLayoutReminder();
            }
        });

        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void openMenu(){
        addReminder.setScaleX(1f);
        addReminder.setScaleY(1f);
        random.setScaleX(1f);
        random.setScaleY(1f);
        addReminder.setVisibility(View.VISIBLE);
        random.setVisibility(View.VISIBLE);
        if (isFabOpen) {
            addReminder.startAnimation(fab_close);
            random.startAnimation(fab_close);
            addReminder.setClickable(false);
            random.setClickable(false);
            isFabOpen = false;
            add.animate().rotation(0);
        } else {
            addReminder.startAnimation(fab_open);
            random.startAnimation(fab_open);
            addReminder.setClickable(true);
            random.setClickable(true);
            isFabOpen = true;
            add.animate().rotation(45);
        }
    }

    private void inflateLayoutReminder() {
        Log.d(TAG, "Add reminder");
        final View dialogView = View.inflate(MainActivity.this ,R.layout.create_reminder_layout, null);
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(dialogView);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int w = dialogView.getWidth();
                    int h = dialogView.getHeight();

                    int endRadius = (int) Math.hypot(w, h);

                    int cx = (int) (addReminder.getX() + (addReminder.getWidth() / 2));
                    int cy = (int) (addReminder.getY()) + addReminder.getHeight() + 56;

                    Animator revealAnimator;

                    revealAnimator = ViewAnimationUtils.createCircularReveal(dialogView, cx, cy, 0, endRadius);

                    dialogView.setVisibility(View.VISIBLE);
                    revealAnimator.setDuration(700);
                    revealAnimator.start();
                }
            }
        });

        dialog.show();

        birthDay = (dialog.findViewById(R.id.birthdaydate));
        name = (dialog.findViewById(R.id.name));
        addNotification = (dialog.findViewById(R.id.AddNotification));

        birthDay.setFocusable(false);
        birthDay.setClickable(true);

        myList.clear();

        mRecyclerView = dialog.findViewById(R.id.recyclerView);
        mRecyclerAdapter = new NotificationRecyclerViewAdapter(myList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        addNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!birthDay.getText().toString().equals("Birthday date") && !birthDay.getText().toString().equals("")
                && !name.getText().toString().equals("") && !name.getText().toString().equals("Name")) {
                    Notification mLog = new Notification();
                    myList.add(mLog);
                    mRecyclerAdapter.notifyData(myList, getBirthdayOfNotification(), getNameOfNotification());
                }else{
                    Toast.makeText(MainActivity.this, "Please fill Birthday and Name first", Toast.LENGTH_LONG).show();
                }
                //createNotification("","",true, "", "");
            }
        });
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "Name edit click");
                name.setText("");
                if(!hasFocus){
                    final InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                    }
                    name.clearFocus();
                }
                saveName(name.getText().toString());
            }
        });
        birthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthDay.setText("");
                Log.d(TAG, "Birthday edit click");
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format(Locale.ENGLISH, "%02d-%02d-%d", dayOfMonth, (monthOfYear + 1),year );
                                birthDay.setText(formattedDate);
                                saveBirthday(formattedDate);
                                saveYear(year);
                                saveMonth(monthOfYear);
                                saveDay(dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
    }

    public void saveYear(int year){ saveYear = year; }
    public void saveMonth(int month){ saveMonth = month; }
    public void saveDay(int day){ saveDay = day; }
    public int getYear(){ return saveYear; }
    public int getMonth(){ return saveMonth; }
    public int getDay(){ return saveDay; }

    public void saveName(String name){
        nameOfNotification = name;
    }
    public void saveBirthday(String date){
        dateOfNotification = date;
    }
    public String getNameOfNotification(){
        return nameOfNotification;
    }
    public String getBirthdayOfNotification(){ return dateOfNotification;}

    public void createNotification(String numOfDaysWeeksMonths, String notificationDailyWeeklyMonthly,
                                   boolean isEmailNotification, String name, String BirthdayDate) {

        //put shit into internal db...

        //calculate difference between birthday day and days/months/weeks before
        //add it to the calendar

        // birthday date 15-10-2019
        // numOfDaysWeeksMonths
        int numSubstract;

        //check if year is negative / older

        Calendar karol = Calendar.getInstance();
        karol.setTimeInMillis(System.currentTimeMillis());
        if (getYear() < karol.get(Calendar.YEAR)) {
            saveYear(getYear() + 1);
        }
        karol.set(getDay(), getMonth(), getYear());
        switch (notificationDailyWeeklyMonthly) {
            case "Days":
                numSubstract = Integer.valueOf(numOfDaysWeeksMonths);
                karol.add(Calendar.DAY_OF_YEAR, -numSubstract);
                break;
            case "Weeks":
                numSubstract = Integer.valueOf(numOfDaysWeeksMonths);
                karol.add(Calendar.WEEK_OF_YEAR, -numSubstract);
                break;
            case "Months":
                numSubstract = Integer.valueOf(numOfDaysWeeksMonths);
                karol.add(Calendar.MONTH, -numSubstract);
                break;
        }

        Log.d("TESTKAROL", "time :" + karol.getTimeInMillis() + " String numOfDaysWeeksMonths: " + numOfDaysWeeksMonths + " Birthday date : " + BirthdayDate);


       /* boolean alarm = (PendingIntent.getBroadcast(this, 0, new Intent("ALARM"), PendingIntent.FLAG_NO_CREATE)   == null);

        if (alarm) {
            Intent itAlarm = new Intent("ALARM");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, itAlarm, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);

            Log.d("TESTDATE", "birthday date :" + getBirthdayOfNotification() + " calendar date + 3seconds: " + calendar );

            AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, karol.getTimeInMillis(), 60000, pendingIntent);
        }
    }*/
    }


    /**
     * Method to start workmanager to schedule flush of initial config.
     *
     * @param timeInSeconds , given time when the background work should be executed.
     */
    public static void startWorkerInitConfig(long timeInSeconds){
        OneTimeWorkRequest oneTimeDispatch = new OneTimeWorkRequest.Builder(ConfigWorker.class)
                .setInitialDelay(timeInSeconds, TimeUnit.SECONDS) // run just one time at this time
                .addTag("notification")
                .build();
        WorkManager.getInstance().enqueue(oneTimeDispatch);
    }


}
