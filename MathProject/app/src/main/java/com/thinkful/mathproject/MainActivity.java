package com.thinkful.mathproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myCalculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int x = 10;
        int y = 5;
        int sum = myAdd(x, y);
        int difference = mySubtract(x, y);
        int product = myMultiply(x, y);
        double quotient = myDivide(x, y);
        Log.i(TAG, "sumValue " + sum);
        Log.i(TAG, "differenceValue " + difference);
        Log.i(TAG, "productValue " + product);
        Log.i(TAG, "quotientValue " + quotient);
    }

    private int myAdd(int number1, int number2) {
        int sum;
        sum = number1 + number2;
        return sum;
    }

    private int mySubtract(int number1, int number2){
        int diff;
        diff = number1 - number2;
        return diff;
    }

    private int myMultiply(int number1, int number2){
        int product;
        product = number1 * number2;
        return product;
    }

    private double myDivide(int numerator, int denominator){
        if (denominator == 0) throw new java.lang.IllegalArgumentException("denominator == 0");
        double quotient;
        quotient = (double) numerator/denominator;
        return  quotient;
    }
}
