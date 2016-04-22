package cameragaleryupload.cristoforideveloper.com.cameraandgaleryupload;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton sendImgBtn;
    private ImageView imgPreview;
    private Uri fileUri;
    private String picturePath;
    private Uri selectedImage;
    private Bitmap photo;

    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendImgBtn = (ImageButton) findViewById(R.id.student_card_change_avatar);
        imgPreview = (ImageView) findViewById(R.id.image_prev);

        sendImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

    }

    private static Uri outputFileUri;

    private void openImageIntent() {

        outputFileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                ".temp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        // Camera
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("scale", true);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("return-data", true);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        galleryIntent.putExtra("crop", "true");
        galleryIntent.putExtra("scale", true);
        galleryIntent.putExtra("aspectX", 1);
        galleryIntent.putExtra("aspectY", 1);
        galleryIntent.putExtra("outputX", 200);
        galleryIntent.putExtra("outputY", 200);
        galleryIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        galleryIntent.putExtra("return-data", true);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Escolha a fonte");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        chooserIntent.putExtra("crop", true);

        startActivityForResult(chooserIntent, IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    if (data.getAction() == null) {
                        isCamera = false;
                    } else {
                        isCamera = data.getAction().equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }

                }

                final Uri selectedImageUri;

                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data.getData();
                }


                //final Uri selectedImage = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                    imgPreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
