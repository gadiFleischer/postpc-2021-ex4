package exercise.find.roots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ShowRootsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_roots);
        Intent resultsIntent=getIntent();
        String originNumber = "Success!\n Original number is: "+ resultsIntent.getLongExtra("original_number", 0);
        String firstRoot ="First Root: "+ resultsIntent.getLongExtra("root1", 0);
        String secondRoot ="Second Root: "+ resultsIntent.getLongExtra("root2", 0);
        String timeCalc ="Time Calculation Is: "+ resultsIntent.getLongExtra("time_until_give_up_seconds", 0);

        TextView originNumberText = findViewById(R.id.original_number);
        TextView root1ShowText = findViewById(R.id.root1);
        TextView root2ShowText = findViewById(R.id.root2);
        TextView timeCalcShowText = findViewById(R.id.time);

        originNumberText.setText(originNumber);
        root1ShowText.setText(firstRoot);
        root2ShowText.setText(secondRoot);
        timeCalcShowText.setText(timeCalc);
    }
}
