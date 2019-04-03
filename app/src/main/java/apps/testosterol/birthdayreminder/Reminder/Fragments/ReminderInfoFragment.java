package apps.testosterol.birthdayreminder.Reminder.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.navigation.Navigation;
import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.R;
import apps.testosterol.birthdayreminder.Util.Util;

import static apps.testosterol.birthdayreminder.Util.Util.dpToPx;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReminderInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReminderInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReminderInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = ReminderInfoFragment.class.getName();
    private static final int IMG_RESULT = 1;

    EditText name, birthday, notificationDate;
    ImageView profilePicture;
    Integer id;
    int mYear, mMonth, mDay;
    String path;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String reminderName;
    private String reminderImage;
    private String reminderBirthdayDate;
    private long reminderNotificationDateInMillis;
    private boolean reminderEventEmail;
    private int reminderId;

    private View reminderInfoFragmentView;

    private OnFragmentInteractionListener mListener;

    public ReminderInfoFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReminderInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderInfoFragment newInstance(String param1, String param2) {
        ReminderInfoFragment fragment = new ReminderInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //no idea what these are for, auto-generated
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            // get bundled arguments from MainScreenRemindersAdapter
            reminderName = getArguments().getString("reminderName");
            reminderImage = getArguments().getString("reminderImage");
            reminderBirthdayDate = getArguments().getString("reminderBirthdayDate");
            reminderNotificationDateInMillis = getArguments().getLong("reminderNotificationDateInMillis", 0);
            reminderEventEmail = getArguments().getBoolean("reminderEventEmail");
            reminderId = getArguments().getInt("reminderId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reminder_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Util.dontCoverTopOfTheScreenWithApp(view, getActivity());

        Toolbar toolbar = view.findViewById(R.id.toolbar_reminder_info);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);

        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayUseLogoEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(Objects.requireNonNull(getView())).popBackStack();
            }
        });

        setHasOptionsMenu(true);

        this.reminderInfoFragmentView = view;

        profilePicture = view.findViewById(R.id.profilePicture);
        name = view.findViewById(R.id.nameNotification);
        birthday = view.findViewById(R.id.birthdayDateNotification);
        notificationDate = view.findViewById(R.id.notificationDate);

        name.setText(reminderName);
        birthday.setText(String.format("Birthday date: %s", reminderBirthdayDate));

        Calendar notificationDateFromArgs = Calendar.getInstance();
        notificationDateFromArgs.setTimeInMillis(reminderNotificationDateInMillis);

        String formattedNotificationDate = String.format(Locale.ENGLISH, "%02d-%02d-%d",
                notificationDateFromArgs.get(Calendar.DAY_OF_MONTH),
                (notificationDateFromArgs.get(Calendar.MONTH) + 1),
                notificationDateFromArgs.get(Calendar.YEAR) );

        notificationDate.setText(String.format("Reminder date: %s", formattedNotificationDate));

        id = reminderId;

        String imagePath = reminderImage;

        Glide.with(this)
                .load(imagePath)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserImage();
            }
        });

        notificationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Reminder date edit click");
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                setNewNotification(year, monthOfYear, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Birthday edit click");
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format(Locale.ENGLISH, "%02d-%02d-%d", dayOfMonth, (monthOfYear + 1),year );
                                birthday.setText(formattedDate);

                                // todo: update date in database - calculate new reminder ?

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setNewNotification(int year, int month, int day) {
        Calendar todayDate = Calendar.getInstance();
        Calendar notificationDateCalendar = Calendar.getInstance();
        notificationDateCalendar.set(year, month, day);

        long dateOfNotificationMillis = notificationDateCalendar.getTimeInMillis();
        long todayDateMillis = todayDate.getTimeInMillis();
        long dateDifferenceInMillis = dateOfNotificationMillis - todayDateMillis;
        long notificationDelayInDays = TimeUnit.MILLISECONDS.toDays(dateDifferenceInMillis);

        if(notificationDelayInDays < 0){
            Toast toast = Toast.makeText(getContext(), R.string.past_date_year, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, dpToPx(70));
            toast.show();
        }else{

            Util.rescheduleAlarmToUsersSpecificDate(Objects.requireNonNull(getContext()), notificationDateCalendar, id);

            String formattedDate = String.format(Locale.ENGLISH, "%02d-%02d-%d", day, (month + 1),year );
            notificationDate.setText(formattedDate);

            //hm todo: reschedule reminder based on id
        }
    }

    private void getUserImage(){
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        if ((ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    PERMISSIONS, 1);
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMG_RESULT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Uri selectedImage = data.getData();
            path = Util.getPathToImageFromUri(selectedImage, getContext());
            Glide.with(this)
                    .load(path)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);

            ReminderDatabase.getInstance(getActivity()).daoAccess().updateReminderImage(reminderId, path);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_search);
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

        if(id == R.id.action_settings){
            Navigation.findNavController(reminderInfoFragmentView).navigate(R.id.action_reminderInfo_to_settingsFragment);
            return true;
        }

        if(id == R.id.action_buffer_field){
            return true;
        }

        if(id == R.id.action_buffer_field2){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
