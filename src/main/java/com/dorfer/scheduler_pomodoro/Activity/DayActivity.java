package com.dorfer.scheduler_pomodoro.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import com.dorfer.scheduler_pomodoro.DateConverter;
import com.dorfer.scheduler_pomodoro.DateFormatter;
import com.dorfer.scheduler_pomodoro.Dialogs.DatePickerFragment;
import com.dorfer.scheduler_pomodoro.R;
import com.dorfer.scheduler_pomodoro.RVAdapter;
import com.dorfer.scheduler_pomodoro.Database.Task;
import com.dorfer.scheduler_pomodoro.Database.TaskDatabase;

public class DayActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerFragment.NoticeDialogListener {

    public final static String CURRENT_DAY_TAG = "current day";

    private final static String TAG = "myTag " + DayActivity.class.toString();
    private TaskDatabase database;
    private ActionMenuView menuSetDate;
    private ActionMenuView menuStartPomodoro;
    private FloatingActionButton buttonAddTask;

    private Calendar currentDay; //current day for activity

    private RecyclerView rv;
    private RVAdapter rvAdapter;
    private RecyclerView.LayoutManager rvLayourManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        //toolbar settings
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        currentDay = Calendar.getInstance(); //firstly today

        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(DateFormatter.dateToString(currentDay,
                DateFormatter.ENGLISH));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        menuSetDate = (ActionMenuView) myToolbar.findViewById(R.id.set_date);
        menuSetDate.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        menuStartPomodoro = (ActionMenuView) myToolbar.findViewById(R.id.pomodoro_timer);
        menuStartPomodoro.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        //bind and set on click listener for buttonAddTask btn
        buttonAddTask = (FloatingActionButton) findViewById(R.id.button_add_task);
        buttonAddTask.setOnClickListener(this);

        //bind recycler view
        initRecyclerView();

        //get database in main thread(incorrect)
    /*    database = TaskDatabase.getTaskDatabase(this);
        List<Task> tasks = database.taskDao().getAllTasks();*/

        //specify an adapter
     //   rvAdapter = new RVAdapter(tasks);
        setDayTasksInAdapter(currentDay);
        rv.setAdapter(rvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.calendar: { //calendar actions
                Log.d(TAG, "main_menu calendar clicked ");
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            }
            case R.id.start_pomodoro: { //start pomodoro action
                Log.d(TAG, "main_menu pomodoro clicked");
                Intent intent = new Intent(this, PomodoroActivity.class);
                startActivityForResult(intent, 2);
                break;
            }
        }
        return true;
    }

    @Override
    public void onDialogPositiveClick(int day, int month, int year) {
        currentDay.set(year, month, day);
        Log.d(TAG, "onDialogPositiveClick\n" + currentDay.toString());
        getSupportActionBar().setTitle(DateFormatter.dateToString(day, month, year,
                DateFormatter.ENGLISH));
        setDayTasksInAdapter(currentDay); //refresh recycler view
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.button_add_task: { //add btn
                Log.d(TAG, "add task clicked");
                Toast.makeText(DayActivity.this, R.string.add_btn, Toast.LENGTH_SHORT)
                        .show();

                Intent intent = new Intent(this, AddTaskActivity.class);
                intent.putExtra(CURRENT_DAY_TAG, DateConverter.fromDate(currentDay));
                startActivityForResult(intent,
                        0);

                break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) { //from AddTaskActivity
            setDayTasksInAdapter(currentDay);
        }
    }

    private void initRecyclerView() {
        rv = (RecyclerView)findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv.setHasFixedSize(true);

        //use linear layout manager
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setDayTasksInAdapter(Calendar day) {
        TaskDatabase database = TaskDatabase.getTaskDatabase(this);
        List<Task> dayTasks = database.taskDao().getDayTasks(day);

        for (Task task : dayTasks) {
            Log.d(TAG, "" + task.getStartTime());
        }

        if (rvAdapter == null) {
            rvAdapter = new RVAdapter(dayTasks);
        } else {
            rvAdapter.setItems(dayTasks);
        }
    }
}
