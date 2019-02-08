package com.techandlearn.firebaseauth;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import static com.techandlearn.firebaseauth.Constants.DELAY;

public class SingUpActivity extends AppCompatActivity implements Animator.AnimatorListener {

    private View rootView;
    private int revelX, revelY;
    private RelativeLayout layoutContent;
    private LinearLayout layoutEdit;

    private FirebaseAuth mAuth;
    private EditText edName, edEmail, edPassword;
    private Button btnSingUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        mAuth = FirebaseAuth.getInstance();
        initTheViews();

        Intent intent = getIntent();
        if (savedInstanceState == null
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(Constants.EXTRA_REVEL_X)
                && intent.hasExtra(Constants.EXTRA_REVEL_Y)) {

            rootView.setVisibility(View.INVISIBLE);
            revelX = intent.getIntExtra(Constants.EXTRA_REVEL_X, 0);
            revelY = intent.getIntExtra(Constants.EXTRA_REVEL_Y, 0);

            ViewTreeObserver treeObserver = rootView.getViewTreeObserver();
            if (treeObserver.isAlive()) {
                treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revelActivity(revelX, revelY);
                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
            else {
                rootView.setVisibility(View.VISIBLE);
            }
        }

        ImageView imageView = findViewById(R.id.imgBack);
        Glide.with(this).load(R.drawable.night_camp).into(imageView);
        bindBtnSingUpClick();


    }

    private void revelActivity(int revelX, int revelY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float radius = (float) Math.max(rootView.getWidth(), rootView.getHeight() * 1.1);
            Animator animator = ViewAnimationUtils.createCircularReveal(rootView, revelX, revelY, radius, 0);
            animator.setDuration(600);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addListener(this);
            rootView.setVisibility(View.VISIBLE);
            animator.start();
        }else {
            finish();
        }
    }

    private void initTheViews() {
        rootView = findViewById(R.id.rootView);
        layoutContent = findViewById(R.id.contentLayout);
        layoutEdit = findViewById(R.id.layoutEdit);

        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnSingUp = findViewById(R.id.btnSingUp);
    }


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        layoutContent.setVisibility(View.VISIBLE);
        animateEditLayout();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private void animateEditLayout() {
        for (int i = 0; i < layoutEdit.getChildCount(); i++) {
            View child = layoutEdit.getChildAt(i);
            contentAnimation(child, (100 + i * DELAY));

        }
    }

    private void contentAnimation(View view, int delay) {
        int imgMove = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new AlphaAnimation(0, 1f));
        animationSet.addAnimation(new TranslateAnimation(0, 0, imgMove, 0));
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setStartOffset(delay);
        animationSet.setDuration(2 * delay); //
        view.startAnimation(animationSet);
    }

    private void existRevel(View view) {
        int revelX = (int) (view.getX() + view.getWidth() / 2);
        int revelY = (int) (view.getY() + view.getHeight() / 2);

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, view, "Button");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_REVEL_X, revelX);
        intent.putExtra(Constants.EXTRA_REVEL_Y, revelY);

        ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
    }

    @Override
    public void onBackPressed() {
        existRevel(rootView);
    }

    private void bindBtnSingUpClick(){
        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edName.getText().toString())){
                    Toast.makeText(SingUpActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edEmail.getText().toString())){
                    Toast.makeText(SingUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edPassword.getText().toString())){
                    Toast.makeText(SingUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = edName.getText().toString();
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();

                btnSingUp.setClickable(false);

                singUpUser(name,email,password);
            }
        });
    }

    private void singUpUser(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    updateUserName(name,user);
                }else {
                    Toast.makeText(SingUpActivity.this, "User not created", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserName(final String name, FirebaseUser user) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SingUpActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SingUpActivity.this,UserActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(SingUpActivity.this, "Some thing went wrong", Toast.LENGTH_SHORT).show();
                    btnSingUp.setClickable(true);
                }
            }
        });
    }

}




