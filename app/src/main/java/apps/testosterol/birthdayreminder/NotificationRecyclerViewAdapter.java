package apps.testosterol.birthdayreminder;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;


public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.RecyclerItemViewHolder> {
    private ArrayList<Notification> myList;
    private String mDate;
    private String mName;



    NotificationRecyclerViewAdapter(ArrayList<Notification> myList) {
        this.myList = myList;
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

            new MainActivity().createNotification(num, notificationDailyWeeklyMonthly
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

    }
}
