package apps.testosterol.birthdayreminder.Reminder.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import apps.testosterol.birthdayreminder.R;
import apps.testosterol.birthdayreminder.Reminder.Reminder;

public class MainScreenRemindersAdapter extends RecyclerView.Adapter<MainScreenRemindersAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Reminder> reminderList;
    private List<Reminder> reminderListFiltered;
    private MainScreenRemindersAdapterListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView selectedReminderName, selectedReminderBirthdayDate, selectedReminderNotificationDate;
        public ImageView selectedReminderImage;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            selectedReminderName = view.findViewById(R.id.name);
            selectedReminderBirthdayDate = view.findViewById(R.id.user_row_item_birthday_date);
            selectedReminderNotificationDate = view.findViewById(R.id.user_row_item_notification_date);
            selectedReminderImage = view.findViewById(R.id.thumbnail);

            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected reminder in callback
                    listener.onReminderSelected(reminderListFiltered.get(getAdapterPosition()));

                    Bundle args = new Bundle();
                    args.putString("reminderName", reminderListFiltered.get(getAdapterPosition()).getReminderName());
                    args.putString("reminderImage", reminderListFiltered.get(getAdapterPosition()).getReminderImage());
                    args.putString("reminderBirthdayDate", reminderListFiltered.get(getAdapterPosition()).getReminderBirthdayDate());


                    Calendar notificationDateFromArgs = Calendar.getInstance();
                    notificationDateFromArgs.setTimeInMillis(reminderListFiltered.get(getAdapterPosition()).getNotificationDateInMillis());

                    String formattedNotificationDate = String.format(Locale.ENGLISH, "%02d-%02d-%d",
                            notificationDateFromArgs.get(Calendar.DAY_OF_MONTH),
                            (notificationDateFromArgs.get(Calendar.MONTH) + 1),
                            notificationDateFromArgs.get(Calendar.YEAR) );


                    Log.d("reminderInfoDate", "date to args:" + formattedNotificationDate);

                    args.putLong("reminderNotificationDateInMillis", reminderListFiltered.get(getAdapterPosition()).getNotificationDateInMillis());
                    args.putInt("reminderId", reminderListFiltered.get(getAdapterPosition()).get_reminderId());

                    Navigation.findNavController(Objects.requireNonNull(view))
                            .navigate(R.id.action_mainFragment_to_reminderInfo, args);
                }
            });
        }
    }


    public MainScreenRemindersAdapter(Context context, List<Reminder> reminderList, MainScreenRemindersAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.reminderList = reminderList;
        this.reminderListFiltered = reminderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void setMessages(List<Reminder> reminders){
        this.reminderList = reminders;
        this.reminderListFiltered = reminders;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Reminder reminder = reminderListFiltered.get(position);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(reminder.getNotificationDateInMillis());
        String formattedDate = String.format(Locale.ENGLISH, "%02d-%02d-%d", c.get(Calendar.DAY_OF_MONTH), (c.get(Calendar.MONTH) + 1),c.get(Calendar.YEAR) );

        holder.selectedReminderName.setText(reminder.getReminderName());
        holder.selectedReminderBirthdayDate.setText(String.format("%s%s%s", context.getString(R.string.birthday),": ", reminder.getReminderBirthdayDate()));
        holder.selectedReminderNotificationDate.setText(String.format("%s: %s", context.getString(R.string.date_of_reminder), formattedDate));
        String imagePath = reminder.getReminderImage();
        Glide.with(context)
                .load(imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.selectedReminderImage);
    }

    public void removeItem(int position) {
        reminderListFiltered.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }
    public void restoreItem(Reminder reminder, int position) {
        reminderListFiltered.add(position, reminder);
        // notify item added by position
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return reminderListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    reminderListFiltered = reminderList;
                } else {
                    List<Reminder> filteredList = new ArrayList<>();
                    for (Reminder row : reminderList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or birthdayDate number match
                        if (row.getReminderName().toLowerCase().contains(charString.toLowerCase()) || row.getReminderBirthdayDate().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    reminderListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = reminderListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                reminderListFiltered = (ArrayList<Reminder>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface MainScreenRemindersAdapterListener {
        void onReminderSelected(Reminder reminder);
    }
}