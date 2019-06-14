package com.example.mutantsvolley;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MutantForm extends AppCompatActivity {
    private static final String TAG = "MutantForm";
    EditText mutantName, mutantSkill1, mutantSkill2, mutantSkill3;
    TextView userName;
    Button actionButton, deleteButton, btpic;
    ProgressDialog progressDialogForForm;
    private Uri fileUri;
    Uri selectedImage;
    Bitmap photo;
    File file;
    Bitmap bitmap;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mutant_form);

        progressDialogForForm = new ProgressDialog(this);

        mutantName = findViewById(R.id.name);
        mutantSkill1 = findViewById(R.id.skill1);
        mutantSkill2 = findViewById(R.id.skill2);
        mutantSkill3 = findViewById(R.id.skill3);
        actionButton = findViewById(R.id.actionButton);
        deleteButton = findViewById(R.id.deleteButton);
        userName = findViewById(R.id.usernameField);

        btpic = findViewById(R.id.cpic);
        btpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickpic();
            }
        });

        Intent it = getIntent();
        Boolean isEditing = it.getBooleanExtra("isEditing", false);

        if (isEditing == true){
            setTitle("Editando Mutante");
            mutantName.setText(it.getStringExtra("mutantName").toString());
            mutantSkill1.setText(it.getStringExtra("mutantPower1").toString());
            mutantSkill2.setText(it.getStringExtra("mutantPower2").toString());
            mutantSkill3.setText(it.getStringExtra("mutantPower3").toString());
            userName.setText("Criado por: " + it.getStringExtra("userName").toString());
            userName.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            setTitle("Novo Mutante");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void saveMutant(View view){
        JSONObject mutant = new JSONObject();
        try {
            mutant.put("name", mutantName.getText().toString());
            mutant.put("power1", mutantSkill1.getText().toString());
            mutant.put("power2", mutantSkill2.getText().toString());
            mutant.put("power3", mutantSkill3.getText().toString());
            mutant.put("user_id", MainActivity.userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent it = getIntent();
        Boolean isEditing = it.getBooleanExtra("isEditing", false);

        if (isEditing == true){
            //updateMutantRequest(mutant);
            uploadBitmapInUpdate(bitmap, mutant);
        } else {
            //createMutantRequest(mutant);
            uploadBitmap(bitmap);
        }
    }

    public void deleteMutant(View view){
        Intent it = getIntent();
        String mutantId = String.valueOf(it.getIntExtra("mutantId", 0));

        deleteMutantRequest(mutantId);

    }

    public void displayFinishAlert(String title, String description, String button){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    public void displayAlert(String title, String description, String button){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void createMutantRequest(JSONObject params){
        String  REQUEST_TAG = "createMutantTag";
        progressDialogForForm.setMessage("Salvando...");
        progressDialogForForm.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, MainActivity.GENERAL_MUTANT_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialogForForm.dismiss();

                        try {
                            int responseStatus = Integer.valueOf(response.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante criado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", response.getString("erro"), "Entendi!");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                displayAlert("Erro!", "Não foi possível conectar ao servidor!", "Entendi!");
                progressDialogForForm.dismiss();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void updateMutantRequest(JSONObject params){
        String  REQUEST_TAG = "updatingMutantTag";
        progressDialogForForm.setMessage("Salvando...");
        progressDialogForForm.show();
        Intent it = getIntent();
        String mutantId = String.valueOf(it.getIntExtra("mutantId", 0));
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.PUT, MainActivity.GENERAL_MUTANT_URL  + mutantId, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialogForForm.hide();

                        try {
                            int responseStatus = Integer.valueOf(response.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante aualizado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", response.getString("erro"), "Entendi!");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialogForForm.dismiss();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void deleteMutantRequest(String mutantId){
        String  REQUEST_TAG = "deleteMutantTag";
        progressDialogForForm.setMessage("Excluindo...");
        progressDialogForForm.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.DELETE, MainActivity.GENERAL_MUTANT_URL  + mutantId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialogForForm.dismiss();

                        try {
                            int responseStatus = Integer.valueOf(response.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante deletado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", response.getString("erro"), "Entendi!");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialogForForm.dismiss();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    private void clickpic() {
        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                try {
                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                        Log.d("imageUpload", "DEU BOA O UPLOAD");
                        if (filePath != null) {
                            file = new File(filePath);
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        }
                    } else {
                        VolleyLog.d("imageUpload", "Erro no upload");
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {
        progressDialogForForm = new ProgressDialog(this);
        progressDialogForForm.setMessage("Salvando Mutante...");
        progressDialogForForm.show();
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, MainActivity.GENERAL_MUTANT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            progressDialogForForm.dismiss();

                            String obj = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            JSONObject json = new JSONObject(obj);
                            int responseStatus = Integer.valueOf(json.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante criado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", json.getString("erro"), "Entendi!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        displayAlert("Erro!", "Erro de conexão com servidor.", "Entendi!");
                        progressDialogForForm.dismiss();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mutant = new HashMap<>();
                mutant.put("name", mutantName.getText().toString());
                mutant.put("power1", mutantSkill1.getText().toString());
                mutant.put("power2", mutantSkill2.getText().toString());
                mutant.put("power3", mutantSkill3.getText().toString());
                mutant.put("user_id", MainActivity.userId);
                return mutant;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                try {
                    params.put("picture", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                } catch (Exception e){
                    e.printStackTrace();
                }
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void uploadBitmapInUpdate(final Bitmap bitmap, JSONObject mutant ) {
        progressDialogForForm = new ProgressDialog(this);
        progressDialogForForm.setMessage("Salvando Mutante...");
        progressDialogForForm.show();

        Intent it = getIntent();
        String mutantId = String.valueOf(it.getIntExtra("mutantId", 0));
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, MainActivity.GENERAL_MUTANT_URL  + mutantId,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            progressDialogForForm.dismiss();

                            String obj = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            JSONObject json = new JSONObject(obj);
                            int responseStatus = Integer.valueOf(json.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante atualzado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", json.getString("erro"), "Entendi!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        displayAlert("Erro!", "Erro de conexão com servidor.", "Entendi!");
                        progressDialogForForm.dismiss();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mutant = new HashMap<>();
                mutant.put("name", mutantName.getText().toString());
                mutant.put("power1", mutantSkill1.getText().toString());
                mutant.put("power2", mutantSkill2.getText().toString());
                mutant.put("power3", mutantSkill3.getText().toString());
                mutant.put("user_id", MainActivity.userId);
                return mutant;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                try {
                    params.put("picture", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                } catch (Exception e){
                    e.printStackTrace();
                }
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}
