package com.example.trialproject3.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trialproject3.Adapter.SliderAdapter;
import com.example.trialproject3.R;
import com.google.android.material.slider.Slider;

public class OnBoardActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;

    Button btn;

    SliderAdapter sliderAdapter;

    Animation animation;

    TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar ni
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_board);

        //hide toolbar ni goys
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);

        btn = findViewById(R.id.get_started_btn);
        addDots(0);

        viewPager.addOnPageChangeListener(changeListener);

        //call adapter ni
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnBoardActivity.this, RegisterActivity.class));
            }
        });
    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.blue));
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDots(position);
            if (position == 0){
                btn.setVisibility(View.INVISIBLE);
            }else if(position == 1){
                btn.setVisibility(View.INVISIBLE);
            }else{
                animation = AnimationUtils.loadAnimation(OnBoardActivity.this,R.anim.slide_animation);
                btn.setAnimation(animation);
                btn.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}