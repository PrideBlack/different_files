package com.example.sendtextactivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class MainActivity extends AppCompatActivity {

    private Button send_button;
    private Button photo_button;
    private TextInputEditText text_field;
    private TextView result;
    private String result_text;
    private Bitmap image_to_text;
    private int RESULT_LIMIT_LENGTH = 100;
    final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_button = findViewById(R.id.send_button);
        photo_button = findViewById(R.id.photo_button);
        text_field = findViewById(R.id.text_input_edit);
        result = findViewById(R.id.result);
        result_text = "TODO"; //TODO

        View.OnClickListener onClickSend = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_text = text_field.getText().toString();
                if(result_text.length() > RESULT_LIMIT_LENGTH) {
                    result_text = result_text.substring(0, RESULT_LIMIT_LENGTH);
                }
                result.setText(result_text);
            }
        };
        send_button.setOnClickListener(onClickSend);

        View.OnClickListener onClickPhoto = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        };
        photo_button.setOnClickListener(onClickPhoto);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            image_to_text = (Bitmap) extras.get("data");
            try {
                getTextFromImage(image_to_text);
            } catch (Exception e) {
                text_field.setText("error");
            }
        }
    }

    public void changeTextIntoTextField(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_field.setText(text);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTextFromImage (Bitmap image) throws Exception {
        OCRRestAPI.PhotoToText(image, new CallBack() {
            @Override
            public void onSuccess(String res) {
                changeTextIntoTextField(res);
            }

            @Override
            public void onFail(String error) {
                changeTextIntoTextField(error);
            }
        });
    }

/*    private String getTextFromImage (Bitmap image) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()) {
            Toast.makeText(getApplicationContext(), "Could not get the text", Toast.LENGTH_SHORT).show();
            return "Could not get the text";
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(image).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < items.size(); i++) {
                sb.append(items.valueAt(i).getValue());
                sb.append("\n");
            }
            return sb.toString();
        }
    }*/
}

