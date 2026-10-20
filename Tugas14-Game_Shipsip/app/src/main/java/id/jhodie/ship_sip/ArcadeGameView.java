package id.jhodie.ship_sip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class ArcadeGameView extends View implements SensorEventListener {

    private Bitmap shipBitmap;

    private Paint paint;
    private Random random;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor proximity;

    private float playerX;
    private float playerY;
    private float playerSize = 60;
    private float playerRotation = 0;

    private float obstacleX;
    private float obstacleY;
    private float obstacleSize = 80;
    private float obstacleSpeed = 9;

    private float coinX;
    private float coinY;
    private float coinRadius = 28;
    private float coinSpeed = 7;

    private int score = 0;
    private int highScore = 0;
    private int level = 1;

    private boolean gameOver = false;
    private boolean gameStarted = false;
    private boolean shieldActive = false;
    private long shieldEndTime = 0;

    private long lastScoreTime = 0;

    private boolean hasGyroscope = false;
    private boolean hasProximity = false;

    public ArcadeGameView(Context context) {
        super(context);

        paint = new Paint();
        paint.setAntiAlias(true);

        random = new Random();

        // INI TEMPAT YANG BENAR BUAT LOAD GAMBAR
        shipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            hasGyroscope = gyroscope != null;
            hasProximity = proximity != null;
        }

        resetObjects();
    }

    public void resumeGame() {
        if (sensorManager != null) {
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            }

            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
            }

            if (proximity != null) {
                sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        invalidate();
    }

    public void pauseGame() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void resetGame() {
        score = 0;
        level = 1;
        obstacleSpeed = 9;
        coinSpeed = 7;
        gameOver = false;
        gameStarted = true;
        shieldActive = false;
        shieldEndTime = 0;
        lastScoreTime = System.currentTimeMillis();

        playerX = getWidth() / 2f;
        playerY = getHeight() - 180;

        resetObjects();
        invalidate();
    }

    private void resetObjects() {
        obstacleX = 100;
        obstacleY = -300;

        coinX = 250;
        coinY = -800;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        if (playerX == 0) {
            playerX = getWidth() / 2f;
            playerY = getHeight() - 180;
            randomizeObstacle();
            randomizeCoin();
        }

        if (!gameStarted) {
            drawStartScreen(canvas);
            return;
        }

        drawHud(canvas);
        drawWatermark(canvas);
        drawPlayer(canvas);
        drawObstacle(canvas);
        drawCoin(canvas);

        if (!gameOver) {
            updateGame();
            invalidate();
        } else {
            drawGameOver(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.rgb(15, 23, 42));

        paint.setColor(Color.rgb(30, 41, 59));
        paint.setStrokeWidth(3);

        for (int i = 0; i < getHeight(); i += 80) {
            canvas.drawLine(getWidth() / 2f, i, getWidth() / 2f, i + 40, paint);
        }
    }

    private void drawStartScreen(Canvas canvas) {
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setColor(Color.WHITE);
        paint.setTextSize(48);
        paint.setFakeBoldText(true);
        canvas.drawText("Ship-sip", getWidth() / 2f, getHeight() / 2f - 120, paint);

        paint.setTextSize(28);
        paint.setFakeBoldText(false);
        paint.setColor(Color.rgb(203, 213, 225));
        canvas.drawText("Miringkan HP untuk bergerak", getWidth() / 2f, getHeight() / 2f - 55, paint);
        canvas.drawText("Putar HP untuk rotasi kapal", getWidth() / 2f, getHeight() / 2f - 15, paint);
        canvas.drawText("Dekatkan tangan ke sensor untuk shield", getWidth() / 2f, getHeight() / 2f + 25, paint);

        paint.setColor(Color.rgb(1, 176, 225));
        paint.setTextSize(32);
        paint.setFakeBoldText(true);
        canvas.drawText("Tap layar untuk mulai", getWidth() / 2f, getHeight() / 2f + 110, paint);

        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawHud(Canvas canvas) {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(true);
        paint.setTextSize(34);
        paint.setColor(Color.WHITE);
        canvas.drawText("Score: " + score, 35, 70, paint);

        paint.setTextSize(26);
        paint.setColor(Color.rgb(148, 163, 184));
        canvas.drawText("Level: " + level, 35, 110, paint);
        canvas.drawText("High: " + highScore, 35, 145, paint);

        paint.setTextSize(24);

        if (shieldActive) {
            paint.setColor(Color.rgb(34, 211, 238));
            canvas.drawText("SHIELD ACTIVE", 35, 185, paint);
        } else {
            paint.setColor(Color.rgb(148, 163, 184));
            canvas.drawText("Shield: proximity", 35, 185, paint);
        }

        paint.setTextSize(20);
        paint.setColor(Color.rgb(100, 116, 139));

        String gyroStatus = hasGyroscope ? "Gyro: aktif" : "Gyro: tidak tersedia";
        String proximityStatus = hasProximity ? "Proximity: aktif" : "Proximity: tidak tersedia";

        canvas.drawText(gyroStatus, 35, getHeight() - 80, paint);
        canvas.drawText(proximityStatus, 35, getHeight() - 50, paint);
    }

    private void drawPlayer(Canvas canvas) {
        if (shieldActive) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(70, 34, 211, 238));
            canvas.drawCircle(playerX, playerY, playerSize + 35, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.rgb(34, 211, 238));
            canvas.drawCircle(playerX, playerY, playerSize + 35, paint);

            paint.setStyle(Paint.Style.FILL);
        }

        if (shipBitmap != null) {
            float targetSize = playerSize * 2.2f;

            Bitmap scaledShip = Bitmap.createScaledBitmap(
                    shipBitmap,
                    (int) targetSize,
                    (int) targetSize,
                    true
            );

            Matrix matrix = new Matrix();

            matrix.postTranslate(
                    -targetSize / 2f,
                    -targetSize / 2f
            );

            matrix.postRotate(playerRotation);

            matrix.postTranslate(playerX, playerY);

            canvas.drawBitmap(scaledShip, matrix, paint);
        } else {
            drawDefaultShip(canvas);
        }
    }

    private void drawDefaultShip(Canvas canvas) {
        canvas.save();

        canvas.translate(playerX, playerY);
        canvas.rotate(playerRotation);

        Path ship = new Path();
        ship.moveTo(0, -playerSize);
        ship.lineTo(-playerSize * 0.75f, playerSize);
        ship.lineTo(0, playerSize * 0.55f);
        ship.lineTo(playerSize * 0.75f, playerSize);
        ship.close();

        paint.setColor(Color.rgb(1, 176, 225));
        canvas.drawPath(ship, paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(0, 10, 10, paint);

        canvas.restore();
    }

    private void drawObstacle(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(239, 68, 68));

        canvas.drawRoundRect(
                obstacleX,
                obstacleY,
                obstacleX + obstacleSize,
                obstacleY + obstacleSize,
                18,
                18,
                paint
        );

        paint.setColor(Color.rgb(127, 29, 29));
        paint.setTextSize(36);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("!", obstacleX + obstacleSize / 2f, obstacleY + obstacleSize / 2f + 13, paint);

        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawCoin(Canvas canvas) {
        paint.setColor(Color.rgb(250, 204, 21));
        canvas.drawCircle(coinX, coinY, coinRadius, paint);

        paint.setColor(Color.rgb(161, 98, 7));
        paint.setTextSize(28);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("+", coinX, coinY + 10, paint);

        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void updateGame() {
        long now = System.currentTimeMillis();

        if (now - lastScoreTime >= 1000) {
            score += 1;
            lastScoreTime = now;
        }

        level = 1 + score / 100;

        obstacleSpeed = 9 + (level * 1.2f);
        coinSpeed = 7 + (level * 0.8f);

        obstacleY += obstacleSpeed;
        coinY += coinSpeed;

        if (obstacleY > getHeight()) {
            randomizeObstacle();
            score += 5;
        }

        if (coinY > getHeight()) {
            randomizeCoin();
        }

        if (shieldActive && now > shieldEndTime) {
            shieldActive = false;
        }

        checkObstacleCollision();
        checkCoinCollision();

        if (score > highScore) {
            highScore = score;
        }
    }

    private void checkObstacleCollision() {
        float obstacleCenterX = obstacleX + obstacleSize / 2f;
        float obstacleCenterY = obstacleY + obstacleSize / 2f;

        float dx = playerX - obstacleCenterX;
        float dy = playerY - obstacleCenterY;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float hitRange = playerSize + obstacleSize / 2f - 15;

        if (distance < hitRange) {
            if (shieldActive) {
                shieldActive = false;
                randomizeObstacle();
                score += 15;
            } else {
                gameOver = true;
            }
        }
    }

    private void checkCoinCollision() {
        float dx = playerX - coinX;
        float dy = playerY - coinY;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < playerSize + coinRadius) {
            score += 25;
            randomizeCoin();
        }
    }

    private void randomizeObstacle() {
        obstacleY = -obstacleSize - random.nextInt(300);
        obstacleX = random.nextInt(Math.max(1, getWidth() - (int) obstacleSize));
    }

    private void randomizeCoin() {
        coinY = -500 - random.nextInt(800);
        coinX = coinRadius + random.nextInt(Math.max(1, getWidth() - (int) (coinRadius * 2)));
    }

    private void drawWatermark(Canvas canvas) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(false);
        paint.setTextSize(22);
        paint.setColor(Color.argb(120, 255, 255, 255));

        canvas.drawText(
                "© 2026 Jhodie Naufal Kertoprodjo",
                getWidth() / 2f,
                getHeight() - 24,
                paint
        );

        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawGameOver(Canvas canvas) {
        paint.setColor(Color.argb(210, 15, 23, 42));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setTextAlign(Paint.Align.CENTER);

        paint.setColor(Color.WHITE);
        paint.setTextSize(58);
        paint.setFakeBoldText(true);
        canvas.drawText("GAME OVER", getWidth() / 2f, getHeight() / 2f - 70, paint);

        paint.setTextSize(32);
        paint.setFakeBoldText(false);
        paint.setColor(Color.rgb(203, 213, 225));
        canvas.drawText("Score akhir: " + score, getWidth() / 2f, getHeight() / 2f - 15, paint);

        paint.setColor(Color.rgb(1, 176, 225));
        paint.setTextSize(30);
        paint.setFakeBoldText(true);
        canvas.drawText("Tap untuk main lagi", getWidth() / 2f, getHeight() / 2f + 60, paint);

        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!gameStarted || gameOver) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float tiltX = event.values[0];

            playerX -= tiltX * 9;

            if (playerX < playerSize) {
                playerX = playerSize;
            }

            if (playerX > getWidth() - playerSize) {
                playerX = getWidth() - playerSize;
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float rotationZ = event.values[2];

            playerRotation += rotationZ * 4;

            if (playerRotation > 25) {
                playerRotation = 25;
            }

            if (playerRotation < -25) {
                playerRotation = -25;
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];

            if (distance < event.sensor.getMaximumRange()) {
                shieldActive = true;
                shieldEndTime = System.currentTimeMillis() + 2500;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Tidak dipakai
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!gameStarted || gameOver) {
                resetGame();
                return true;
            }
        }

        return true;
    }
}