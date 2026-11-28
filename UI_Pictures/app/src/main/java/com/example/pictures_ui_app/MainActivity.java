package com.example.pictures_ui_app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private static final String PREVIEW_TAG = "photo_preview";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        TextView count = findViewById(R.id.photo_count);
        count.setText(getString(R.string.photo_count, Photo.GALLERY.length));

        RecyclerView grid = findViewById(R.id.photo_grid);
        Configuration config = getResources().getConfiguration();
        int columns = config.screenWidthDp >= 600 ? 5 : 3;
        grid.setLayoutManager(new GridLayoutManager(this, columns));
        grid.setAdapter(new PhotoAdapter(this::openPhoto));
    }

    private void openPhoto(int position) {
        FragmentManager fragments = getSupportFragmentManager();
        if (!fragments.isStateSaved() && fragments.findFragmentByTag(PREVIEW_TAG) == null) {
            PhotoDialog.newInstance(position).showNow(fragments, PREVIEW_TAG);
        }
    }
}
