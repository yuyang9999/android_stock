package com.naughtypiggy.android.stock;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.naughtypiggy.android.stock.network.model.ProfileStock;
import com.naughtypiggy.android.stock.utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class ProfileStockActivity extends AppCompatActivity {

    private ProfileStock mStock;
    private BarChart mStockChart;
    private static final int OPEN_SEARCH_ACTIVITY_ITEM = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_stock);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String stockString = intent.getStringExtra(getString(R.string.one_stock_profile_key));
        mStock = (ProfileStock) Utility.ungsonObject(stockString, ProfileStock.class);
        setTitle(mStock.getSname());

        mStockChart = (BarChart) findViewById(R.id.stockchart);
        mStockChart.setDrawBarShadow(false);
        mStockChart.setDrawValueAboveBar(true);

        mStockChart.setPinchZoom(false);
        mStockChart.setDrawGridBackground(false);

        XAxis xAxis = mStockChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Float.toString(value);
            }
        });

        YAxis leftAxis = mStockChart.getAxisLeft();
        leftAxis.setLabelCount(8);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Float.toString(value);
            }
        });
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMaximum(0f);

        YAxis rightAxis = mStockChart.getAxisRight();
        rightAxis.setDrawGridLines(false);

        setdata();
    }


    private void setdata() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            entries.add(new BarEntry(i, (float)Math.random() * 100));
        }

        BarDataSet set1 = new BarDataSet(entries, "the year 2017");
        set1.setDrawIcons(false);
        set1.setColors(ColorTemplate.MATERIAL_COLORS);

        ArrayList<IBarDataSet> datasets = new ArrayList<>();
        datasets.add(set1);

        BarData data = new BarData(datasets);

        mStockChart.setData(data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
