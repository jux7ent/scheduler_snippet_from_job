package com.dorfer.scheduler_pomodoro;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import com.dorfer.scheduler_pomodoro.Activity.AddTaskActivity;
import com.dorfer.scheduler_pomodoro.Database.Task;
import com.dorfer.scheduler_pomodoro.Database.TaskDatabase;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVHolder> {
    private List<Task> taskList;

    public static class RVHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener {

        public TextView startTimeTV;
        public TextView durationTV;
        public TextView contentTV;
        public CheckBox isCompleteCheckBox;
        private Task mTask;
        private View mView;

        public RVHolder(View view) {
            super(view);
            mView = view;
            startTimeTV = (TextView) view.findViewById(R.id.start_time_view);
            durationTV = (TextView) view.findViewById(R.id.duration_view);
            contentTV = (TextView) view.findViewById(R.id.content_view);
            isCompleteCheckBox = (CheckBox) view.findViewById(R.id.is_complete_checkbox);
        }

        public void bind(Task task) {
            this.mTask = task;
            Log.d("myTag", "\nstart: " + mTask.getContent());

            int duration;
            if (this.mTask.getStartTime() == AddTaskActivity.TIME_NOT_SET) {
                Log.d("myTag", "first text color " + this.mTask.getStartTime());
                startTimeTV.setTextColor(Color.argb(0, 0, 0, 0));

                duration = mTask.getDuration();
            }
            else {
                duration = (mTask.getDuration() + mTask.getStartTime()) % 1440;
                startTimeTV.setTextColor(Color.argb(255, 0, 0, 0));
            }
            if (mTask.getDuration() == AddTaskActivity.TIME_NOT_SET) {
                Log.d("myTag", "second text color " + mTask.getDuration());
                durationTV.setTextColor(Color.argb(0, 0, 0, 0));
                duration = AddTaskActivity.TIME_NOT_SET;
            }
            else {
                durationTV.setTextColor(Color.argb(255, 0, 0, 0));
            }

            Log.d("myTag", "startTime: " + mTask.getStartTime());
            Log.d("myTag", "duration: " + duration);
            Log.d("myTag", "contentTV: " + mTask.getContent() + "\n");

            startTimeTV.setText(RVAdapter.minutesInTimeString(mTask.getStartTime()));
            durationTV.setText(RVAdapter.minutesInTimeString(duration));
            contentTV.setText(mTask.getContent());
            isCompleteCheckBox.setChecked(mTask.isComplete());

            setBackgroundTask(mTask.isComplete());
/*
            if (mTask.isComplete()) {
                contentTV.setPaintFlags(contentTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                        R.color.completed_task));
            }
            else {
                contentTV.setPaintFlags(contentTV.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                        R.color.pending_task));
            } */

            isCompleteCheckBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mTask.setComplete(isChecked);
            updateTask(mTask, mView.getContext());

            /*
            if (isChecked) {
                contentTV.setPaintFlags(contentTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                        R.color.completed_task));
            }
            else {
                contentTV.setPaintFlags(contentTV.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                        R.color.pending_task));
            }*/
            setBackgroundTask(isChecked);
        }

        private void updateTask(final Task task, Context context) {
            TaskDatabase database = TaskDatabase.getTaskDatabase(context);
            database.taskDao().updateTask(task);
        }

        private void setBackgroundTask(boolean isChecked) {
            if (isChecked) {
                contentTV.setPaintFlags(contentTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                        R.color.completed_task));
            }
            else {
                contentTV.setPaintFlags(contentTV.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                if (mTask.getDuration() == AddTaskActivity.TIME_NOT_SET) {
                    mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                            R.color.pending_task));
                }
                else {
                    if (mTask.getStartTime() != AddTaskActivity.TIME_NOT_SET && isOverdue(mTask)) {
                        mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                                R.color.overdue_task));
                    }
                    else {
                        mView.setBackgroundColor(ContextCompat.getColor(mView.getContext(),
                                R.color.pending_task));
                    }
                }

            }
        }

        private boolean isOverdue(Task task) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE),
                    0, 0, 0);

            if ((calendar.compareTo(task.getDate())) < 0) {

                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                int currentMinutes = currentHour * 60 + currentMinute;

                int taskMinutes = task.getStartTime() + task.getDuration();

                return currentMinutes > taskMinutes;
            }
            else {
                return true;
            }
        }
    }

    public static String minutesInTimeString(int minutes) {
        int hours = minutes / 60;
        minutes -= 60 * hours;
/*
        String strHour = hours < 10 ? "0" + hours : "" + hours;
        String minHour = minutes < 10 ? "0" + minutes : "" + minutes; */

        return String.format("%02d:%02d", hours, minutes);
    }

    public void setItems(List<Task> tasks) {
        taskList = tasks;
        notifyDataSetChanged();
    }

    public void clearItems() {
        taskList.clear();
        notifyDataSetChanged();
    }

    // Provide a suitable constructor
    public RVAdapter(List<Task> tasks) {
        taskList = tasks;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RVAdapter.RVHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new TextView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_holder, parent, false);

        return new RVHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RVHolder holder, int position) {
        holder.bind(taskList.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
