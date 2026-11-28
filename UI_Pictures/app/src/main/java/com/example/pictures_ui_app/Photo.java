package com.example.pictures_ui_app;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

final class Photo {
    @DrawableRes final int image;
    @StringRes final int title;
    @StringRes final int note;
    final String date;

    private Photo(@DrawableRes int image, @StringRes int title, @StringRes int note, String date) {
        this.image = image;
        this.title = title;
        this.note = note;
        this.date = date;
    }

    static final Photo[] GALLERY = {
        new Photo(R.drawable.photo_1, R.string.title_1, R.string.note_1, "20/11/2026"),
        new Photo(R.drawable.photo_2, R.string.title_2, R.string.note_2, "20/11/2026"),
        new Photo(R.drawable.photo_3, R.string.title_3, R.string.note_3, "20/11/2026"),
        new Photo(R.drawable.photo_4, R.string.title_4, R.string.note_4, "19/11/2026"),
        new Photo(R.drawable.photo_5, R.string.title_5, R.string.note_5, "19/11/2026"),
        new Photo(R.drawable.photo_6, R.string.title_6, R.string.note_6, "19/11/2026"),
        new Photo(R.drawable.photo_7, R.string.title_7, R.string.note_7, "18/11/2026"),
        new Photo(R.drawable.photo_8, R.string.title_8, R.string.note_8, "18/11/2026"),
        new Photo(R.drawable.photo_9, R.string.title_9, R.string.note_9, "18/11/2026"),
        new Photo(R.drawable.photo_10, R.string.title_10, R.string.note_10, "17/11/2026"),
        new Photo(R.drawable.photo_11, R.string.title_11, R.string.note_11, "17/11/2026"),
        new Photo(R.drawable.photo_12, R.string.title_12, R.string.note_12, "17/11/2026")
    };
}
