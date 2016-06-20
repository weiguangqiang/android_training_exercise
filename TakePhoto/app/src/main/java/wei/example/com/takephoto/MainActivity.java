package wei.example.com.takephoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button capturePhotoBtn = null; // 使用startActivityForResult()的方法获取Photo
    private ImageView showPhotoImageView = null;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capturePhotoBtn = (Button) findViewById(R.id.capture_photo_with_activity_for_result_btn);
        showPhotoImageView = (ImageView) findViewById(R.id.show_photo_img);

        capturePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePhotoIntent.resolveActivity(getPackageManager()) != null) { // 检测是否有响应MediaStore.ACTION_IMAGE_CAPTURE的应用，防止崩溃
                    startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            /* 1. 显示thumbnail大小的图片 */
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            showPhotoImageView.setVisibility(View.VISIBLE);
            showPhotoImageView.setImageBitmap(imageBitmap);
            /*****************************/
        }
    }
}
