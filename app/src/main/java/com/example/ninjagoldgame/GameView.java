package com.example.ninjagoldgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRationX, screenRationY;
    private Paint paint;
    private Bird[] birds;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    public Flight flight;
    private GameActivity activity;
    private BackGround backGround1, backGround2;
    private int sound;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);
        this.activity = activity;
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        }else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;

        screenRationX = 1920f / screenX;
        screenRationY = 1080f / screenY;

        backGround1 = new BackGround(screenX, screenY, getResources());
        backGround2 = new BackGround(screenX, screenY, getResources());

        flight = new Flight(this, screenY, getResources());

        bullets = new ArrayList<>();
        backGround2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);
        birds = new Bird[4];

        for(int i = 0; i < 4; i++){
            Bird bird = new Bird(getResources());
            birds[i] = bird;
        }

        random = new Random();
    }

    @Override
    public void run() {
        while(isPlaying){
            update ();
            draw ();
            sleep ();
        }
    }

    private void update(){
        //Denotes 10 pixel towards left
        backGround1.x -= 10 * screenRationX;
        backGround2.x -= 10 * screenRationX;

        if(backGround1.x + backGround1.background.getWidth() < 0){
            backGround1.x = screenX;
        }
        if(backGround2.x + backGround2.background.getWidth() < 0){
            backGround2.x = screenX;
        }

        if(flight.isGoingUp)
            flight.y -= 30 * screenRationY;
        else
            flight.y += 30 * screenRationY;


        if(flight.y < 0)
            flight.y = 0;

        if(flight.y >= screenY - flight.height)
            flight.y = screenY - flight.height;

        List<Bullet> trash = new ArrayList<>();

        for(Bullet bullet: bullets){
            if(bullet.x > screenX)
                trash.add(bullet);
            bullet.x += 50 * screenRationX;

            for(Bird bird: birds){
                if(Rect.intersects(bird.getCollisionShape(), bullet.getCollisionShape())){
                    score++;
                    bird.x = -500;
                    bullet.x = screenX + 500;
                    bird.wasShot = true;
                }
            }
        }

        for(Bullet bullet: trash)
            bullets.remove(bullet);

        for(Bird bird : birds){
            bird.x -= bird.speed;

            if(bird.x + bird.width < 0){
                if(!bird.wasShot){
                    isGameOver = true;
                    return;
                }

                int bound = (int) (30 * screenRationX);
                bird.speed = random.nextInt(bound);

                if(bird.speed < 10 * screenRationX){
                    bird.speed = (int) (10 * screenRationX);
                }
                bird.x = screenX;
                bird.y = random.nextInt(screenY - bird.height);
                bird.wasShot = false;
            }

            if(Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())){
                isGameOver = true;
                return;
            }
        }
    }

    private void draw(){
        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(backGround1.background, backGround1.x, backGround1.y, paint);
            canvas.drawBitmap(backGround2.background, backGround1.x, backGround2.y, paint);

            for(Bird bird : birds){
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
            }

            canvas.drawText(score + "", screenX/2f, 164, paint);

            if(isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting();
                return;
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for(Bullet bullet: bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void waitBeforeExiting() {
        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("scores");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HashMap map = new HashMap();
        map.put("name", user.getEmail());
        map.put("score", score);
        myRef.push().setValue(map);

        if(prefs.getInt("highscore", 0) < score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }

    private void sleep(){
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() < screenX / 2){
                    flight.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false;
                if(event.getX() > screenX / 2){
                    flight.toShoot++;
                }
                break;
        }
        return true;
    }

    public void newBullet() {
        if(!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 1, 1, 0, 0, 1);
        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);
    }
}
