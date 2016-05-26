package wei.example.com.sharefileserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MY_INTERNAL_FILE_NAME = "myfile";
    private static final String INTERNAL_FILE_CONTENT = "This is my internal file for test";

    private Button generateBtn = null;
    private ListView listView = null;

    private Intent mResultIntent = null;

    private File mPrivateRootDir = null;
    private File mFileDir = null;
    private File[] mFiles = null;
    private String[] mFilesName = null;

    private Uri fileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView(this);

        mResultIntent = new Intent("wei.example.com.sharefileserver.fileprovider.ACTION_RETURN_FILE");

        mPrivateRootDir = getFilesDir();
        mFileDir = new File(mPrivateRootDir, "images"); // 共享文件的路径
        mFiles = mFileDir.listFiles(); // 获取共享文件路径中的所有文件

        mFilesName = new String[mFiles.length];

        for (int i = 0; i < mFiles.length; i++) {
            mFilesName[i] = mFiles[i].getName(); // 获取共享文件的文件名
        }
        
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null);

        /*
         * Display the file names in the ListView mFileListView.
         * Back the ListView with the array mImageFilenames, which
         * you can create by iterating through mImageFiles and
         * calling File.getAbsolutePath() for each File
         */
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mFilesName));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /*
             * When a filename in the ListView is clicked, get its
             * content URI and send it to the requesting app
             */
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Get a File for the selected file name.
                 * Assume that the file names are in the
                 * mImageFilename array.
                 */
                File requestFile = new File(mFileDir, mFilesName[position]); // 必须是文件的完整路径
                /*
                 * Most file-related method calls need to be in
                 * try-catch blocks.
                 */
                // Use the FileProvider to get a content URI
                try {
                    fileUri = FileProvider.getUriForFile(MainActivity.this, "wei.example.com.sharefileserver.fileprovider", requestFile);
                    Toast.makeText(MainActivity.this, fileUri.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fileUri != null) {
                    // Grant temporary read permission to the content URI
                    mResultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // Put the Uri and MIME type in the result Intent
                    mResultIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                    // Set the result
                    MainActivity.this.setResult(Activity.RESULT_OK, mResultIntent);
                } else {
                    mResultIntent.setDataAndType(null, "");
                    MainActivity.this.setResult(Activity.RESULT_CANCELED, mResultIntent);
                }
            }
        });

    }

    private void initView(Context context) {
        generateBtn = (Button) findViewById(R.id.generate_file_btn);
        generateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generate_file_btn:
                generateInternalFile(this);
                Toast.makeText(this, "generate internal file successfully, please check.", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    /**
     * 生成Internal File
     * 路径：/data/data/<packeage_name>/files/
     */
    private void generateInternalFile(Context context) {
        File myFile = new File(context.getFilesDir(), MY_INTERNAL_FILE_NAME);

        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(MY_INTERNAL_FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(INTERNAL_FILE_CONTENT.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OnDoneBtn触发事件
     */
    public void onDoneClick(View v) {
        finish();
    }

}
