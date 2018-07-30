package com.knoxpo.rajivsonawala.photogalleryupdate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends SingleFragmentActivity {


        public static Intent newIntent(Context context){

            return new Intent(context,MainActivity.class);
        }

        protected Fragment createFragment() {
            return PhotoGalleryFragment.newInstance();
        }

}
