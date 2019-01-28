package apps.testosterol.birthdayreminder.Notification;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;



import apps.testosterol.birthdayreminder.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Notification> notificationList;
    private List<Notification> notificationListFiltered;
    private NotificationsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, birthdayDate, notificationDate;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            birthdayDate = view.findViewById(R.id.birthday);
            notificationDate = view.findViewById(R.id.notification_date);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected notification in callback
                    listener.onNotificationSelected(notificationListFiltered.get(getAdapterPosition()));
                    Intent intent = new Intent(context, NotificationActivity.class);

                    Log.d("TESTEST", "id:" + notificationListFiltered.get(getAdapterPosition()).getId() +
                            " name:" + notificationListFiltered.get(getAdapterPosition()).getName() + " image: " +
                            notificationListFiltered.get(getAdapterPosition()).getImage());

                    Notification notification = new Notification();
                    notification.setName(notificationListFiltered.get(getAdapterPosition()).getName());
                    notification.setImage(notificationListFiltered.get(getAdapterPosition()).getImage());
                    notification.setBirthday(notificationListFiltered.get(getAdapterPosition()).getBirthday());
                    notification.setNotificationDate(notificationListFiltered.get(getAdapterPosition()).getNotificationDate());
                    notification.setId(notificationListFiltered.get(getAdapterPosition()).getId());
                    notification.setEventEmail(notificationListFiltered.get(getAdapterPosition()).getEmail());
                    // 3. put person in intent data
                    intent.putExtra("notification", notification);

                    context.startActivity(intent);
                }
            });
        }
    }


    public NotificationAdapter(Context context, List<Notification> notificationList, NotificationsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.notificationList = notificationList;
        this.notificationListFiltered = notificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Notification notification = notificationListFiltered.get(position);
        holder.name.setText(notification.getName());
        holder.birthdayDate.setText(String.format("%s%s%s", context.getString(R.string.Birthday),": ", notification.getBirthday()));
        holder.notificationDate.setText(String.format("%s%s%s",context.getString(R.string.NotificationDatenotification), ": ", notification.getNotificationDate()));
        String imagePath = notification.getImage();
        Glide.with(context)
                .load(imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return notificationListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    notificationListFiltered = notificationList;
                } else {
                    List<Notification> filteredList = new ArrayList<>();
                    for (Notification row : notificationList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or birthdayDate number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getBirthday().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    notificationListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = notificationListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notificationListFiltered = (ArrayList<Notification>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface NotificationsAdapterListener {
        void onNotificationSelected(Notification notification);
    }
}