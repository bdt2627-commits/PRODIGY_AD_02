package com.example.to_dolist;



import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences; // New Import
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson; // New Import
import com.google.gson.reflect.TypeToken; // New Import

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnEditClickListener {

    private EditText taskInput;
    private Button addButton;
    private Button selectDateButton;
    private Button selectTimeButton;
    private TextView dateTimeDisplay;
    private ListView taskList;

    private ArrayList<TaskItem> items;
    private TaskAdapter adapter;

    private Calendar currentCalendar;
    private int editingPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInput = findViewById(R.id.taskInput);
        addButton = findViewById(R.id.addButton);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        dateTimeDisplay = findViewById(R.id.dateTimeDisplay);
        taskList = findViewById(R.id.taskList);

        // Pehle data load karenge
        loadData();

        adapter = new TaskAdapter(this, items);
        adapter.setOnEditClickListener(this);
        taskList.setAdapter(adapter);

        currentCalendar = Calendar.getInstance();
        updateDateTimeDisplay();

        selectDateButton.setOnClickListener(v -> showDatePicker());
        selectTimeButton.setOnClickListener(v -> showTimePicker());
        addButton.setOnClickListener(v -> addItem());

        taskList.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }

    // --- DATA PERSISTENCE METHODS ---

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("task_list", json);
        editor.apply(); // Data save ho gaya
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task_list", null);
        Type type = new TypeToken<ArrayList<TaskItem>>() {}.getType();
        items = gson.fromJson(json, type);

        if (items == null) {
            items = new ArrayList<>();
        }
    }

    // --- EXISTING METHODS UPDATED WITH saveData() ---

    private void addItem() {
        String task = taskInput.getText().toString().trim();
        if (task.isEmpty()) {
            Toast.makeText(this, "Add Task", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.format(Locale.getDefault(), "%02d/%02d/%d",
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR));

        String time = String.format(Locale.getDefault(), "%02d:%02d %s",
                (currentCalendar.get(Calendar.HOUR) == 0) ? 12 : currentCalendar.get(Calendar.HOUR),
                currentCalendar.get(Calendar.MINUTE),
                (currentCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM");

        if (editingPosition == -1) {
            TaskItem newItem = new TaskItem(task, date, time);
            items.add(newItem);
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        } else {
            TaskItem itemToEdit = items.get(editingPosition);
            itemToEdit.setTaskName(task);
            itemToEdit.setDueDate(date);
            itemToEdit.setDueTime(time);
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            editingPosition = -1;
        }

        adapter.notifyDataSetChanged();
        saveData(); // Save changes
        taskInput.setText("");
        currentCalendar = Calendar.getInstance();
        updateDateTimeDisplay();
    }

    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    items.remove(position);
                    adapter.notifyDataSetChanged();
                    saveData(); // Save after delete
                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // --- OTHER UI METHODS (Same as before) ---

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    currentCalendar.set(Calendar.YEAR, year);
                    currentCalendar.set(Calendar.MONTH, month);
                    currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                },
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    currentCalendar.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                currentCalendar.get(Calendar.HOUR_OF_DAY),
                currentCalendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        String dateString = String.format(Locale.getDefault(),
                "%02d/%02d/%d",
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR));

        String timeString = String.format(Locale.getDefault(),
                "%02d:%02d %s",
                (currentCalendar.get(Calendar.HOUR) == 0) ? 12 : currentCalendar.get(Calendar.HOUR),
                currentCalendar.get(Calendar.MINUTE),
                (currentCalendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM");

        dateTimeDisplay.setText(dateString + " @ " + timeString);
        addButton.setText(editingPosition == -1 ? "Done" : "Update");
    }

    @Override
    public void onEditClick(int position) {
        TaskItem taskToEdit = items.get(position);
        editingPosition = position;
        taskInput.setText(taskToEdit.getTaskName());
        addButton.setText("Update");
        Toast.makeText(this, "Edit Mode", Toast.LENGTH_SHORT).show();
    }
}