package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private BroadcastReceiver broadcastReceiverForSuccess = null;
  private BroadcastReceiver broadcastReceiverForFailure = null;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ProgressBar progressBar = findViewById(R.id.progressBar);
    EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
    Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set initial UI:
    progressBar.setVisibility(View.GONE); // hide progress
    editTextUserInput.setText(""); // cleanup text in edit-text
    editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
    buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

    // set listener on the input written by the keyboard to the edit-text
    editTextUserInput.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
      public void onTextChanged(CharSequence s, int start, int before, int count) { }
      public void afterTextChanged(Editable s) {
        // text did change
        String newText = editTextUserInput.getText().toString();
        buttonCalculateRoots.setEnabled(newText.matches("-?\\d+(\\.\\d+)?"));
        // todo: check conditions to decide if button should be enabled/disabled (see spec below)
      }
    });
//* when we triggered a calculation and still didn't get any result, button is disabled
// * otherwise (valid number && not calculating anything in the BG), button is enabled
    // set click-listener to the button
    buttonCalculateRoots.setOnClickListener(v -> {
      Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
      String userInputString = editTextUserInput.getText().toString();
      if(userInputString.matches("-?\\d+(\\.\\d+)?")) {
        long userInputLong = Long.parseLong(userInputString, 10);
        intentToOpenService.putExtra("number_for_service", userInputLong);
        startService(intentToOpenService);
        buttonCalculateRoots.setEnabled(false);
        editTextUserInput.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
      }
      // todo: check that `userInputString` is a number. handle bad input. convert `userInputString` to long
      // todo: set views states according to the spec (below)
    });

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForSuccess = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots")){
          return;
        }
        progressBar.setVisibility(View.GONE);
        buttonCalculateRoots.setEnabled(true);
        editTextUserInput.setEnabled(true);
        editTextUserInput.setText("");
        long firstRoot = incomingIntent.getLongExtra("root1", 0);
        long secondRoot = incomingIntent.getLongExtra("root2", 0);
        long timeCalc = incomingIntent.getLongExtra("time_until_give_up_seconds", 0);
        System.out.println("root1 is :"+firstRoot);
        System.out.println("root2 is :"+secondRoot);
        System.out.println("time is :"+ timeCalc);
        //add here new class
        // success finding roots!
        /*
         TODO: handle "roots-found" as defined in the spec (below).
          also:
           - the service found roots and passed them to you in the `incomingIntent`. extract them.
           - when creating an intent to open the new-activity, pass the roots as extras to the new-activity intent
             (see for example how did we pass an extra when starting the calculation-service)
         */
      }
    };
    registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));
    registerReceiver(broadcastReceiverForFailure, new IntentFilter("stopped_calculations"));
    broadcastReceiverForFailure = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations")){
          return;
        }
        progressBar.setVisibility(View.GONE);
        buttonCalculateRoots.setEnabled(true);
        editTextUserInput.setEnabled(true);
        long timeCalc = incomingIntent.getLongExtra("time_until_give_up_seconds", 0);
        long originalNumber = incomingIntent.getLongExtra("original_number", 0);
        System.out.println("time is :"+ timeCalc);
        System.out.println("original number is :"+originalNumber);
        Toast.makeText(context, "calculation aborted after "+(timeCalc*1000)+" seconds", Toast.LENGTH_SHORT).show();
      }
    };
//        // success finding roots!
//        /*
//         TODO: handle "roots-found" as defined in the spec (below).
//          also:
//           - the service found roots and passed them to you in the `incomingIntent`. extract them.
//           - when creating an intent to open the new-activity, pass the roots as extras to the new-activity intent
//             (see for example how did we pass an extra when starting the calculation-service)
//         */





    /*
    todo:
     add a broadcast-receiver to listen for abort-calculating as defined in the spec (below)
     to show a Toast, use this code:
     `Toast.makeText(this, "text goes here", Toast.LENGTH_SHORT).show()`
     */
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.unregisterReceiver(broadcastReceiverForSuccess);
    this.unregisterReceiver(broadcastReceiverForFailure);
    // todo: remove ALL broadcast receivers we registered earlier in onCreate().
    //  to remove a registered receiver, call method `this.unregisterReceiver(<receiver-to-remove>)`
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    // TODO: put relevant data into bundle as you see fit
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    // TODO: load data from bundle and set screen state (see spec below)
  }
}


/*

TODO:
the spec is:

upon launch, Activity starts out "clean":
* progress-bar is hidden
* "input" edit-text has no input and it is enabled
* "calculate roots" button is disabled

the button behavior is:
* when there is no valid-number as an input in the edit-text, button is disabled
* when we triggered a calculation and still didn't get any result, button is disabled
* otherwise (valid number && not calculating anything in the BG), button is enabled

the edit-text behavior is:
* when there is a calculation in the BG, edit-text is disabled (user can't input anything)
* otherwise (not calculating anything in the BG), edit-text is enabled (user can tap to open the keyboard and add input)

the progress behavior is:
* when there is a calculation in the BG, progress is showing
* otherwise (not calculating anything in the BG), progress is hidden

when "calculate roots" button is clicked:
* change states for the progress, edit-text and button as needed, so user can't interact with the screen

when calculation is complete successfully:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* open a new "success" screen showing the following data:
  - the original input number
  - 2 roots combining this number (e.g. if the input was 99 then you can show "99=9*11" or "99=3*33"
  - calculation time in seconds

when calculation is aborted as it took too much time:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* show a toast "calculation aborted after X seconds"


upon screen rotation (saveState && loadState) the new screen should show exactly the same state as the old screen. this means:
* edit-text shows the same input
* edit-text is disabled/enabled based on current "is waiting for calculation?" state
* progress is showing/hidden based on current "is waiting for calculation?" state
* button is enabled/disabled based on current "is waiting for calculation?" state && there is a valid number in the edit-text input


 */