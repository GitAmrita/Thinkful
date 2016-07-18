package com.thinkful.fizzbuzzunit1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;


public class MainActivity extends Activity {
    private static final String TAG = "FizzBuzz";

    TextView textView;
    EditText editText;
    String fizzString = "Fizz";
    String buzzString = "Buzz";
    String fizzBuzzString = "FizzBuzz";
    String valueString;
    int [] fizzArray = new int[10];
    int [] buzzArray = new int[10];
    int fizzIndex = 0;
    int buzzIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doButton(View view){
        // Implement the function of the button here
        // Use numberOfValues, and create a string array of both the numbers and words,
        // replacing the appropriate numbers with the words fizz or buzz

        editText = (EditText) findViewById(R.id.editText); // user input
        textView = (TextView) findViewById(R.id.fizzText); // where the text will display
        textView.setText("" + "\n");

        int numberOfValues = Integer.parseInt(editText.getText().toString());
        for(int i=1; i<=numberOfValues; i++){
            boolean isDivisibleByThree = isDivisible(i, 3);
            boolean isDivisibleByFive = isDivisible(i, 5);
            if(isDivisibleByThree && isDivisibleByFive){
                valueString = fizzBuzzString;
            }
            else if(isDivisibleByThree){
                valueString = fizzString;
                fizzArray[fizzIndex] = i;
                fizzIndex ++;
            }
            else if(isDivisibleByFive){
                valueString = buzzString;
                buzzArray[buzzIndex] = i;
                buzzIndex ++;
            }
            else{
                valueString = String.valueOf(i);
            }
            textView.append(valueString + "\n");
        }
        for (int i=0; i<fizzArray.length; i++){
            Log.i(TAG, "FizzIndex " + String.valueOf(fizzArray[i]));
            Log.i(TAG, "BuzzIndex " + String.valueOf(buzzArray[i]));
        }
    }

    private boolean isDivisible(int number, int divisibleBy){
        return number % divisibleBy == 0;
    }
}
