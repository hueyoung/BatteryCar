package com.ytl.batterycar.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ytl.batterycar.R;

public class MainActivity extends AppCompatActivity {
    // 当前电量显示
    private TextView tv_electric;
    // 当前电压
    private TextView tv_voltage;
    // 当前电池温度
    private TextView tv_temperature;
    // 健康
    private TextView tv_health;
    // 开关
    private ToggleButton tb_switch;
    private BatteryReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_electric = findViewById(R.id.tv_electric);
        tv_voltage = findViewById(R.id.tv_voltage);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_health = findViewById(R.id.tv_health);
        tb_switch = findViewById(R.id.tb_switch);
        autoGetElectric(true);
        tb_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoGetElectric(isChecked);
            }
        });
    }

    public void clickRefreshBtn(View v) {
        autoGetElectric(true);
    }

    /** 自动获取电量 */
    private void autoGetElectric(boolean isAuto) {
        if (isAuto && batteryReceiver == null) {
            // 广播监听
            registerReceiver(batteryReceiver = new BatteryReceiver() ,
                            new IntentFilter(Intent.ACTION_BATTERY_CHANGED ) ) ;

        } else if (!isAuto && batteryReceiver != null){
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }
    }

    /** 手动获取电量 */
    private void handGetElectric() {
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //int current=intent.getExtras().getInt("level");//获得当前电量
            //int total=intent.getExtras().getInt("scale");//获得总电量
            //int percent=current * 100 / total;
            //tv.setText("现在的电量是"+percent+"%。");
            int level = intent.getIntExtra( "level" , 0 );//电量（0-100）
            // int status = intent.getIntExtra( "status" , 0 );
            int health = intent.getIntExtra( "health" , 1 );
            //boolean present = intent.getBooleanExtra( "present" , false );
            //int scale = intent.getIntExtra( "scale" , 0 );
            //int plugged = intent.getIntExtra( "plugged" , 0 );//
            int voltage = intent.getIntExtra( "voltage" , 0 );//电压
            int temperature = intent.getIntExtra( "temperature" , 0 ); // 温度的单位是10℃
            String technology = intent.getStringExtra( "technology" );
            tv_electric.setText("电量: " + level + "%");
            tv_voltage.setText("电压: " + voltage + "mV");
            tv_temperature.setText("温度: " + (temperature / 10) + "℃");
            tv_health.setText("健康: " + health);
        }
    }

    /**
     * 设置电池图标
     *
     * @param level
     *            0-10
     */
    private int getBatteryImageByLevel(int level) {
        int batteryResId = R.mipmap.ic_launcher;
        try {
            batteryResId = getResources().getIdentifier(
                        "home_battery_" + level, "drawable", getPackageName());
        } catch (Exception e) {
        } finally {
            return batteryResId;
        }
    }
}
