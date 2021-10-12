package pis03_2016.savealltherobots.view.viewclass;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class GifImageView extends ImageView {

    /**
     * Application context
     */
    private Context context;

    private Double scaleX, scaleY;
    private Movie movie;
    private long moviestart;

    /**
     * GifView Constructor.
     *
     * @param context context
     */
    public GifImageView(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * GifView Constructor.
     *
     * @param context context
     * @param attrs   attrs
     */
    public GifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * GifView Constructor.
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    /**
     * Set object dimension.
     *
     * @param widthMeasureSpec  is the width measure
     * @param heightMeasureSpec is the height measure
     */
   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }*/

    /**
     * Load Gif from resource files.
     *
     * @param id resource id
     */
    /*public void loadGIFResource(int id) {
        InputStream stream = this.context.getResources().openRawResource(id);
        this.movie = Movie.decodeStream(stream);
    }*/

    /**
     * Load Gif from resource files.
     *
     * @param id       resource id
     * @param activity is the current Activity
     */
    public void loadGIFResource(final int id, final Activity activity) {

        new Thread() {

            public void run() {

                try {

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            InputStream stream = context.getResources().openRawResource(id);
                            movie = Movie.decodeStream(stream);
                        }
                    });

                    Thread.sleep(1);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /**
     * My second alternative for Multi Thread
     *
     * @param id is the current id
     */
    public void loadGIFResource(final int id) {

        //Create runnable for run in background
        Runnable runnable = new Runnable() {
            public void run() {
                InputStream stream = context.getResources().openRawResource(id);
                movie = Movie.decodeStream(stream);
            }
        };

        //Use extern service executor
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(runnable);

        //Force shut down immediately
        executor.shutdownNow();
    }


    /**
     * Describes how the gif is drawn.
     *
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        super.onDraw(canvas);

        long currentTime = android.os.SystemClock.uptimeMillis();

        if (movie != null && this.moviestart == 0) {

            this.scaleX = (double) this.getWidth() / (double) this.movie.width();
            this.scaleY = (double) this.getHeight() / (double) this.movie.height();
            this.moviestart = currentTime;
        }

        if (movie != null) {
            if(this.movie.duration() != 0) {
                this.movie.setTime((int) ((currentTime - this.moviestart) % this.movie.duration()));
                canvas.scale(this.scaleX.floatValue(), this.scaleY.floatValue());
                this.movie.draw(canvas, this.scaleX.floatValue(), this.scaleY.floatValue());
            }
        }

        this.invalidate();
    }
}