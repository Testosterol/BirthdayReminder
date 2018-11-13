package apps.testosterol.birthdayreminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;


public class NotificationRecyclerViewAdapter extends
        RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {

    // ... constructor and member variables

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public NotificationRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recycler_view_items, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull NotificationRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Notification notification = mNotifications.get(position);

        // Set item views based on your views and data model

        FloatingActionButton mConfirmNotification = viewHolder.confirmNotification;
        Spinner mRegularity = viewHolder.regularity;
        EditText mNumberOfRegularity = viewHolder.numberOfRegularity;
        CheckBox mEmailNotification = viewHolder.emailNotif;

      /*  TextView textView = viewHolder.nameTextView;
        textView.setText(notification.getName());
        Button button = viewHolder.messageButton;
        button.setText(notification.isOnline() ? "Message" : "Offline");
        button.setEnabled(notification.isOnline());
    */}

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    // ... view holder defined above...

    // Store a member variable for the contacts
    private List<Notification> mNotifications;

    // Pass in the contact array into the constructor
    NotificationRecyclerViewAdapter(List<Notification> notifications) {
        mNotifications = notifications;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        CheckBox emailNotif;
        EditText numberOfRegularity;
        Spinner regularity;
        FloatingActionButton confirmNotification;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            emailNotif = itemView.findViewById(R.id.email_notification);
            numberOfRegularity =  itemView.findViewById(R.id.regularity_notification);
            regularity = itemView.findViewById(R.id.notification_choose);
            confirmNotification =  itemView.findViewById(R.id.confirm_notification);
        }
    }
}