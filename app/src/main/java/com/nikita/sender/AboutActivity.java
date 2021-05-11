package com.nikita.sender;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.cunoraz.gifview.library.GifView;

public class AboutActivity extends FragmentActivity {

    GifView avatar;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        InitComponents();
    }

    private void InitComponents() {
        avatar = findViewById(R.id.avatarGif);
        avatar.setGifResource(R.drawable.avatar);
        avatar.setVisibility(View.VISIBLE);
        avatar.play();

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });
    }
}