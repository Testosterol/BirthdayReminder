package apps.testosterol.birthdayreminder;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import apps.testosterol.birthdayreminder.Notification.Notification;
import apps.testosterol.birthdayreminder.Notification.NotificationAdapter;
import apps.testosterol.birthdayreminder.SchedulingService.ConfigWorker;
import apps.testosterol.birthdayreminder.Database.DatabaseNotifications;
import apps.testosterol.birthdayreminder.Util.MyDividerItemDecoration;

import static apps.testosterol.birthdayreminder.Util.NetworkInfo.URL;

public class MainActivity extends AppCompatActivity implements LifecycleOwner, NotificationAdapter.NotificationsAdapterListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CHANNEL_ID = "1";

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

    private RecyclerView recyclerView;
    ArrayList<Notification> notificationList = new ArrayList<>();
    private NotificationAdapter mAdapter;
    private SearchView searchView;

    private static String dateOfNotification,nameOfNotification;

    private static final String URL = "https://api.androidhive.info/json/contacts.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLifecycle().addObserver(LifecycleTracker.getInstance());
        createNotificationChannel();

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));


        recyclerView = findViewById(R.id.recycler_view);
        notificationList = new ArrayList<>();
        mAdapter = new NotificationAdapter(this, notificationList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);


        fetchNotifications();

    }

    /**
     * fetches json by making http calls
     */
    private void fetchNotifications() {
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the notifications! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<Notification> items = new Gson().fromJson(response.toString(), new TypeToken<List<Notification>>() {
                        }.getType());

                        // adding notifications to notifications list
                        notificationList.clear();
                        notificationList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
        }
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onNotificationSelected(Notification notification) {
        Toast.makeText(getApplicationContext(), "Selected: " + notification.getName() + ", " + notification.getPhone(), Toast.LENGTH_LONG).show();
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
                   // mRecyclerAdapter.notifyDataSetChanged();
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
                //name.setText("");
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
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
    }

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

        int numSubstract;
        switch (notificationDailyWeeklyMonthly) {
            case "Days":
                if(numOfDaysWeeksMonths != null) {
                    numSubstract = Integer.valueOf(numOfDaysWeeksMonths);
                    dateOfNotification.add(Calendar.DAY_OF_YEAR, -numSubstract);
                }
                break;
            case "Weeks":
                if(numOfDaysWeeksMonths != null) {
                    numSubstract = Integer.valueOf(numOfDaysWeeksMonths);
                    numSubstract *= 7;
                    dateOfNotification.add(Calendar.DAY_OF_YEAR, -numSubstract);
                }
                break;
            case "Months":
                if(numOfDaysWeeksMonths != null) {
                    numSubstract = Integer.valueOf(numOfDaysWeeksMonths);
                    dateOfNotification.add(Calendar.MONTH, -numSubstract);
                }
                break;
        }

        Calendar todayDate = Calendar.getInstance();

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
        long notificationDelayInDays = TimeUnit.DAYS.convert(dateDifferenceInMillis, TimeUnit.MILLISECONDS);
        
        // add this to work manager as delay - alebo nastav alarm o tolko dni neskor
        notificationDelayInDays += 1;




        //startWorkerInitConfig(notificationDelayInDays);

        DatabaseNotifications datebaseNotifications = new DatabaseNotifications(MainActivity.this);
        datebaseNotifications.addEvent("", "",
                "", System.currentTimeMillis(), System.currentTimeMillis(), true );

      /*  DatabaseNotifications.getDatabaseNotifications(MainActivity.this).addEvent(name, BirthdayDate,
                todayDate.toString(), System.currentTimeMillis(), notificationDelayInDays, isEmailNotification );
*/

    /*        //generate email if clicked ?
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","abc@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));*/
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    /**
     * Method to start workmanager to schedule flush of initial config.
     *
     * @param timeInDays , given time when the background work should be executed.
     */
    public static void startWorkerInitConfig(long timeInDays){
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
