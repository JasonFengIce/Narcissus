package cn.ismartv.voice.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 2/17/16.
 */
public class WeatherView extends FrameLayout {

    private TextView weatherTemp;
    private TextView weatherDay;
    private TextView weatherDetail;
    private ImageView weatherIcon;
    private TextView weatherDate;

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        RelativeLayout relativeLayout = (RelativeLayout) inflate(context, R.layout.layout_weather_view, null);
        addView(relativeLayout);
        weatherTemp = (TextView) relativeLayout.findViewById(R.id.weather_temp);
        weatherDay = (TextView) relativeLayout.findViewById(R.id.weather_day);
        weatherDetail = (TextView) relativeLayout.findViewById(R.id.weather_detail);
        weatherIcon = (ImageView) relativeLayout.findViewById(R.id.weather_icon);
        weatherDate = (TextView) relativeLayout.findViewById(R.id.weather_date);
    }

    public void setWeatherTemp(String weatherTemp) {
        this.weatherTemp.setText(weatherTemp);
    }

    public void setWeatherDay(String weatherDay) {
        this.weatherDay.setText(weatherDay);
    }

    public void setWeatherDetail(String weatherDetail) {
        this.weatherDetail.setText(weatherDetail);
    }


    public void setWeatherDate(String weatherDate) {
        this.weatherDate.setText(weatherDate);
    }

    public ImageView getWeatherIcon() {
        return weatherIcon;
    }
}
