package apps.testosterol.birthdayreminder.Reminder.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.navigation.Navigation;
import apps.testosterol.birthdayreminder.Main.MainActivity;
import apps.testosterol.birthdayreminder.R;
import apps.testosterol.birthdayreminder.Reminder.Adapters.CreateReminderAdapter;
import apps.testosterol.birthdayreminder.Reminder.Reminder;
import apps.testosterol.birthdayreminder.Util.Util;

import static android.widget.Toast.makeText;
import static apps.testosterol.birthdayreminder.Util.Util.dpToPx;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateReminderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateReminderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = CreateReminderFragment.class.getSimpleName();
    private static final int IMG_RESULT = 1;

    EditText birthDay, name;
    FloatingActionButton addNotification;

    String path;

    RecyclerView mRecyclerView;
    CreateReminderAdapter mRecyclerAdapter;
    ArrayList<Reminder> myList = new ArrayList<>();

    ImageView profilePicture;

    private View createReminderFragmentView;

    int mYear, mMonth, mDay;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CreateReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateReminderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateReminderFragment newInstance(String param1, String param2) {
        CreateReminderFragment fragment = new CreateReminderFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar_create_notification);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);

        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayUseLogoEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(Objects.requireNonNull(getView())).popBackStack();
            }
        });

        this.createReminderFragmentView = view;
        setHasOptionsMenu(true);

        Util.dontCoverTopOfTheScreenWithApp(view, getActivity());

        profilePicture = view.findViewById(R.id.profilePicture);

        path = Util.getURLForResource(R.drawable.birtdhaycake);

        Glide.with(Objects.requireNonNull(getContext()))
                .load(path)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);

        birthDay = (view.findViewById(R.id.birthdaydate));
        name = (view.findViewById(R.id.name));
        addNotification = (view).findViewById(R.id.AddNotification);
        birthDay.setFocusable(false);
        birthDay.setClickable(true);

        myList.clear();

        mRecyclerView = (Objects.requireNonNull(getView()).findViewById(R.id.recyclerView));
        mRecyclerAdapter = new CreateReminderAdapter(getContext(), myList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        addNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!birthDay.getText().toString().equals("Birthday date") && !birthDay.getText().toString().equals("")
                        && !name.getText().toString().equals("") && !name.getText().toString().equals("Name")) {
                    Reminder mLog = new Reminder();
                    myList.add(mLog);
                    mRecyclerAdapter.notifyData(myList, birthDay.getText().toString(), name.getText().toString(), path);
                }else{
                    Toast toast = makeText(getContext(), "Please fill Birthday and Name first", Toast.LENGTH_LONG);
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
                    final InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(MainActivity.INPUT_METHOD_SERVICE);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
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

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserImage();
            }
        });
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

            //Log.d("KarolTest", "remidner id: " + reminderId);
            //ReminderDatabase.getInstance(getActivity()).daoAccess().updateReminderImage(reminderId, path);
            //sotre into db
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_search);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
            Navigation.findNavController(createReminderFragmentView).navigate(R.id.action_createReminderFragment_to_settingsFragment);
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


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
