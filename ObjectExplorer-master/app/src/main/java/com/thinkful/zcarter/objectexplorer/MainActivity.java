package com.thinkful.zcarter.objectexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

// This class handles output of log to the emulator screen
class Screen {
    // static fields
    static TextView textView;
    static String text = new String();

    public static void outputToScreen() {
        textView.setText(text);
    }

    public static void log(String textToLog) {
        text += textToLog + "\n";
        textView.setText(text);
    }
}

abstract class Ball extends Observable {
    public abstract void roll();
}

class Football extends Ball {

    // Note the @Override is a compiler directive.
    // It is not required, but is a best practice to use
    // for your own benefit
    @Override
    public void roll() {
        Screen.log("This football is rolling");
        this.setChanged();
        this.notifyObservers();
    }
}

class Referee implements Observer {

    @Override
    public void update(Observable observable, Object data) {
        String[] parts = observable.getClass().toString().split("\\.");
        String ballClass = parts[parts.length-1];
        Screen.log("The " + ballClass + " has been changed.");

    }
}

class Crowd implements Observer {

    @Override
    public void update(Observable observable, Object data) {
        String[] parts = observable.getClass().toString().split("\\.");
        String ballClass = parts[parts.length-1];
        if ( ((Hardball)observable).isHomeRun ){
            Screen.log("The " + ballClass + " cheers.");
        }

    }
}

abstract class Baseball extends Ball {
    protected float speed;
    public abstract void pitch();
}

class Softball extends Baseball {
    @Override
    public void pitch(){
        Log.i("Softball", "A soft ball is pitched underhand");
    }

    @Override
    public void roll() {
        Screen.log("This soft ball is rolling");
        this.setChanged();
        this.notifyObservers();
    }

}

class Hardball extends Baseball {
    boolean isHomeRun = false;
    public Hardball() {
       this.speed = 65;
    }
    public Hardball(float speed) {
        this.speed = speed;
    }
    public Hardball(Observer observer) {
        this.addObserver(observer);
    }

    @Override
    public void pitch() {
        Screen.log("A hard ball is pitched overhand with speed " + this.speed);
    }

    @Override
    public void roll() {
        Screen.log("This hard ball is rolling");
        this.setChanged();
        this.notifyObservers();
    }

    public void homerunHit(){
        Screen.log("Home ground!");
        isHomeRun = true;
        this.setChanged();
        this.notifyObservers();
    }
}

class BouncyBall {

    public void bounce() {
        Screen.log("The BancyBall object bounces.");
    }
}

class SuperBall extends BouncyBall {

    @Override
    public void bounce() {
        Screen.log("The SuperBall object bounces super high.");
    }
}

//-------------------------------------------------------------------
// This space is for students to define classes in Thinkful Unit 2
// Alternatively, create classes in a new file in the same package,
// and they will be available here

//-------------------------------------------------------------------


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Screen.textView = (TextView) this.findViewById(R.id.textView);
        Button startButton = (Button)this.findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Screen.outputToScreen();
            }
        });
        this.playBall();

    }

    public void playBall() {
       /* Hardball tryItBall = new Hardball(10);
        tryItBall.pitch();

        SuperBall superBall = new SuperBall();
        superBall.bounce();*/

        Referee firstUmpire = new Referee();
        Crowd people = new Crowd();
       // Referee secondUmpire = new Referee();
        Hardball theBall = new Hardball(firstUmpire);
       theBall.addObserver(people);

       // Hardball theBall = new Hardball(people);
      // theBall.roll();
      theBall.homerunHit();
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
}
