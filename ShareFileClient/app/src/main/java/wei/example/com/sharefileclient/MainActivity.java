package wei.example.com.sharefileclient;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ShareFileClientTag";

    private Intent mRequestFileIntent = null;
    private ParcelFileDescriptor mInputPFD = null;

    private Button requestBtn = null;
    private TextView content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestBtn = (Button) findViewById(R.id.request_btn);
        content = (TextView) findViewById(R.id.content);

        mRequestFileIntent = new Intent(Intent.ACTION_PICK);
        mRequestFileIntent.setType("text/plain");

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFile();
            }
        });
    }

    protected void requestFile() {
        startActivityForResult(mRequestFileIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            Log.i(TAG, "MainActivity # onActivityResult # resultCode != OK!!!");
            return;
        } else {
            Uri returnUri = data.getData();
            String mimeType = getContentResolver().getType(returnUri);
            Log.i(TAG, "MainActivity # onActivityResult # returnUri: " + returnUri + "; mimeType: " + mimeType);
            try{

                // 使用 FileProvider的query()方法，获取文件名和文件大小
                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                /*
                 * Get the column indexes of the data in the Cursor,
                 * move to the first row in the Cursor, get the data,
                 * and display it.
                 */
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                StringBuilder builder = new StringBuilder();
                builder.append("name: " + returnCursor.getString(nameIndex));
                builder.append("; size: " + returnCursor.getLong(sizeIndex));

                content.setText(builder.toString());

                // 获取 FileDescriptor
                mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "File Not Found!!!");
                return;
            }
        }

        FileDescriptor fd = mInputPFD.getFileDescriptor();
        Log.i(TAG, "MainActivity # onActivityResult # fd: " + fd + "; valid: " + fd.valid());

        /*if(fd.valid()) {
            content.setText(fd.toString());
        } else {
            Toast.makeText(this, "FileDescriptor Not Valid!!!", Toast.LENGTH_SHORT).show();
        }*/
    }
}
