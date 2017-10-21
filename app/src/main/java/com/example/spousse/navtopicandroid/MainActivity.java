package com.example.spousse.navtopicandroid;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Image loading result to pass to startActivityForResult method.
    private static int LOAD_IMAGE_RESULTS = 1;

    // GUI components
    private Button button;	// The button
    private Button buttonGoTo; // THe Goto Button
    private TextView textView; // The textview
    private ImageView image;// ImageView

    private static final String TAG = MainActivity.class.getSimpleName();

    // Méthode pour vérifier la permission d'écriture sur le téléphone
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find references to the GUI objects
        button = (Button)findViewById(R.id.button);
        buttonGoTo = (Button)findViewById(R.id.button2);
        textView = (TextView)findViewById(R.id.textView);
        image = (ImageView)findViewById(R.id.image);

        //hide the button GoTo
        buttonGoTo.setVisibility(View.INVISIBLE);

        // Set button's onClick listener object.
        button.setOnClickListener(this);
        buttonGoTo.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {

            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();

            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            // recupération des meta de la photo
            float[] latLong = null;
            boolean hasCoordinates = true; // TODO: 20/10/2017 Remettre à false
            ExifInterface exif = null;


            try {
                exif = new ExifInterface(imagePath);
                //hasCoordinates = exif.getLatLong(latLong);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Now we need to set the GUI ImageView data with data read from the picked file.
            image.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            String latitude = "48.122846";
            String longitude = "-1.770979";


            if(hasCoordinates) {
                //longitude = String.valueOf(latLong[0]);
                //latitude = String.valueOf(latLong[1]);
            } else {
                //longitude = latitude = "None";
            }
            textView.setText("Largeur de l'image : " + exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) + "\n" +
                    "Poids de l'image : " + exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + "\n" +
                    "Date image : " + exif.getAttribute(ExifInterface.TAG_DATETIME) + "\n" +
                    "Coordonnée GPS : " + longitude + "," + latitude + "\n" +
                    "Orientation : " + exif.getAttribute(ExifInterface.TAG_ORIENTATION) + "\n"
            );

            // Display button to navigate to the place
            if(hasCoordinates) {
                buttonGoTo.setVisibility(View.VISIBLE);


            }
            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case  R.id.button : //clic sur le bouton upload

                // Create the Intent for Image Gallery.
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
                startActivityForResult(i, LOAD_IMAGE_RESULTS);

                break;

            case R.id.button2 : //clic sur le bouton "y aller"
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=latitude,longitude"));
                startActivity(intent);
                break;
        }
    }

}
