package vn.siliconstraits.airlight;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main extends Activity {

    public static String urlDemo = "https://dl.dropboxusercontent.com/u/86183939/AirLight/sss-office.txt";
    public static String urlDemo1 = "https://dl.dropboxusercontent.com/u/86183939/AirLight/demo.txt";
    private Context context;
    private ProgressDialog pd;
    Button btnDemo;
    Button btnSave;
    Button btnPaste;
    EditText txtUrl;
    TextView txtContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnDemo = (Button) findViewById(R.id.btnDemo);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnPaste = (Button) findViewById(R.id.btnPaste);
        txtUrl = (EditText) findViewById(R.id.txtUrl);
        txtContact = (TextView) findViewById(R.id.txtContact);

        //set onclick demo button
        btnDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                AsyncTask<Void, Void, String> taskDemo = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        pd = new ProgressDialog(context);
                        pd.setTitle("Processing...");
                        pd.setMessage("Please wait.");
                        pd.setCancelable(true);
                        pd.setIndeterminate(true);
                        pd.show();
                    }

                    @Override
                    protected String doInBackground(Void... voids) {
                        String data = getDataDemo(urlDemo1);
                        //Log.i("Demo", data.toString());
                        return data;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        pd.dismiss();
                        super.onPostExecute(result);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("data", result);
                        startActivity(intent);
                    }
                };
                taskDemo.execute();
            }
        });

        //set on click save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check input validate
                if ((txtUrl.getText().toString().equals("")) || !txtUrl.getText().toString().contains("http") || !txtUrl.getText().toString().contains("txt")) {
                    Toast.makeText(getApplicationContext(), R.string.url_wrong, 1000).show();
                }
                //execute url
                else {
                    AsyncTask<Void, Void, String> taskDemo = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                            pd = new ProgressDialog(context);
                            pd.setTitle("Processing...");
                            pd.setMessage("Please wait.");
                            pd.setCancelable(true);
                            pd.setIndeterminate(true);
                            pd.show();
                        }

                        @Override
                        protected String doInBackground(Void... voids) {
                            String data = getDataDemo(txtUrl.getText().toString());
                            //Log.i("Demo", data.toString());
                            return data;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            pd.dismiss();
                            super.onPostExecute(result);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("data", result);
                            startActivity(intent);

                        }
                    };
                    taskDemo.execute();
                }
            }
        });
        //paste data from clipboard
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                try {
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                                .getSystemService(context.CLIPBOARD_SERVICE);
                        txtUrl.setText(clipboard.getText().toString());

                    } else {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        txtUrl.setText(clipboard.getText().toString());

                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No data to paste", 1000).show();
                    e.printStackTrace();
                }
            }
        });

        //send email to SSS
        txtContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                    intent.putExtra(Intent.EXTRA_TEXT, "Body of email");
                    intent.setData(Uri.parse("mailto:" + txtContact.getText().toString())); // or just "mailto:" for blank
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                    startActivity(intent);

                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);

                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        //restore default state
        if (pd != null) {
            pd.dismiss();
            btnDemo.setEnabled(true);
        }
        super.onDestroy();
    }

    public String getDataDemo(String url) {
        String responseString = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                String s = EntityUtils.toString(entity);
                if (s != "") {
                    try {
                        responseString = s.toString();
                        FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/arduino.txt"));
                        out.write(responseString.getBytes());
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }
}