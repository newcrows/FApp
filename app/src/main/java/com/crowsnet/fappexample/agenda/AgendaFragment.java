package com.crowsnet.fappexample.agenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.crowsnet.fappexample.R;
import com.crowsnet.fappexample.Utils;
import com.crowsnet.fappexample.DetailActivity;
import com.crowsnet.fappexample.core.pojo.DateItem;
import com.crowsnet.fappexample.core.pojo.ItemType;
import com.crowsnet.fappexample.core.dialog.TypeFilterDialog;
import com.crowsnet.fappexample.core.filter.DateRangeFilter;
import com.crowsnet.fappexample.core.filter.TypeFilter;
import com.crowsnet.fappexample.core.provider.DateProvider;

import java.util.List;
import java.util.Map;

public class AgendaFragment extends Fragment implements DateProvider.DateListener<AgendaItem>, AgendaAdapter.ItemClickListener {

    public static final String TAG = "AgendaFragment";

    private RecyclerView recyclerView;
    private AgendaAdapter agendaAdapter;

    private DateProvider dateProvider;
    private DateRangeFilter dateRangeFilter;

    private TypeFilter typeFilter;
    private AlertDialog typeFilterDialog;

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.agenda, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                showTypeFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle sis) {
        View view = inflater.inflate(R.layout.fragment_agenda, group, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle sis) {
        super.onActivityCreated(sis);

        agendaAdapter = new AgendaAdapter(this);
        recyclerView.setAdapter(agendaAdapter);

        dateProvider = DateProvider.getInstance();
        dateRangeFilter = makeDateRangeFilter(sis);
        typeFilter = makeTypeFilter(sis);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        dateRangeFilter.save(out);
        typeFilter.save(out);

        super.onSaveInstanceState(out);
    }

    @Override
    public void onStart() {
        super.onStart();

        dateProvider.addDateListener(this, dateRangeFilter, typeFilter);
        dateProvider.request(this);
    }

    @Override
    public void onStop() {
        dismissTypeFilterDialog();
        dateProvider.removeDateListener(this);

        super.onStop();
    }

    @Override
    public void onPrepare(int flag) {
        if (flag == DateProvider.FLAG_TYPES)
            typeFilter.setTypes(dateProvider.listTypes());
    }

    @Override
    public List<AgendaItem> onFormat(List<DateItem> dateItems, Map<String, ItemType> itemTypes, int flag) {
        return AgendaItem.listFrom(dateItems, itemTypes);
    }

    @Override
    public void onPublish(List<AgendaItem> formattedDates, int flag) {
        agendaAdapter.setItems(formattedDates);
    }

    @Override
    public void onItemClick(AgendaItem item) {
        Log.d(TAG, "item clicked => " + item.key);

        Bundle extras = new Bundle();
        extras.putString(DetailActivity.EXTRA_ITEM_KEY, item.key);

        Utils.launchActivity(getContext(), DetailActivity.class, extras);
    }

    private void showTypeFilterDialog() {
        typeFilterDialog = TypeFilterDialog.wrap(getContext(), this, typeFilter);

        typeFilterDialog.show();
    }

    private void dismissTypeFilterDialog() {
        if (typeFilterDialog != null && typeFilterDialog.isShowing())
            typeFilterDialog.dismiss();
    }

    private DateRangeFilter makeDateRangeFilter(Bundle sis) {
        DateRangeFilter filter = new DateRangeFilter();
        filter.restore(sis);

        return filter;
    }

    private TypeFilter makeTypeFilter(Bundle sis) {
        TypeFilter typeFilter = new TypeFilter();
        typeFilter.restore(sis);

        return typeFilter;
    }
}
