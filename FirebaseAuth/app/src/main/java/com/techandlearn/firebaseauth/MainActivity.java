package com.techandlearn.firebaseauth;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView imgBack, imgLogo;
    private View bottomView;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!= null){
            startActivity(new Intent(this,UserActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        initTheViews();

        Glide.with(this).load(R.drawable.night_camp).into(imgBack);
        Glide.with(this).load(R.drawable.logo).into(imgLogo);

        contentAnimation();
        bottomAnimation();

        findViewById(R.id.btnSingIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithRevel(v,SingInActivity.class);
            }
        });

        findViewById(R.id.btnSingUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithRevel(v,SingUpActivity.class);
            }
        });
    }

    private void initTheViews(){
        imgBack = findViewById(R.id.imgBack);
        imgLogo = findViewById(R.id.imgLogo);
        bottomView = findViewById(R.id.layoutBottom);
    }

    private void contentAnimation(){
        int imgMove = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,25,getResources().getDisplayMetrics());
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new AlphaAnimation(0,1f));
        animationSet.addAnimation(new TranslateAnimation(0,0,imgMove,0));
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setStartOffset(200);
        animationSet.setDuration(400);
        imgLogo.startAnimation(animationSet);
    }

    private void bottomAnimation(){

        int height =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,65,getResources().getDisplayMetrics());
        TranslateAnimation translateAnimation = new TranslateAnimation(0,0,height,0);
        translateAnimation.setDuration(550);
        translateAnimation.setStartOffset(500);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        bottomView.startAnimation(translateAnimation);
    }

    private void startActivityWithRevel(View view, Class targetClass){
        int revelX =(int) (view.getX() + view.getWidth() / 2);
        int revelY = (int) (view.getY() + view.getHeight() / 2);

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this,view,"Button");
        Intent intent = new Intent(this,targetClass);
        intent.putExtra(Constants.EXTRA_REVEL_X,revelX);
        intent.putExtra(Constants.EXTRA_REVEL_Y,revelY);

        ActivityCompat.startActivity(this,intent,optionsCompat.toBundle());

    }
}
