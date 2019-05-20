package apps.testosterol.birthdayreminder.Main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import apps.testosterol.birthdayreminder.BroadcastReceiver.AlarmWakeUpBroadcastReceiver;
import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.R;
import apps.testosterol.birthdayreminder.Reminder.Adapters.MainScreenRemindersAdapter;
import apps.testosterol.birthdayreminder.Reminder.Reminder;
import apps.testosterol.birthdayreminder.Util.MyDividerItemDecoration;
import apps.testosterol.birthdayreminder.Util.RecyclerItemTouchHelper;
import apps.testosterol.birthdayreminder.Util.Util;
import apps.testosterol.birthdayreminder.ViewModel.ReminderViewModel;

import static android.widget.Toast.makeText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements MainScreenRemindersAdapter.MainScreenRemindersAdapterListener,
                                                        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL_ID = "notifications";

    Animation fab_open, fab_close;
    boolean isFabOpen = false;
    FloatingActionButton add;
    RecyclerView allRemindersRecyclerView;
    List<Reminder> allRemindersList = new ArrayList<>();
    private MainScreenRemindersAdapter mainScreenAdapter;
    private ConstraintLayout constraintLayout;
    private View mainFragmentView;
    private ReminderViewModel reminderViewModel;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isFabOpen = false;
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);

        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayUseLogoEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(R.string.toolbar_title);

        this.mainFragmentView = view;

        setHasOptionsMenu(true);

        Util.dontCoverTopOfTheScreenWithApp(view, getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Util.createNotificationChannel(getActivity(), CHANNEL_ID);
        }

        add = view.findViewById(R.id.Add);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.add_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.add_close);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(Objects.requireNonNull(getView()))
                        .navigate(R.id.action_mainFragment_to_createReminderFragment);
            }
        });


        allRemindersRecyclerView = view.findViewById(R.id.recycler_view);
        allRemindersList = new ArrayList<>();
        mainScreenAdapter = new MainScreenRemindersAdapter(getContext(), allRemindersList, this);

        //Setting up recyclerView with Adapter
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        allRemindersRecyclerView.setLayoutManager(mLayoutManager);
        allRemindersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        allRemindersRecyclerView.addItemDecoration(new MyDividerItemDecoration(Objects.requireNonNull(getContext()),
                DividerItemDecoration.VERTICAL, 36));
        allRemindersRecyclerView.setAdapter(mainScreenAdapter);

        allRemindersList.clear();

        allRemindersList.addAll(ReminderDatabase.getInstance(getContext()).daoAccess().getAllReminders());

        // refreshing recycler view
        mainScreenAdapter.notifyDataSetChanged();

        constraintLayout = view.findViewById(R.id.constraint_layout);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(allRemindersRecyclerView);

        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);

        reminderViewModel.getAllReminders().observe(getActivity(), new Observer<List<Reminder>>() {
            @Override
            public void onChanged(@Nullable List<Reminder> reminders) {
                mainScreenAdapter.setMessages(reminders);
            }
        });



    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MainScreenRemindersAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = allRemindersList.get(viewHolder.getAdapterPosition()).getReminderName();

            // backup of removed item for undo purpose
            final Reminder deletedItem = allRemindersList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();



            Log.d("RemovingOnSwipe", "deletedIndex: " + deletedIndex);

            Log.d("RemovingOnSwipe", "AllReminderList.getAdapterPosition.getReminderId: "
                    + allRemindersList.get(viewHolder.getAdapterPosition()).get_reminderId());

            Log.d("RemovingOnSwipe", "ID based on name from db" +
                    ReminderDatabase.getInstance(getContext()).daoAccess().getReminderIdBasedOnName(name));

            //todo: remove reminder from db and stop alarm manager for this specific intent


            cancelAlarm(getContext(), allRemindersList.get(viewHolder.getAdapterPosition()).get_reminderId());


            ReminderDatabase.getInstance(getContext()).daoAccess().removeSpecificReminder(deletedIndex);

            // remove the item from recycler view
            mainScreenAdapter.removeItem(viewHolder.getAdapterPosition());


            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(constraintLayout, name + " removed from reminders!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //todo: put back the notification into db and set the alarm manager again

                    // undo is selected, restore the deleted item
                    mainScreenAdapter.restoreItem(deletedItem, deletedIndex);

                    // refreshing recycler view
                    mainScreenAdapter.notifyDataSetChanged();

                    Log.d("testReceiver", "karol id: " +  deletedItem.get_reminderId());

                    renewTheAlarm(getContext(), deletedItem.getNotificationDateInMillis(), deletedItem.get_reminderId());
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public void cancelAlarm(Context context, long uniqueReminderId) {
        Intent i = new Intent(context, AlarmWakeUpBroadcastReceiver.class);
        PendingIntent pendingIntent =  (PendingIntent.getBroadcast(context, (int) uniqueReminderId, i, PendingIntent.FLAG_ONE_SHOT));

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(pendingIntent);
        }
    }

    public void renewTheAlarm(Context context, long notificationDateInMillis, long uniqueReminderId) {

        Log.d("testReceiver", "karol id renewthealarm: " + uniqueReminderId);

        Intent i = new Intent(context, AlarmWakeUpBroadcastReceiver.class);
        i.putExtra("reminder_id", uniqueReminderId);

        Log.d("testReceiver", "karol id renewthealarm from intent: " + (Objects.requireNonNull(i.getExtras())).get("reminder_id"));

        PendingIntent pendingIntent =  (PendingIntent.getBroadcast(context, (int) uniqueReminderId, i, 0));

        Calendar c = Calendar.getInstance();

        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (aManager != null) {
            aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 10000, pendingIntent );  // todo: get rid of additional time
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
        }
        searchView.setMaxWidth(Integer.MAX_VALUE);



        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mainScreenAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mainScreenAdapter.getFilter().filter(query);
                return false;
            }
        });
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
            Navigation.findNavController(mainFragmentView).navigate(R.id.action_mainFragment_to_settingsFragment);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onReminderSelected(Reminder reminder) {
        makeText(getContext(), "Selected: " + reminder.getReminderName() + ", " + reminder.getReminderBirthdayDate() + ", id:"
                        + reminder.get_reminderId(), Toast.LENGTH_LONG).show();
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
