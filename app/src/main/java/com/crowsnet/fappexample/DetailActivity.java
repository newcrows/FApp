package com.crowsnet.fappexample;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crowsnet.fappexample.R;
import com.crowsnet.fappexample.Utils;
import com.crowsnet.fappexample.agenda.AgendaItem;
import com.crowsnet.fappexample.core.dialog.DatePickerDialogFragment;
import com.crowsnet.fappexample.core.dialog.EditTextDialogFragment;
import com.crowsnet.fappexample.core.dialog.SingleChoiceDialogFragment;
import com.crowsnet.fappexample.core.dialog.TimePickerDialogFragment;
import com.crowsnet.fappexample.core.exception.DetailActivityException;
import com.crowsnet.fappexample.core.filter.KeyFilter;
import com.crowsnet.fappexample.core.pojo.DateItem;
import com.crowsnet.fappexample.core.pojo.ItemType;
import com.crowsnet.fappexample.core.provider.DateProvider;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements DateProvider.DateListener<AgendaItem>, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, SingleChoiceDialogFragment.SingleChoiceListener, EditTextDialogFragment.EditListener {

    public static final String TAG = "DetailActivity";
    public static final String EXTRA_CREATE_MODE = TAG + ".createMode";
    public static final String EXTRA_ITEM_KEY = TAG + ".itemKey";

    private static final String NO_ITEM_KEY = "Intent contains no EXTRA_ITEM_KEY or the key is null";

    private DateProvider dateProvider;
    private KeyFilter keyFilter;

    private AgendaItem agendaItem;
    private boolean isInEditMode, isInCreateMode;

    private TextView dateStripTV, timeStripTV, labelStripTV, durationStripTV, freeTextStripTV;
    private TextView dateTV, timeTV, labelTV, durationTV, freeTextTV;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;

    private DialogFragment dialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isInCreateMode = getIntent().getBooleanExtra(EXTRA_CREATE_MODE, false);

        dateProvider = DateProvider.getInstance();
        keyFilter = makeKeyFilter();

        initFab();

        initViews();

        if (isInCreateMode) {
            agendaItem = new AgendaItem();
            findViewById(R.id.fab).setEnabled(false);
            switchMode();
            bindViews(agendaItem);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        dateProvider.addDateListener(this, keyFilter);
        if (agendaItem == null)
            dateProvider.request(this);
    }

    @Override
    public void onStop() {
        dateProvider.removeDateListener(this);

        super.onStop();
    }

    @Override
    public void onPause() {
        if (dialogFragment != null)
            dialogFragment.dismiss();

        super.onPause();
    }

    @Override
    public void onPrepare(int flag) {
        //nothing to do here
    }

    @Override
    public List<AgendaItem> onFormat(List<DateItem> dateItems, Map<String, ItemType> itemTypes, int flag) {
        return AgendaItem.listFrom(dateItems, itemTypes);
    }

    @Override
    public void onPublish(List<AgendaItem> formattedDates, int flag) {
        if (formattedDates.size() == 0) {
            finish();
            return;
        }

        agendaItem = formattedDates.get(0);
        bindViews(agendaItem);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTimeInMillis(Utils.unixToMillis(agendaItem.timestamp));

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (agendaItem.dateItem != null)
            updateItemTimestamp(calendar, agendaItem.dateItem);
        else {
            agendaItem.timestamp = Utils.millisToUnix(calendar.getTimeInMillis());
            agendaItem.date = AgendaItem.makeDate(calendar);
            check();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTimeInMillis(Utils.unixToMillis(agendaItem.timestamp));

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        if (agendaItem.dateItem != null)
            updateItemTimestamp(calendar, agendaItem.dateItem);
        else {
            agendaItem.timestamp = Utils.millisToUnix(calendar.getTimeInMillis());
            agendaItem.time = AgendaItem.makeTime(calendar);
            check();
        }
    }

    @Override
    public void onChoiceSet(int which) {
        ItemType type = dateProvider.listTypes().get(which);

        if (agendaItem.dateItem != null) {
            agendaItem.dateItem.setType(type.getKey());
            dateProvider.updateItem(agendaItem.dateItem);
        } else {
            agendaItem.typeKey = type.getKey();
            check();
        }
    }

    @Override
    public void onEdited(String text) {
        if (agendaItem.dateItem != null) {
            agendaItem.dateItem.setText(text);
            dateProvider.updateItem(agendaItem.dateItem);
        } else {
            agendaItem.text = text;
            check();
        }
    }

    private void check() {
        bindViews(agendaItem);
        Log.d(TAG, "check() => " + agendaItem.timestamp + ", " + agendaItem.typeKey + " => complete=" + agendaItem.isComplete());
        if (agendaItem.isComplete()) {
            Button btn = findViewById(R.id.createButton);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateItem dateItem = new DateItem(agendaItem.timestamp, agendaItem.typeKey, agendaItem.text);

                    dateProvider.removeDateListener(DetailActivity.this);
                    dateProvider.addItem(dateItem);
                    finish();
                }
            });

        } else {
            findViewById(R.id.createButton).setVisibility(View.GONE);
        }
    }

    private void updateItemTimestamp(Calendar calendar, DateItem dateItem) {
        dateItem.setTimestamp(Utils.millisToUnix(calendar.getTimeInMillis()));

        dateProvider.updateItem(dateItem);
    }

    private void switchMode() {
        FloatingActionButton fab = findViewById(R.id.fab);
        nestedScrollView.removeAllViews();
        if (isInEditMode) {
            Utils.inflate(R.layout.content_detail_noedit, nestedScrollView);
            isInEditMode = false;
            fab.setRotation(0);
        } else {
            Utils.inflate(R.layout.content_detail_edit, nestedScrollView);
            isInEditMode = true;
            fab.setRotation(45);
        }
        initViews();

        if (agendaItem != null)
            bindViews(agendaItem);
        else
            dateProvider.request(this);
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchMode();
            }
        });
    }

    private void initViews() {
        dateStripTV = findViewById(R.id.dateStripTV);
        timeStripTV = findViewById(R.id.timeStripTV);
        labelStripTV = findViewById(R.id.labelStripTV);
        durationStripTV = findViewById(R.id.durationStripTV);
        freeTextStripTV = findViewById(R.id.freeTextStripTV);

        dateTV = findViewById(R.id.dateTV);
        timeTV = findViewById(R.id.timeTV);
        labelTV = findViewById(R.id.labelTV);
        durationTV = findViewById(R.id.durationTV);
        freeTextTV = findViewById(R.id.freeTextTV);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        nestedScrollView = findViewById(R.id.nestedScrollView);
    }

    private void bindViews(AgendaItem item) {
        int color = item.col;
        Utils.setTextAndColor(dateTV, item.date, color);
        Utils.setTextAndColor(timeTV, item.time, color);
        Utils.setTextAndColor(labelTV, item.label, color);
        Utils.setTextAndColor(durationTV, item.duration, color);
        Utils.setTextAndColor(freeTextTV, item.text == null ? "-" : item.text, color);

        Utils.setColor(dateStripTV, color);
        Utils.setColor(timeStripTV, color);
        Utils.setColor(labelStripTV, color);
        Utils.setColor(durationStripTV, color);
        Utils.setColor(freeTextStripTV, color);

        collapsingToolbarLayout.setBackgroundColor(item.bk);
        nestedScrollView.setBackgroundColor(item.bk);

        setTitle(item.label);

        if (isInEditMode) {
            bindListeners();
        }
    }

    private void bindListeners() {
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment fragment = new DatePickerDialogFragment();
                fragment.setTimestamp(agendaItem.timestamp == 0 ? Utils.millisToUnix(Calendar.getInstance(Locale.GERMANY).getTimeInMillis()) : agendaItem.timestamp);
                fragment.setListener(DetailActivity.this);

                showDialog(fragment);
            }
        });

        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment fragment = new TimePickerDialogFragment();
                fragment.setTimestamp(agendaItem.timestamp);
                fragment.setListener(DetailActivity.this);

                showDialog(fragment);
            }
        });

        labelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ItemType> types = dateProvider.listTypes();

                String[] choices = new String[types.size()];
                int selected = 0;
                for (int c = 0; c < types.size(); c++) {
                    String label = types.get(c).getLabel();
                    choices[c] = label;
                    if (label.equals(agendaItem.label))
                        selected = c;
                }

                SingleChoiceDialogFragment fragment = new SingleChoiceDialogFragment();
                fragment.setSingleChoiceItems(choices, selected);
                fragment.setListener(DetailActivity.this);

                showDialog(fragment);
            }
        });

        freeTextTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDialogFragment fragment = new EditTextDialogFragment();
                fragment.setText(agendaItem.text);
                fragment.setListener(DetailActivity.this);

                showDialog(fragment);
            }
        });
    }

    private void showDialog(DialogFragment fragment) {
        fragment.show(getSupportFragmentManager(), "picker");
        dialogFragment = fragment;
    }

    private KeyFilter makeKeyFilter() {
        String itemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);
        if (itemKey == null && !isInCreateMode)
            throw new DetailActivityException(NO_ITEM_KEY);

        return new KeyFilter(itemKey);
    }
}
