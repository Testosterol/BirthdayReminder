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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Gravity;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import apps.testosterol.birthdayreminder.Notification.Notification;
import apps.testosterol.birthdayreminder.Notification.NotificationAdapter;
import apps.testosterol.birthdayreminder.Database.DatabaseNotifications;
import apps.testosterol.birthdayreminder.Tracking.LifecycleTracker;
import apps.testosterol.birthdayreminder.Util.MyDividerItemDecoration;

import static android.widget.Toast.makeText;
import static apps.testosterol.birthdayreminder.Util.Util.dpToPx;

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
    RecyclerView mRecyclerView;
    NotificationRecyclerViewAdapter mRecyclerAdapter;
    ArrayList<Notification> myList = new ArrayList<>();

    private RecyclerView recyclerView;
    ArrayList<Notification> notificationList = new ArrayList<>();
    private NotificationAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLifecycle().addObserver(LifecycleTracker.getInstance());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);




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

        //fetchNotifications();

        JSONArray jsonArray = DatabaseNotifications.getDatabaseNotifications(this).getNotifications("notifications");

        List<Notification> items = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Notification>>() {
        }.getType());

        notificationList.clear();
        notificationList.addAll(items);

        // refreshing recycler view
        mAdapter.notifyDataSetChanged();



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
        makeText(getApplicationContext(), "Selected: " + notification.getName() + ", " + notification.getBirthday(), Toast.LENGTH_LONG).show();
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
        mRecyclerAdapter = new NotificationRecyclerViewAdapter(this, myList);
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
                    mRecyclerAdapter.notifyData(myList, birthDay.getText().toString(), name.getText().toString());
                }else{
                    Toast toast = makeText(MainActivity.this, "Please fill Birthday and Name first", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, dpToPx(70));
                    toast.show();
                }
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
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
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




}
