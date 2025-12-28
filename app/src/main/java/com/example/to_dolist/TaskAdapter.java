package com.example.to_dolist;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<TaskItem> {

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    private OnEditClickListener editListener;

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editListener = listener;
    }

    public TaskAdapter(Context context, ArrayList<TaskItem> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskItem task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_task_item, parent, false);
        }

        TextView taskName = convertView.findViewById(R.id.taskName);
        TextView taskDateTime = convertView.findViewById(R.id.taskDateTime);
        TextView editButton = convertView.findViewById(R.id.editButton); // Edit Button

        if (task != null) {
            taskName.setText(task.getTaskName());
            String dateTime = task.getDueDate() + " @ " + task.getDueTime();
            taskDateTime.setText(dateTime);
        }

        editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(position);
            }
        });

        return convertView;
    }
}