package com.example.notatnik_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etName;
    private Button btSelectTime, btSelectDate, btSave;
    private int hour, minute, year, month, day;
    private String name, date;
    private DatePickerDialog datePickerDialog;
//    private AlarmManager alarmManager;
//    private PendingIntent pendingIntent;
    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().hide();

        createNotoficationChannel();

        etName = findViewById(R.id.editTextName);
        btSelectTime = findViewById(R.id.buttonSelectTime);
        btSelectDate = findViewById(R.id.buttonDatePicker);
        btSave = findViewById(R.id.btSaveTask);

        btSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(view);
            }
        });

        initDatePicker();
        btSelectDate.setText(getTodayDate());
        btSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                save(view);
                setAlarm();
                Intent intent = new Intent(AddTaskActivity.this, ToDoActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    public void popTimePicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                btSelectTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Ustaw godzinę");
        timePickerDialog.show();
    }

    private void openDatePicker(View view){
        datePickerDialog.show();
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                date = makeDateString(day, month , year);
                btSelectDate.setText(date);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, onDateSetListener, year, month, day);
    }

    private String getTodayDate(){
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        month += 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year){
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month){
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";
        return "JAN";
    }

    private void save(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String stHour = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        date = date + " " + stHour;
        HashMap<String, Object> taskItem = new HashMap<>();
        taskItem.put("data", date);
        taskItem.put("czyWykonane", false);

        db.collection("zadania").document(name).set(taskItem, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(AddTaskActivity.this, "Dodano nowy wpis", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createNotoficationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notiReminderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notiId", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int rightHour(int hour){
        if(hour > 12){
            return hour - 12;
        }
        return hour;
    }

    private void calendarSettings(){
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.MONTH, month);
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
        LocalDateTime l = LocalDateTime.of(year, month, day, hour, minute);
        calendar.setTime(Date.from(l.atZone(ZoneId.systemDefault()).toInstant()));
        System.out.println("Rok "+year);
        System.out.println("Miesiac "+month);
        System.out.println("dzien "+day);
        System.out.println("godzina "+hour);
        System.out.println("minuta "+minute);
        System.out.println("Obecna data " + calendar.getTime());
    }

    private long setMillis(){
        //long timeAtCall = System.currentTimeMillis();
        long timeAtCall = Calendar.getInstance().getTimeInMillis();
        calendarSettings();
        long diff = calendar.getTimeInMillis() - timeAtCall;
//        long inCal = minute * hour * day * month * year * 1000;
//        long diff = inCal - timeAtCall;

        System.out.println("Obecnie: "+timeAtCall);
        System.out.println("Na kalendarzu "+calendar.getTimeInMillis());
        System.out.println("Roznica "+diff);
        return timeAtCall + diff;
    }

    private void setAlarm(){
        Intent intent = new Intent(AddTaskActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddTaskActivity.this, 0, intent, 0);
        //calendarSettings();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeAtCall = System.currentTimeMillis();
        long tenSeconds = 1000 * 10;
        alarmManager.set(AlarmManager.RTC_WAKEUP, setMillis(), pendingIntent);
        Toast.makeText(this, "Przypomnienie ustawione pomyślnie.", Toast.LENGTH_LONG).show();
    }
}