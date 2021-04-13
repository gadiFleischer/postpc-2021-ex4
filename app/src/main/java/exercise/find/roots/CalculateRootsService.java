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