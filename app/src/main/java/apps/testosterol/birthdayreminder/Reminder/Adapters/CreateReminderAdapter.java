package apps.testosterol.birthdayreminder.Reminder.Adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import apps.testosterol.birthdayreminder.BroadcastReceiver.AlarmWakeUpBroadcastReceiver;
import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.R;
import apps.testosterol.birthdayreminder.Reminder.Reminder;


public class CreateReminderAdapter extends RecyclerView.Adapter<CreateReminderAdapter.RecyclerItemViewHolder> {
    private ArrayList<Reminder> myList;
    private String mDate;
    private String mName;
    private String mImagePath;
    private Context mContext;



    public CreateReminderAdapter(Context context, ArrayList<Reminder> myList) {
        this.myList = myList;
        this.mContext = context;
    }
    @NonNull
    public RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.recycler_view_items, parent, false);
        return new RecyclerItemViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Log.d("onBindViewHoler ", myList.size() + "");
    }
    @Override
    public int getItemCount() {
        return(null != myList?myList.size():0);
    }

    public void notifyData(ArrayList<Reminder> myList, String birthdayDate, String name, String imagePath) {
        Log.d("notifyData ", myList.size() + " Birthday date: " + birthdayDate + " name: " + name);
        this.myList = myList;
        this.mDate = birthdayDate;
        this.mName = name;
        this.mImagePath = imagePath;

        //notifyDataSetChanged();
        notifyItemInserted(0);
    }

    class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        EditText regularityNotification;
        Spinner notificationChoose;
        FloatingActionButton confirmNotification, cancelNotification;

        Animation fab_open, fab_close;

        private ConstraintLayout constraintNotificationLayout;

        RecyclerItemViewHolder(final View parent) {
            super(parent);

            fab_open = AnimationUtils.loadAnimation(parent.getContext(), R.anim.add_open);
            fab_close = AnimationUtils.loadAnimation(parent.getContext(), R.anim.add_close);

            regularityNotification = parent.findViewById(R.id.regularity_notification);
            notificationChoose = parent.findViewById(R.id.notification_choose);
            confirmNotification = parent.findViewById(R.id.confirm_notification);
            cancelNotification = parent.findViewById(R.id.cancel_notification);
            constraintNotificationLayout =  parent.findViewById(R.id.constrainNotificationLayout);

            constraintNotificationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();
                }
            });

            confirmNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveNotification();
                }
            });



        }

        @SuppressLint("RestrictedApi")
        void saveNotification(){
            String num = regularityNotification.getText().toString();
            String notificationDailyWeeklyMonthly = notificationChoose.getSelectedItem().toString();

            createNotification(num, notificationDailyWeeklyMonthly, mName , mDate);

            confirmNotification.setScaleX(1f);
            confirmNotification.setScaleY(1f);
            confirmNotification.startAnimation(fab_close);
            confirmNotification.setClickable(false);
            confirmNotification.setVisibility(View.INVISIBLE);

            cancelNotification.setScaleX(1f);
            cancelNotification.setScaleY(1f);
            cancelNotification.startAnimation(fab_open);
            cancelNotification.setClickable(true);
            cancelNotification.setVisibility(View.VISIBLE);


            cancelNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelNotification.setClickable(false);
                    cancelNotification.setVisibility(View.INVISIBLE);

                    confirmNotification.setClickable(true);
                    confirmNotification.setVisibility(View.VISIBLE);

                    myList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

        public void createNotification(String numOfDaysWeeksMonths, String notificationDailyWeeklyMonthly
                , String name, String birthdayDate) {

            Calendar dateOfBirthdayInCurrentYear = retreiveBirthdayDateForCurrentYear(birthdayDate);

            Calendar dateOfNotification = calculateDateOfNotification(dateOfBirthdayInCurrentYear,
                                                                        numOfDaysWeeksMonths,
                                                                        notificationDailyWeeklyMonthly);

            Calendar todayDate = Calendar.getInstance();
            long dateOfNotificationMillis = dateOfNotification.getTimeInMillis();
            long todayDateMillis = todayDate.getTimeInMillis();
            long dateDifferenceInMillis = dateOfNotificationMillis - todayDateMillis; // to add to current date.
            long notificationDelayInDays = TimeUnit.MILLISECONDS.toDays(dateDifferenceInMillis); // to add to current date in days.

            todayDate.add(Calendar.DAY_OF_YEAR, (int) notificationDelayInDays);


            Reminder reminder = createNewReminderBasedOnUserInput(mImagePath, name, birthdayDate, notificationDailyWeeklyMonthly);

            long insertedId = ReminderDatabase.getInstance(mContext).daoAccess().insertOnlySingleNotification(reminder);

            setNotificationForSpecificDate(insertedId, reminder, dateDifferenceInMillis);
        }

        private Calendar calculateDateOfNotification(Calendar dateOfNotification, String numOfDaysWeeksMonths,
                                                     String notificationDailyWeeklyMonthly) {

            Calendar todayDate = Calendar.getInstance();

            int numSubtract;

            switch (notificationDailyWeeklyMonthly) {
                case "Days":
                    if(numOfDaysWeeksMonths != null) {
                        numSubtract = Integer.valueOf(numOfDaysWeeksMonths);
                        dateOfNotification.add(Calendar.DAY_OF_YEAR, -numSubtract);
                    }
                    break;
                case "Weeks":
                    if(numOfDaysWeeksMonths != null) {
                        numSubtract = Integer.valueOf(numOfDaysWeeksMonths);
                        numSubtract *= 7;
                        dateOfNotification.add(Calendar.DAY_OF_YEAR, -numSubtract);
                    }
                    break;
                case "Months":
                    if(numOfDaysWeeksMonths != null) {
                        numSubtract = Integer.valueOf(numOfDaysWeeksMonths);
                        dateOfNotification.add(Calendar.MONTH, -numSubtract);
                    }
                    break;
            }

            if(todayDate.get(Calendar.YEAR) > dateOfNotification.get(Calendar.YEAR)) {
                dateOfNotification.set (Calendar.YEAR, todayDate.get(Calendar.YEAR));
            }
            if(todayDate.get(Calendar.MONTH) > dateOfNotification.get(Calendar.MONTH)){
                dateOfNotification.set (Calendar.YEAR, todayDate.get(Calendar.YEAR) + 1);
            }else{
                if ((todayDate.get(Calendar.MONTH) == dateOfNotification.get(Calendar.MONTH) && (todayDate.get(Calendar.DAY_OF_YEAR) > dateOfNotification.get(Calendar.DAY_OF_YEAR)))) {
                    dateOfNotification.set (Calendar.YEAR, todayDate.get(Calendar.YEAR) + 1);
                }
            }

            return dateOfNotification;
        }

        private Calendar retreiveBirthdayDateForCurrentYear(String birthdayDate) {
            String date[] = birthdayDate.split("-");

            Calendar dateOfNotification = Calendar.getInstance();

            if(Integer.parseInt(date[2]) < dateOfNotification.get(Calendar.YEAR)) {
                date[2] = String.valueOf(dateOfNotification.get(Calendar.YEAR));
            }
            if((Integer.parseInt(date[1]) - 1) < dateOfNotification.get(Calendar.MONTH)){
                date[2] = String.valueOf(dateOfNotification.get(Calendar.YEAR));
                dateOfNotification.set((Integer.parseInt(date[2]) +1), Integer.parseInt(date[1])-1, Integer.parseInt(date[0]));
            }else{
                if ((Integer.parseInt(date[1]) - 1) == dateOfNotification.get(Calendar.MONTH) && Integer.parseInt(date[0]) < dateOfNotification.get(Calendar.DAY_OF_YEAR)) {
                    date[2] = String.valueOf(dateOfNotification.get(Calendar.YEAR));
                    dateOfNotification.set((Integer.parseInt(date[2]) +1), Integer.parseInt(date[1])-1, Integer.parseInt(date[0]));
                }else{
                    dateOfNotification.set((Integer.parseInt(date[2])), Integer.parseInt(date[1])-1, Integer.parseInt(date[0]));
                }
            }
            return dateOfNotification;
        }

        private Reminder createNewReminderBasedOnUserInput(String mImagePath, String name, String birthdayDate, String notificationDailyWeeklyMonthly) {
            Reminder reminder = new Reminder();
            reminder.setReminderImage(mImagePath);
            reminder.setReminderName(name);
            reminder.setReminderBirthdayDate(birthdayDate);
            reminder.setNotificationDate(notificationDailyWeeklyMonthly);
            return reminder;
        }

        private void setNotificationForSpecificDate(long insertedId, Reminder reminder, long dateDifferenceInMillis) {

            Log.d("TESTINGINTENTALARM", "setNotificaitonForSpecificDate");

            Log.d("TESTINGINTENTALARM", "insertedId: " + insertedId);

            Intent intent = new Intent(mContext, AlarmWakeUpBroadcastReceiver.class);

            intent.putExtra("reminder_name", reminder.getReminderImage());
            intent.putExtra("reminder_id", insertedId);

            Log.d("TESTINGINTENTALARM", "intent.getId: " + Objects.requireNonNull(intent.getExtras()).get("reminder_id"));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int)insertedId, intent, PendingIntent.FLAG_ONE_SHOT);


            //mContext.registerReceiver( AlarmWakeUpBroadcastReceiver.class ,intent);
            mContext.sendBroadcast(intent);

            Calendar c = Calendar.getInstance();
            int mseconds = c.get(Calendar.MILLISECOND);
            long timeOrLengthofWait = 10000;
            AlarmManager aManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            if (aManager != null) {
                aManager.set(alarmType, SystemClock.elapsedRealtime()+timeOrLengthofWait, pendingIntent );
            }
        }


    }
}
