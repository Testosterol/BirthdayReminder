package apps.testosterol.birthdayreminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import apps.testosterol.birthdayreminder.Database.DatabaseNotifications;
import apps.testosterol.birthdayreminder.Database.DatabaseStats;
import apps.testosterol.birthdayreminder.Notification.Notification;
import apps.testosterol.birthdayreminder.SchedulingService.ConfigWorker;


public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.RecyclerItemViewHolder> {
    private ArrayList<Notification> myList;
    private String mDate;
    private String mName;
    private Context mContext;



    NotificationRecyclerViewAdapter(Context context, ArrayList<Notification> myList) {
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

    void notifyData(ArrayList<Notification> myList, String birthdayDate, String name) {
        Log.d("notifyData ", myList.size() + " Birthday date: " + birthdayDate + " name: " + name);
        this.myList = myList;
        this.mDate = birthdayDate;
        this.mName = name;
        //notifyDataSetChanged();
        notifyItemInserted(0);
    }

    class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        EditText regularityNotification;
        Spinner notificationChoose;
        FloatingActionButton confirmNotification, cancelNotification;
        CheckBox emailNotification;

        Animation fab_open, fab_close;

        private ConstraintLayout constraingNotification;

        RecyclerItemViewHolder(final View parent) {
            super(parent);

            fab_open = AnimationUtils.loadAnimation(parent.getContext(), R.anim.add_open);
            fab_close = AnimationUtils.loadAnimation(parent.getContext(), R.anim.add_close);

            regularityNotification = parent.findViewById(R.id.regularity_notification);
            notificationChoose = parent.findViewById(R.id.notification_choose);
            confirmNotification = parent.findViewById(R.id.confirm_notification);
            cancelNotification = parent.findViewById(R.id.cancel_notification);
            emailNotification = parent.findViewById(R.id.email_notification);
            constraingNotification =  parent.findViewById(R.id.constraingNotification);

            constraingNotification.setOnClickListener(new View.OnClickListener() {
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
            boolean isEmailNotification = false;
            if(emailNotification.isChecked()){
                isEmailNotification = true;
            }

            createNotification(mContext, num, notificationDailyWeeklyMonthly
                    , isEmailNotification, mName , mDate);
           /* MainActivity karol = new MainActivity();
            karol.createNotification(num,notificationDailyWeeklyMonthly,isEmailNotification );*/
            //otification notification = new Notification(num, notificationDailyWeeklyMonthly, isEmailNotification);

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

        public void createNotification(Context context, String numOfDaysWeeksMonths, String notificationDailyWeeklyMonthly,
                                       boolean isEmailNotification, String name, String BirthdayDate) {
            Calendar todayDate = Calendar.getInstance();
            //put shit into internal db...

            //calculate difference between birthday day and days/months/weeks before
            //add it to the calendar

            String date[] = BirthdayDate.split("-");

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

            long dateOfNotificationMillis = dateOfNotification.getTimeInMillis();
            long todayDateMillis = todayDate.getTimeInMillis();
            long dateDifferenceInMillis = dateOfNotificationMillis - todayDateMillis;
            long notificationDelayInDays = TimeUnit.MILLISECONDS.toDays(dateDifferenceInMillis);

            todayDate.add(Calendar.DAY_OF_YEAR, (int) notificationDelayInDays);

            String imageUrl = getURLForResource(R.drawable.birtdhaycake);
            DatabaseNotifications.getDatabaseNotifications(context).addEvent(name, BirthdayDate,
                    String.format(Locale.ENGLISH,"%1$tA %1$tb %1$td %1$tY", dateOfNotification),
                    System.currentTimeMillis(), notificationDelayInDays, isEmailNotification, imageUrl );



    /*        //generate email if clicked ?
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","abc@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));*/
        }

        public String getURLForResource (int resourceId) {
            return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
        }

        /**
         * Method to start workmanager to schedule flush of initial config.
         *
         * @param timeInDays , given time when the background work should be executed.
         */
        public void startWorkerInitConfig(long timeInDays){
            OneTimeWorkRequest oneTimeDispatch = new OneTimeWorkRequest.Builder(ConfigWorker.class)
                    .setInitialDelay(timeInDays, TimeUnit.DAYS) // run just one time at this time
                    .addTag("notification")
                    .build();
            WorkManager.getInstance().enqueue(oneTimeDispatch);


            // TODO: FIGURE OUT CANCELLING SPECIFIC WORK

     /*   WorkManager.getInstance().enqueueUniqueWork()
        WorkManager.getInstance().cancelWorkById();*/
        }



    }
}
