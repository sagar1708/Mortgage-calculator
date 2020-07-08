package com.example.mcalcpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.roumani.i2c.MPro;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.TTS = new TextToSpeech(this,this);


        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    MPro mp = new MPro();
    public void buttonClicked(View v)
    {
        try {

            mp.setPrinciple(((EditText) findViewById(R.id.pBox)).getText().toString());
            mp.setAmortization(((EditText) findViewById(R.id.aBox)).getText().toString());
            mp.setInterest(((EditText) findViewById(R.id.iBox)).getText().toString());

            String result = "Monthly Payment = " + mp.computePayment("%,.2f");
            result += "\n\n";
            result += "By making this payments monthly for 20 years, the mortgage will be paid in full." +
                    " But if you terminate the mortgage on its nth anniversary, the balance still owing depends on " +
                    "n as shown below:";
            result += "\n\n";
            result += "        n" + "        balance";
            result += "\n\n";

            for (int i = 0; i <= 20; i++) {
                result += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                result += "\n\n";


            }
            ((TextView) findViewById(R.id.output)).setText(result);

            TTS.speak(result, TextToSpeech.QUEUE_FLUSH, null);
        }
        catch (Exception e)
        {
            Toast label = Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
            label.show();
        }
    }

    private TextToSpeech TTS;

    @Override
    public void onInit(int status) {
        this.TTS.setLanguage(Locale.CANADA);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double ax = event.values[0];
        double bx = event.values[1];
        double cx = event.values[2];
        double a = Math.sqrt(ax*ax + bx*bx + cx*cx);
        if(a>20)
        {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
