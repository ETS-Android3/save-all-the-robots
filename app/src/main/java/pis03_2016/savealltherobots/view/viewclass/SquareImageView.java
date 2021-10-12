package pis03_2016.savealltherobots.view.viewclass;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class SquareImageView extends ImageView {

    /**
     * Constructor
     *
     * @param context context
     */
    public SquareImageView(Context context) {
        super(context);
    }

    /**
     * SquareImage constructor
     *
     * @param context context
     * @param attrs   attrs
     */
    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * SquareImage constructor
     *
     * @param context context
     * @param attrs   attrs
     */
    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the new measure
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
