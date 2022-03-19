package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;

    static boolean shuffleBoolean = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        //memanggil method permission
        runTimePermission();




    }


    //Method untuk menampilkan permission
    public void runTimePermission() {
        Dexter.withContext(getApplicationContext())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        displaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        //Keeps asking for external storage permission
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }


    public ArrayList<File> findSong(File file) {

        //ArrayList untuk menampung lagu
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {

            //memambah direktory ke dalam arraylist
            if (singleFile.isDirectory() && !singleFile.isHidden()) {

                arrayList.addAll(findSong(singleFile));

            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".m4a")) {
                    arrayList.add(singleFile);
                }
            }
        }

        return arrayList;

    }

    public void displaySong() {


        final ArrayList<File> Songs = findSong(Environment.getExternalStorageDirectory());
        items = new String[Songs.size()];

        //Adding all the music file without extensions to ArrayList
        for (int i = 0; i < Songs.size(); i++) {
            items[i] = Songs.get(i).getName().toString().replace(".mp3", "").replace(".m4a", "");
        }

        //memanggil adapter dan memasukkan ke listView
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);


//        Implementing onClickListener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String songName = (String) listView.getItemAtPosition(i);

                //Calling the next intent and sending the Required Details to play the songs
                Intent intent = new Intent(getApplicationContext(), Player.class);
                intent.putExtra("songs", Songs);
                intent.putExtra("songname", songName);
                intent.putExtra("pos", i);
                startActivity(intent);
            }
        });




    }



    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView txtSong = view.findViewById(R.id.txtSong);
            txtSong.setSelected(true);
            txtSong.setText(items[position]);
            return view;
        }
    }
}