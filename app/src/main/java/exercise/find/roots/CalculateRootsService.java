package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class CalculateRootsService extends IntentService {

  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent == null) return;
    long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
    if (numberToCalculateRootsFor <= 0) {
      Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
      return;
    }
    Intent sendIntent = new Intent();
    long timeStartMs = System.currentTimeMillis();
    if(numberToCalculateRootsFor==1){
      sendGood(numberToCalculateRootsFor, sendIntent,1,1,timeStartMs);
    }else{
      for(long i=2;i< numberToCalculateRootsFor/2; i++){
        if (numberToCalculateRootsFor % i == 0){
          sendGood(numberToCalculateRootsFor, sendIntent,i,numberToCalculateRootsFor/i,timeStartMs);
          return;
        }
        long checkTime=System.currentTimeMillis() - timeStartMs;
        if(TimeUnit.MILLISECONDS.toSeconds(checkTime) >= 20 ){
          sendBad(sendIntent,numberToCalculateRootsFor,checkTime);
          return;
        }
      }
      //the number is prime
      sendGood(numberToCalculateRootsFor, sendIntent,numberToCalculateRootsFor,1,timeStartMs);
    }

    /*
    TODO:
     calculate the roots.
     check the time (using `System.currentTimeMillis()`) and stop calculations if can't find an answer after 20 seconds
     upon success (found a root, or found that the input number is prime):
      send broadcast with action "found_roots" and with extras:
       - "original_number"(long)
       - "root1"(long)
       - "root2"(long)
     upon failure (giving up after 20 seconds without an answer):
      send broadcast with action "stopped_calculations" and with extras:
       - "original_number"(long)
       - "time_until_give_up_seconds"(long) the time we tried calculating

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up

     */
    }

  private void sendGood(long numberToCalculateRootsFor, Intent intent,long root1,long root2,long timeStarts) {
    intent.setAction("found_roots");
    intent.putExtra("original_number",numberToCalculateRootsFor);
    intent.putExtra("root1",root1);
    intent.putExtra("root2",root2);
    long x = System.currentTimeMillis();
    long timeInSeconds = (long)((x - timeStarts)/1000);
    intent.putExtra("time_until_give_up_seconds",timeInSeconds);
    sendBroadcast(intent);
  }
  void sendBad(Intent intent,long numberToCalculateRootsFor,long checkTime){
    intent.setAction("stopped_calculations");
    intent.putExtra("original_number",numberToCalculateRootsFor);
    intent.putExtra("time_until_give_up_seconds",(long)checkTime/1000);
    sendBroadcast(intent);
  }

}