package com.joseph.antsome_todo_prj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

// 인트로 화면 구현하는 액티비티
public class LoadingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // 이미지뷰 gif 파일을 쓰기 위한 Glide 객체 생성
        ImageView splashGif = (ImageView)findViewById(R.id.splash_gif_view);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(splashGif);
        Glide.with(this).load(R.raw.react2).into(splashGif);

        //로딩 시작 하는 함수 호출
        startLoading();
    }

    // 핸들러를 이용해 로딩을 시작하고 5초뒤에 intent로 메인액티비티로 이동한다.
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run(){
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}