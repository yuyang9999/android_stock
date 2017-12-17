package com.naughtypiggy.android.stock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.ProfileStock;
import com.naughtypiggy.android.stock.network.model.StockHistory;
import com.naughtypiggy.android.stock.utility.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileStockActivity extends AppCompatActivity {

    private ProfileStock mStock;
    private LineChart mStockChart;
    private static final int OPEN_SEARCH_ACTIVITY_ITEM = 100;

    private void refreshStockHistory() {
        Call<ApiResp.ApiStockHistoryResp> call = NetworkUtil.service.getStockHistory(AuthManager.getAccessToken(), mStock.getSname());

        call.enqueue(new Callback<ApiResp.ApiStockHistoryResp>() {
            @Override
            public void onResponse(Call<ApiResp.ApiStockHistoryResp> call, Response<ApiResp.ApiStockHistoryResp> response) {
                ApiResp.ApiStockHistoryResp resp = response.body();
                if (resp != null) {
                    if (resp.hasError) {
                        Utility.showToastText(ProfileStockActivity.this, resp.errorMsg);
                    } else {
                        List<StockHistory> histories = resp.response;
                        resetStockChartDataWithHisotryData(histories);
                    }
                } else {
                    Utility.showToastText(ProfileStockActivity.this, "could not get stock history data");
                }
            }

            @Override
            public void onFailure(Call<ApiResp.ApiStockHistoryResp> call, Throwable t) {
                Utility.showToastText(ProfileStockActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void resetStockChartDataWithHisotryData(final List<StockHistory> histories) {
        if (histories.size() == 0) {
            return;
        }


        List<Float> closePrices = new ArrayList<>();
        for (int i = 0; i < histories.size(); i++) {
            closePrices.add(histories.get(i).getClos());
        }

        //get the min, max
        float maxClose = Collections.max(closePrices);
        float minClose = Collections.min(closePrices);

        final int dividends = 10;

        XAxis xAxis = mStockChart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (Math.abs(value - (int)value) <= 0.01) {
                    int idx = (int)value;

                    StockHistory history = histories.get(idx);
                    Date d = history.getDate();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    return format.format(d);
                }

                return "";
            }
        });

        LimitLine ll1 = new LimitLine(maxClose, "");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(minClose, "");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setTextSize(10f);

        YAxis leftAxis = mStockChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum((float)Math.ceil(maxClose / dividends) * dividends);
        leftAxis.setAxisMinimum((float)Math.floor(minClose / dividends)  * dividends);

        leftAxis.enableGridDashedLine(19f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        mStockChart.getAxisRight().setEnabled(false);


        List<Entry> values = new ArrayList<>();
        for (int i = 0; i < histories.size(); i++) {
            values.add(new Entry(i, closePrices.get(i)));
        }

        LineDataSet set1 = new LineDataSet(values, mStock.getSname());
        set1.setDrawIcons(false);

        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[] {10f, 5f}, 0));;
        set1.setFormSize(15.f);

        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> datasets = new ArrayList<>();
        datasets.add(set1);

        LineData data = new LineData(datasets);

        mStockChart.setData(data);
    }

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

        mStockChart = (LineChart) findViewById(R.id.stockchart);

        setupLineChart();

//        mStockChart.setPinchZoom(false);
//        mStockChart.setDrawGridBackground(false);
//
//        XAxis xAxis = mStockChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setGranularity(1f);
//        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return Float.toString(value);
//            }
//        });
//
//        YAxis leftAxis = mStockChart.getAxisLeft();
//        leftAxis.setLabelCount(8);
//        leftAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return Float.toString(value);
//            }
//        });
//        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMaximum(0f);
//
//        YAxis rightAxis = mStockChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);

//        setupLineChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStockHistory();
    }

    private void setupLineChart() {
        mStockChart.setDragEnabled(true);
        mStockChart.setScaleEnabled(true);
        mStockChart.setPinchZoom(true);
    }


//    private void setdata() {
//        List<BarEntry> entries = new ArrayList<>();
//        for (int i = 0; i < 10; i ++) {
//            entries.add(new BarEntry(i, (float)Math.random() * 100));
//        }
//
//        BarDataSet set1 = new BarDataSet(entries, "the year 2017");
//        set1.setDrawIcons(false);
//        set1.setColors(ColorTemplate.MATERIAL_COLORS);
//
//        ArrayList<IBarDataSet> datasets = new ArrayList<>();
//        datasets.add(set1);
//
//        BarData data = new BarData(datasets);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
