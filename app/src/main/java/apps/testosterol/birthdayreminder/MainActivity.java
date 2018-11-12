package apps.testosterol.birthdayreminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LifecycleOwner{

    private static final String TAG = MainActivity.class.getSimpleName();

    Button addReminder, random;
    Animation fab_open, fab_close;
    boolean isFabOpen = false;
    FloatingActionButton add, addNotification;
    Dialog dialog;
    EditText birthDay, name;
    int mYear, mMonth, mDay;

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
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.create_reminder_layout);
        dialog.show();

        birthDay = (dialog.findViewById(R.id.birthdaydate));
        name = (dialog.findViewById(R.id.name));
        addNotification = (dialog.findViewById(R.id.AddNotification));

        birthDay.setFocusable(false);
        birthDay.setClickable(true);



        addNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView rvContacts = dialog.findViewById(R.id.recyclerView);

                ArrayList<Contact> contacts;
                // Initialize contacts
                contacts = Contact.createContactsList(20);
                // Create adapter passing in the sample user data
                ContactsAdapter adapter = new ContactsAdapter(contacts);
                // Attach the adapter to the recyclerview to populate items
                rvContacts.setAdapter(adapter);
                // Set layout manager to position the items
                rvContacts.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                // That's all!
            }
        });
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    final InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                    name.clearFocus();
                }
            }
        });
        birthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
