package pis03_2016.savealltherobots.view.viewclass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class TypeFaceTextView extends android.support.v7.widget.AppCompatTextView {

    /**
     * Stroke of the paint
     */
    private static final Paint BLACK_BORDER_PAINT = getWhiteBorderPaint();
    private static final int BORDER_WIDTH = 1;

    /**
     *
     */
    static {
        BLACK_BORDER_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    /**
     * The Typeface the class stores
     */
    private Typeface typeface;

    /**
     * Constructor
     *
     * @param context context
     */
    public TypeFaceTextView(Context context) {
        super(context);
    }

    /**
     * Constructor
     *
     * @param context context
     * @param attrs   some attributes
     */
    public TypeFaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDrawingCacheEnabled(false);

        setTypeface(attrs);
    }

    /**
     * Constructor
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public TypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(attrs);
    }

    /**
     * how the new TextView is painted
     *
     * @return Paint
     */
    private static Paint getWhiteBorderPaint() {
        return new Paint(Color.WHITE);
    }

    /**
     * Te new text
     *
     * @param text text
     * @param type type
     */
    @Override
    public void setText(CharSequence text, BufferType type) {

        super.setText(text.toString(), type);
    }

    /**
     * Sets the attributes
     *
     * @param attrs attrs
     */
    private void setTypeface(AttributeSet attrs) {
        final String typefaceFileName = attrs.getAttributeValue(null, "typeface");
        if (typefaceFileName != null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceFileName);
        }

        setTypeface(typeface);
    }

    @Override
    public void draw(Canvas aCanvas) {
        aCanvas.saveLayer(null, BLACK_BORDER_PAINT, Canvas.ALL_SAVE_FLAG);

        drawBackground(aCanvas, -BORDER_WIDTH, -BORDER_WIDTH);
        drawBackground(aCanvas, BORDER_WIDTH + BORDER_WIDTH, 0);
        drawBackground(aCanvas, 0, BORDER_WIDTH + BORDER_WIDTH);
        drawBackground(aCanvas, -BORDER_WIDTH - BORDER_WIDTH, 0);

        aCanvas.restore();
        super.draw(aCanvas);
    }

    /**
     * Establish the background
     *
     * @param aCanvas aCanvas
     * @param aDX     aDX
     * @param aDY     aDY
     */
    private void drawBackground(Canvas aCanvas, int aDX, int aDY) {
        aCanvas.translate(aDX, aDY);
        super.draw(aCanvas);
    }
}
