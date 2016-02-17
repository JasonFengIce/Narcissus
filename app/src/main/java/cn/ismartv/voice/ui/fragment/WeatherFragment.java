package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.handler.WeatherXmlParser;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.WeatherEntity;
import cn.ismartv.voice.data.table.CityTable;
import cn.ismartv.voice.ui.widget.WeatherView;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 2/17/16.
 */
public class WeatherFragment extends BaseFragment implements View.OnClickListener {
    private WeatherView todayWeather;
    private WeatherView tomorrowWeather;
    private View noWeatherTipLayout;
    private FrameLayout moreWeatherBtn;
    private TextView currentLocationText;

    private CityTable location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todayWeather = (WeatherView) view.findViewById(R.id.today_weather);
        tomorrowWeather = (WeatherView) view.findViewById(R.id.tomorrow_weather);
        noWeatherTipLayout = view.findViewById(R.id.no_weather_tip);
        moreWeatherBtn = (FrameLayout) view.findViewById(R.id.more_weather);
        currentLocationText = (TextView) view.findViewById(R.id.current_location);
        moreWeatherBtn.setOnClickListener(this);

    }

    private void fetchWeather() {
        long geoId = 0;
        if (location == null) {
            noWeatherTipLayout.setVisibility(View.VISIBLE);
        } else {
            noWeatherTipLayout.setVisibility(View.GONE);
            geoId = location.geo_id;
            String rawTextValue = getString(R.string.weather_current_location);
            currentLocationText.setText(String.format(rawTextValue, location.city));
        }


        Retrofit retrofit = HttpManager.getInstance().resetAdapter_MEDIA_LILY;
        retrofit.create(HttpAPI.WeatherSearch.class).doRequest(String.valueOf(geoId)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response) {
                if (response.errorBody() != null) {

                } else {
                    try {
                        String result = response.body().string();
                        WeatherEntity weatherEntity = WeatherXmlParser.parse(result);
                        todayWeather.setWeatherTemp(weatherEntity.getToday().getTemplow() + "℃ ~ " + weatherEntity.getToday().getTemphigh() + "℃");
                        todayWeather.setWeatherDay("今天");
                        todayWeather.setWeatherDetail(weatherEntity.getToday().getCondition());
                        Picasso.with(getContext()).load(weatherEntity.getToday().getImage_url()).into(todayWeather.getWeatherIcon());
                        Date now = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");//可以方便地修改日期格式
                        String todayTime = dateFormat.format(now);
                        todayWeather.setWeatherDate(todayTime);

                        tomorrowWeather.setWeatherTemp(weatherEntity.getTomorrow().getTemplow() + "℃ ~ " + weatherEntity.getTomorrow().getTemphigh() + "℃");
                        tomorrowWeather.setWeatherDay("明天");
                        tomorrowWeather.setWeatherDetail(weatherEntity.getTomorrow().getCondition());
                        Picasso.with(getContext()).load(weatherEntity.getTomorrow().getImage_url()).into(tomorrowWeather.getWeatherIcon());
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(now);
                        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
                        Date tomorrowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
                        String tomorrow = dateFormat.format(tomorrowDate);
                        tomorrowWeather.setWeatherDate(tomorrow);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void setLocation(CityTable cityTable) {
        location = cityTable;
        fetchWeather();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_weather:
                break;
        }
    }
}


