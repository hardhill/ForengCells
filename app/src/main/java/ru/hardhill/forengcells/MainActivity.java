package ru.hardhill.forengcells;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String NAMESPACE = "http://www.w3schools.com/xml/";
    private static final String MAIN_REQUEST_URL = "http://www.w3schools.com/xml/tempconvert.asmx";
    private static final String SOAP_ACTION = "http://www.w3schools.com/xml/FahrenheitToCelsius";

    TextView lblCelsius;
    Button bConvert;
    EditText edtForeng;

    private String celsius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtForeng = (EditText)findViewById(R.id.value_to_convert);
        bConvert = (Button)findViewById(R.id.bConvert);
        lblCelsius = (TextView)findViewById(R.id.answer);



        bConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtForeng.length() > 0) {
                    getCelsius(edtForeng.getText().toString());
                } else {
                    lblCelsius.setText("Fahrenheit value can not be empty.");
                }
            }
        });
    }

    //-----------------------------------------------------------------------------------------

    private final void getCelsius(final String toConvert) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                //SoapRequests ex = new SoapRequests();
                celsius = getCelsiusConversion(toConvert);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }
    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(MAIN_REQUEST_URL);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    public String getCelsiusConversion(String fValue) {
        String data = null;
        String methodname = "FahrenheitToCelsius";

        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("Fahrenheit", fValue);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        HttpTransportSE ht = getHttpTransportSE();

        try {
            ht.call(SOAP_ACTION, envelope);
            //testHttpResponse(ht);
            SoapPrimitive resultsString = (SoapPrimitive)envelope.getResponse();

//            List COOKIE_HEADER = (List)ht.getServiceConnection().getResponseProperties();
//
//            for (int i = 0; i < COOKIE_HEADER.size(); i++) {
//                String key = COOKIE_HEADER.get(i).getKey();
//                String value = COOKIE_HEADER.get(i).getValue();
//
//                if (key != null && key.equalsIgnoreCase("set-cookie")) {
//                    SoapRequests.SESSION_ID = value.trim();
//                    Log.v("SOAP RETURN", "Cookie :" + SoapRequests.SESSION_ID);
//                    break;
//                }
//            }
            data = resultsString.toString();

        } catch (SocketTimeoutException t) {
            t.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception q) {
            q.printStackTrace();
        }
        return data;
    }

    // === перехватывает сообщения от другого процесса
    public Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    lblCelsius.setText(celsius);
                    break;
            }
            return false;
        }
    });
}
