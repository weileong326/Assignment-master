package my.edu.taruc.assignment;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PassengerSearchPath extends AppCompatActivity {

    Spinner From,To;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_search_path);

        From = findViewById(R.id.input_FromP);
        To = findViewById(R.id.input_ToP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage("Discard changes?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PassengerSearchPath.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("PrefText2", MODE_PRIVATE).edit();
                        editor.putInt("FromLocation", From.getSelectedItemPosition());
                        editor.putInt("ToLocation", To.getSelectedItemPosition());
                        editor.apply();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Warning  !");
        alert.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("PrefText2", MODE_PRIVATE);
        int fromIndex = sp.getInt("FromLocation",0);
        int toIndex = sp.getInt("ToLocation",0);
        From.setSelection(fromIndex);
        To.setSelection(toIndex);
    }

    public void toFoundPath(View view){
        if(From.getSelectedItemPosition()!=0 && To.getSelectedItemPosition()!=0 && From.getSelectedItemPosition()!= To.getSelectedItemPosition()) {
            Intent newIntent = new Intent(this, PassengerFoundPath.class);

            newIntent.putExtra("From", From.getSelectedItem().toString());
            newIntent.putExtra("To", To.getSelectedItem().toString());

            this.startActivity(newIntent);

            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("PrefText2", MODE_PRIVATE).edit();
            editor.putInt("FromLocation", From.getSelectedItemPosition());
            editor.putInt("ToLocation", To.getSelectedItemPosition());
            editor.apply();
        }
        else{
            Toast.makeText(getApplicationContext(),"Please select correct location",Toast.LENGTH_LONG).show();
        }
    }
}
