package com.knoxpo.rajivsonawala.photogalleryupdate;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends SingleFragmentActivity {

        protected Fragment createFragment() {
            return PhotoGalleryFragment.newInstance();
        }

}
