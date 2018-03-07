package com.example.h.analogclock.ui;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.h.analogclock.R;
import com.example.h.analogclock.widget.ClockView;
import com.example.h.analogclock.widget.ClockViewSurface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MyFragment extends Fragment{

    private View returnView;
    private ClockViewSurface mClockViewSurface;
    private ClockView mClockView;
    private Button alarmButton;
    private Button cancelAlarm;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    final static int RQS_1 = 1;
    private Spinner sItems;
    int hour_x;
    int minute_x;
    int z;

    public MyFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        returnView = inflater.inflate(R.layout.fragment_my, container, false);
        alarmButton = (Button)returnView.findViewById(R.id.alarmButton);
        cancelAlarm = (Button)returnView.findViewById(R.id.unsetAlarm);
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Black");
        spinnerArray.add("Red");
        spinnerArray.add("Green");
        spinnerArray.add("Blue");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner)returnView.findViewById(R.id.spinner);
        sItems.setAdapter(adapter);
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Color set to: " + item.toString(), Toast.LENGTH_SHORT).show();
                switch(position) {
                    case 0:
                        mClockViewSurface.changeColor1();
                        break;
                    case 1:
                        mClockViewSurface.changeColor2();
                        break;
                    case 2:
                        mClockViewSurface.changeColor3();
                        break;
                    case 3:
                        mClockViewSurface.changeColor4();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calNow = Calendar.getInstance();
                        Calendar calset = (Calendar)calNow.clone();

                        calset.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calset.set(Calendar.MINUTE,minute);
                        calset.set(Calendar.SECOND,0);
                        calset.set(Calendar.MILLISECOND,0);

                        if(calset.compareTo(calNow) <= 0){
                            calset.add(Calendar.DATE, 1);
                        }
                        Toast.makeText(getContext(),"Alarm Set",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getContext(),AlarmReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(getContext(),RQS_1,intent,0);
                        alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calset.getTimeInMillis(),pendingIntent);
                        cancelAlarm.setVisibility(View.VISIBLE);
                    }
                },hour,minute,false);
                mTimePicker.setTitle("Pick Time");
                mTimePicker.show();

            }
        });

        cancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alarmManager.cancel(pendingIntent);
                Toast.makeText(getContext(),"Alarm Unset", Toast.LENGTH_SHORT).show();
                Intent i1=new Intent(getContext(),RingtonePlayer.class);
                getActivity().stopService(i1);

            }
        });
        return returnView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeContent();
    }

    private void initializeContent(){
        mClockViewSurface = (ClockViewSurface) returnView.findViewById(R.id.clockView);

    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_12_hours:
                mClockViewSurface.setClockFormat(12);
                return true;
            case R.id.action_24_hours:
                mClockViewSurface.setClockFormat(24);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
