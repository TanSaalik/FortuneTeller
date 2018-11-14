package com.helennagel.magic8ball;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //Set variables, constants and interface elements (like TextView).
    private TextView txtAnswer, resultText;
    private SensorManager sensorManager;
    private float acelValue;
    private float acelLast;
    private float shake;
    private String[] answer;


   //The initial build-up where View, buttons and other properties are referenced.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAnswer = findViewById(R.id.txtAnwser);
        resultText = findViewById(R.id.resultText);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
       sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor
               (Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        acelValue = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
         shake = 0.00f;
        answer = getResources().getStringArray(R.array.answers);

    }

    //Menu is created from XML resources when Menu is pressed/clicked.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When menuitem is clicked, take user to the next activity.
    // Otherwise "false" for normal functionality.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.magic:
                startActivity(new Intent(this, ShakeActivity.class));
                return true;
        }
        return false;
    }

    //An interface to receive data from sensors.
    private final SensorEventListener sensorListener = new SensorEventListener() {
        //When sensor data has changed (e.g. shook the device), calculate to check whether
        // the shaking was strong enough (intentional). If it was provide a random answer from list.
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelValue;
            acelValue = (float)Math.sqrt((double)(x*x + y*y + z*z));
            float delta = acelValue - acelLast;
            shake = shake * 0.9f + delta;
            if (shake > 12){
                int randomInt = new Random().nextInt(answer.length);
                String randomAnswer = answer[randomInt];
                txtAnswer.setText(randomAnswer);
            }
        }

        //When sensor accuracy changes.
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    //start another method when onMic button is pressed
    public void onMic(View view) {
        promptSpeechInput();
    }

    //Start new intent and try to prompt the speech input.
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!");
        try{
            startActivityForResult(intent, 100);
            int randomInt = new Random().nextInt(answer.length);
            String randomAnswer = answer[randomInt];
            txtAnswer.setText(randomAnswer);
        }
        catch (ActivityNotFoundException ax){
            Toast.makeText(this, "Sorry, your device doesn't support speech language",
                    Toast.LENGTH_LONG).show();
        }
    }

    //When "onClear" button is pressed, clear the text field.
    public void onClear(View view) {
        resultText.setText("");
    }
}
