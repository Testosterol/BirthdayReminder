package apps.testosterol.birthdayreminder.Main;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import androidx.navigation.Navigation;
import apps.testosterol.birthdayreminder.Database.ReminderDatabase;
import apps.testosterol.birthdayreminder.R;
import apps.testosterol.birthdayreminder.Reminder.Adapters.MainScreenRemindersAdapter;
import apps.testosterol.birthdayreminder.Util.RecyclerItemTouchHelper;
import apps.testosterol.birthdayreminder.Reminder.Reminder;
import apps.testosterol.birthdayreminder.Util.MyDividerItemDecoration;
import apps.testosterol.birthdayreminder.Util.Util;

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
    private static final String TAG = MainFragment.class.getSimpleName();

    Button addReminder, random;
    Animation fab_open, fab_close;
    boolean isFabOpen = false;
    FloatingActionButton add;
    RecyclerView allRemindersRecyclerView;
    ArrayList<Reminder> allRemindersList = new ArrayList<>();
    private MainScreenRemindersAdapter mainScreenAdapter;
    private SearchView searchView;
    private ConstraintLayout constraintLayout;
    private View mainFragmentView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void openMenu() {
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

       /* Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);*/
        setHasOptionsMenu(true);

        Util.dontCoverTopOfTheScreenWithApp(view, getActivity());

        add = view.findViewById(R.id.Add);
        addReminder = view.findViewById(R.id.AddReminder);
        random = view.findViewById(R.id.RandomButton);

        addReminder.setVisibility(View.INVISIBLE);
        random.setVisibility(View.INVISIBLE);

        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.add_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.add_close);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenu();
            }
        });

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(Objects.requireNonNull(getView()))
                        .navigate(R.id.action_mainFragment_to_createReminderFragment);
            }
        });

        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        allRemindersList.addAll(ReminderDatabase.getInstance(getContext()).daoAccess().fetchAllNotifications());

        // refreshing recycler view
        mainScreenAdapter.notifyDataSetChanged();

        constraintLayout = view.findViewById(R.id.constraint_layout);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(allRemindersRecyclerView);

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

            // remove the item from recycler view
            mainScreenAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(constraintLayout, name + " removed from reminders!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mainScreenAdapter.restoreItem(deletedItem, deletedIndex);

                    // refreshing recycler view
                    mainScreenAdapter.notifyDataSetChanged();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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
        makeText(getContext(), "Selected Test Bro: " + reminder.getReminderName() + ", " + reminder.getReminderBirthdayDate(), Toast.LENGTH_LONG).show();
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
