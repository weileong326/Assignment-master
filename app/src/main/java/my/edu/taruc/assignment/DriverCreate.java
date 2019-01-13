package my.edu.taruc.assignment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DriverCreate extends AppCompatActivity {

    private Spinner StartPlace, DestinationPlace;
    private TextView AvailableSlot, WaitTime, Charge, Note;
    private String URL_SAVE = "https://yaptw-wa16.000webhostapp.com/insert_carpool.php";
    private String DELETE_URL_HOST = "https://yaptw-wa16.000webhostapp.com/delete_carpool_where.php";
    private String StudentName = "" ;
    private String StudentID = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_create);

        StartPlace = findViewById(R.id.Input_SpinnerStart);
        DestinationPlace = findViewById(R.id.Input_SpInnerDestination);
        AvailableSlot = findViewById(R.id.Input_TextSlot);
        WaitTime = findViewById(R.id.Input_TextWaitTime);
        Charge = findViewById(R.id.Input_TextCharge);
        Note = findViewById(R.id.Input_StringNote);

        SharedPreferences prefs =   getApplicationContext().getSharedPreferences("PrefText", MODE_PRIVATE);
        StudentName = prefs.getString("StudentName", "No name defined");//"No name defined" is the default value.
        StudentID = prefs.getString("StudentID", "No ID defined"); //0 is the default value.

    }

    private Intent newIntent;
    public void createCarPool(View view) {
        if (StartPlace.getSelectedItemPosition() != 0 && DestinationPlace.getSelectedItemPosition() != 0 && DestinationPlace.getSelectedItemPosition() != StartPlace.getSelectedItemPosition()) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setMessage("Loading...");
            mDialog.show();

            saveData();

            newIntent = new Intent(this, CarPoolRoom.class);
            newIntent.putExtra("RoomID", StudentID);
            newIntent.putExtra("isHost", true);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please choose a proper location", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void saveData(){
        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    URL_SAVE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==0) {
                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DELETE_URL_HOST + "?RoomID=" + StudentID, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                }
                                            });

                                    // Add the request to the RequestQueue.
                                    NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else{
                                    startActivity(newIntent);
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("RoomID", StudentID);
                    params.put("Driver", StudentName);
                    params.put("Charges", Charge.getText().toString() );
                    params.put("Slot", AvailableSlot.getText().toString() );
                    params.put("CurrentSlot", "0");
                    params.put("FromLocation", StartPlace.getSelectedItem().toString() );
                    params.put("ToLocation", DestinationPlace.getSelectedItem().toString() );
                    int temp = Integer.parseInt(WaitTime.getText().toString());;
                    Date currentTime = new Date(Calendar.getInstance().getTimeInMillis() + (temp * 60000));
                    params.put("CreatedTime", new SimpleDateFormat("HH:mm:ss").format(currentTime));
                    params.put("Note",Note.getText().toString());
                    params.put("isStart","false");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            NetworkCalls.getInstance().addToRequestQueue(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage("Discard changes?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DriverCreate.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getApplicationContext().getApplicationContext().getSharedPreferences("PrefText3", MODE_PRIVATE).edit();
                        editor.putInt("StartPlaceDetails", StartPlace.getSelectedItemPosition());
                        editor.putInt("DestinationPlaceDetails", DestinationPlace.getSelectedItemPosition());
                        editor.putString("AvailableSlotDetails", AvailableSlot.getText().toString());
                        editor.putString("WaitTimeDetails", WaitTime.getText().toString());
                        editor.putString("ChargeDetails", Charge.getText().toString());
                        editor.putString("NoteDetails", Note.getText().toString());
                        editor.apply();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Warning  !");
        alert.show();

    }

    @Override
    public void onPause() { super.onPause();
        SharedPreferences.Editor editor = getApplicationContext().getApplicationContext().getSharedPreferences("PrefText3", MODE_PRIVATE).edit();
        editor.putInt("StartPlaceDetails", StartPlace.getSelectedItemPosition());
        editor.putInt("DestinationPlaceDetails", DestinationPlace.getSelectedItemPosition());
        editor.putString("AvailableSlotDetails", AvailableSlot.getText().toString());
        editor.putString("WaitTimeDetails", WaitTime.getText().toString());
        editor.putString("ChargeDetails", Charge.getText().toString());
        editor.putString("NoteDetails", Note.getText().toString());
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp2 = getSharedPreferences("PrefText3", MODE_PRIVATE);
        int StartPlaceIndex = sp2.getInt("StartPlaceDetails",0);
        int DestinationPlaceIndex = sp2.getInt("DestinationPlaceDetails",0);
        String AvailableSlotIndex = sp2.getString("AvailableSlotDetails","");
        String WaitTimeIndex = sp2.getString("WaitTimeDetails","");
        String ChargeIndex = sp2.getString("ChargeDetails","");
        String NoteIndex = sp2.getString("NoteDetails","");
        StartPlace.setSelection(StartPlaceIndex);
        DestinationPlace.setSelection(DestinationPlaceIndex);
        AvailableSlot.setText(AvailableSlotIndex);
        WaitTime.setText(WaitTimeIndex);
        Charge.setText(ChargeIndex);
        Note.setText(NoteIndex);
    }


    public void toCreateRoom(View view){
        if(StartPlace.getSelectedItemPosition()!= 0 && DestinationPlace.getSelectedItemPosition()!= 0 && AvailableSlot.getText().toString()!=" " && Charge.getText().toString()!=" " && Note.getText().toString()!=" " && StartPlace.getSelectedItemPosition()!= 0 && DestinationPlace.getSelectedItemPosition()!= 0) {
            Intent newIntent = new Intent(this, CarPoolRoom.class);

            newIntent.putExtra("StartPlaceDetails", StartPlace.getSelectedItemPosition());
            newIntent.putExtra("DestinationPlaceDetails", DestinationPlace.getSelectedItemPosition());
            newIntent.putExtra("AvailableSlotDetails", AvailableSlot.getText().toString());
            newIntent.putExtra("WaitTimeDetails", WaitTime.getText().toString());
            newIntent.putExtra("ChargeDetails", Charge.getText().toString());
            newIntent.putExtra("NoteDetails", Note.getText().toString());

            this.startActivity(newIntent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Please select correct location",Toast.LENGTH_LONG).show();
        }
    }

}