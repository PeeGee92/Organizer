package peegee.fullorganizer.service.adapters;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.alarm.AddAlarm;
import peegee.fullorganizer.firebase_db.AlarmDB;
import peegee.fullorganizer.service.local_db.AlarmItemDB;
import peegee.fullorganizer.service.local_db.AppDatabase;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    List<AlarmDB> alarmDBList;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    Context appContext;
    AlarmManager alarmManager;
    NotificationManager notificationManager;

    private final View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            AlarmDB item = alarmDBList.get(itemPosition);

            // Load this item data in the next activity
            String tempId = item.getAlarmId();
            Intent intent = new Intent(view.getContext(), AddAlarm.class);
            intent.putExtra("ALARM_ID", tempId);
            view.getContext().startActivity(intent);
        }
    };

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView =recyclerView;
    }

    public AlarmAdapter(List<AlarmDB> alarmDBList, Context appContext,
                        AlarmManager alarmManager, NotificationManager notificationManager) {

        this.alarmDBList = alarmDBList;
        this.appContext = appContext;
        this.alarmManager = alarmManager;
        this.notificationManager = notificationManager;
    }

    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        view.setOnClickListener(myOnClickListener);
        adapter = this;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlarmAdapter.ViewHolder holder, final int position) {
        final AlarmDB temp = alarmDBList.get(position);

        holder.swOnOff.setChecked(temp.alarmOn);
        String time, amPm, h, m;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp.alarmDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        // 24 to 12 format
        if (hour > 12) {
            hour -= 12;
            amPm = "PM";
        } else if (hour == 0) {
            hour = 12;
            amPm = "AM";
        } else if (hour == 12){
            amPm = "PM";
        }else{
            amPm = "AM";
        }

        //Display hour fix
        if (hour < 10)
            h = "0" + hour;
        else
            h = Integer.toString(hour);

        //Display minute fix
        if (min < 10)
            m = "0" + min;
        else
            m = Integer.toString(min);

        time = h + ":" + m + " " + amPm;
        holder.tvTime.setText(time);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete Alarm")
                        .setMessage("Are you sure you want to delete this Alarm permanently?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Firebase
                                synchronized (MainActivity.FBLOCK) {
                                    MainActivity.alarmRef.child(temp.getAlarmId()).removeValue();
                                }

                                // Update RecyclerView
                                alarmDBList.remove(position);
                                recyclerView.removeViewAt(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, alarmDBList.size());
                                adapter.notifyDataSetChanged();
                            }})
                        .setNegativeButton("Cancel", null).show();
            }
        });

        holder.swOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int index = MainActivity.alarmsList.indexOf(temp);

                // Check if alarm time has passed
                Date oldDate = temp.alarmDate;
                Calendar oldCalendar = Calendar.getInstance();
                oldCalendar.setTime(oldDate);
                if (oldCalendar.before(Calendar.getInstance())) {
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.set(Calendar.MINUTE, oldCalendar.get(Calendar.MINUTE));
                    newCalendar.set(Calendar.HOUR_OF_DAY, oldCalendar.get(Calendar.HOUR_OF_DAY));
                    if (newCalendar.before(Calendar.getInstance()))
                        newCalendar.add(Calendar.DATE, 1);

                    temp.alarmDate = newCalendar.getTime();
                    MainActivity.alarmsList.get(index).alarmDate = newCalendar.getTime();

                    AddAlarm.cancelOldAndSetNew(temp, newCalendar, appContext, alarmManager, notificationManager);
                }

                MainActivity.alarmsList.get(index).alarmOn = holder.swOnOff.isChecked();


                temp.alarmOn = holder.swOnOff.isChecked();

                // Firebase
                synchronized (MainActivity.FBLOCK) {
                    MainActivity.todoItemRef.child(temp.getAlarmId()).setValue(temp);
                }

                // Local db
                AppDatabase db = Room.databaseBuilder(appContext, AppDatabase.class, "alarms_reset")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                if (holder.swOnOff.isChecked()) {
                    AlarmItemDB tempAlarmDb = new AlarmItemDB(temp.getAlarmId(), temp.getAlarmRequestCode(), temp.alarmDate, false);
                    db.alarmItemDAO().insert(tempAlarmDb);
                }
                else {
                    AlarmItemDB tempAlarmDb = db.alarmItemDAO().getAlarmById(temp.getAlarmId());
                    db.alarmItemDAO().delete(tempAlarmDb);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmDBList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Switch swOnOff;
        TextView tvTime;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            swOnOff = itemView.findViewById(R.id.swOnOff);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
