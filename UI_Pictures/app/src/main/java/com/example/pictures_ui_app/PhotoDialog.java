package com.example.pictures_ui_app;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AtomicFile;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoDialog extends DialogFragment {
    private static final String POSITION = "position";
    private static final String BACK_SIDE = "back_side";

    public PhotoDialog() {
        super(R.layout.dialog_photo);
    }

    static PhotoDialog newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        PhotoDialog dialog = new PhotoDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setStyle(STYLE_NO_TITLE, R.style.Theme_AnhLat_Preview);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle state) {
        super.onViewCreated(view, state);
        Photo photo = Photo.GALLERY[requireArguments().getInt(POSITION)];
        EasyFlipView card = view.findViewById(R.id.flip_card);
        ImageView image = view.findViewById(R.id.preview_image);
        TextView frontDate = view.findViewById(R.id.front_date);
        TextView backDate = view.findViewById(R.id.back_date);
        TextView noteTitle = view.findViewById(R.id.note_title);
        TextView noteBody = view.findViewById(R.id.note_body);

        image.setClipToOutline(true);
        Glide.with(this).load(photo.image).fitCenter().into(image);
        image.setContentDescription(getString(R.string.preview_photo, getString(photo.title)));
        frontDate.setText(photo.date);
        backDate.setText(photo.date);
        noteTitle.setText(photo.title);
        noteBody.setText(photo.note);
        noteBody.setOnClickListener(clicked -> card.flipTheView());

        image.setOnClickListener(clicked -> card.flipTheView());
        view.findViewById(R.id.show_note).setOnClickListener(clicked -> card.flipTheView());
        view.findViewById(R.id.show_photo).setOnClickListener(clicked -> card.flipTheView());
        view.findViewById(R.id.close).setOnClickListener(clicked -> dismiss());

        if (state != null && state.getBoolean(BACK_SIDE)) {
            card.flipTheView(false);
        }

        bindShareControls(view, photo);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = requireDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setDimAmount(0.82f);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        View view = getView();
        if (view != null) {
            EasyFlipView card = view.findViewById(R.id.flip_card);
            outState.putBoolean(BACK_SIDE, card.isBackSide());
        }
    }

    private void bindShareControls(View view, Photo photo) {
        SharedPreferences prefs = requireContext().getSharedPreferences("photo_sharing", Context.MODE_PRIVATE);
        String key = getResources().getResourceEntryName(photo.image);
        SwitchCompat allowView = view.findViewById(R.id.allow_view);
        ImageButton share = view.findViewById(R.id.share);

        allowView.setChecked(prefs.getBoolean(key, false));
        share.setAlpha(allowView.isChecked() ? 1f : 0.55f);
        allowView.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean(key, checked).apply();
            share.setAlpha(checked ? 1f : 0.55f);
        });

    }

    @SuppressLint("ResourceType")
    private void sharePhoto(Photo photo, String filename) {
        Context context = requireContext();
        File directory = new File(context.getCacheDir(), "shared_photos");
        File imageFile = new File(directory, filename + ".jpg");

        try {
            if (!directory.isDirectory() && !directory.mkdirs()) {
                throw new IOException("Could not create the shared photo directory");
            }
            AtomicFile sharedFile = new AtomicFile(imageFile);
            try (InputStream source = getResources().openRawResource(photo.image)) {
                FileOutputStream destination = sharedFile.startWrite();
                try {
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = source.read(buffer)) != -1) {
                        destination.write(buffer, 0, count);
                    }
                    sharedFile.finishWrite(destination);
                } catch (IOException error) {
                    sharedFile.failWrite(destination);
                    throw error;
                }
            }

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".files", imageFile);
            String text = getString(R.string.share_text, getString(photo.title),
                    photo.date, getString(photo.note));
            Intent share = new Intent(Intent.ACTION_SEND)
                    .setType("image/jpeg")
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .putExtra(Intent.EXTRA_TEXT, text)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setClipData(ClipData.newRawUri(getString(photo.title), uri));
            startActivity(Intent.createChooser(share, getString(R.string.share)));
        } catch (IOException | ActivityNotFoundException error) {
            Toast.makeText(context, R.string.share_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
