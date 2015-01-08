package cc.dasa.guitarscales;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GuitarView extends View {

    private Paint mPaint = new Paint();
    private GestureDetector mDetector;

    private int mFrets = 20;
    private Note[] mNotes = {Note.E, Note.A, Note.D, Note.G, Note.B, Note.E};
    private List<Note> mSelections = new ArrayList<>();

    public GuitarView(Context context) {
        super(context);
        init();
    }

    public GuitarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mDetector = new GestureDetector(getContext(), new GestureListener());
    }

    public void setNotes(Note... notes) {
        mNotes = notes;
        invalidate();
    }

    public Note[] getNotes() {
        return mNotes;
    }

    public void setFrets(int frets) {
        mFrets = frets;
        invalidate();
    }

    public int getFrets() {
        return mFrets;
    }

    public void addSelections(Note... notes) {
        for (Note note : notes) {
            if (!mSelections.contains(note)) {
                mSelections.add(note);
            }
        }
        invalidate();
    }

    public void removeSelections(Note... notes) {
        for (Note note : notes) {
            if (mSelections.contains(note)) {
                mSelections.remove(note);
            }
        }
        invalidate();
    }

    public void clearSelections() {
        mSelections.clear();
        invalidate();
    }

    public List<Note> getSelections() {
        return mSelections;
    }

    public void toggleSelections(Note... notes) {
        for (Note note : notes) {
            if (mSelections.contains(note)) {
                mSelections.remove(note);
            } else {
                mSelections.add(note);
            }
        }
        invalidate();
    }

    private Point getSpacing() {
        return new Point(getWidth() / mNotes.length, getHeight() / mFrets);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);

        Point sp = getSpacing();

        int xOffset = sp.x / 2;
        int yOffset = sp.y / 2;

        for (Note note : mNotes) {
            canvas.drawLine(xOffset, 0, xOffset, getHeight(), mPaint);
            xOffset += sp.x;
        }

        for (int i = 0; i < mFrets; i++) {
            if (i == 0) {
                mPaint.setStrokeWidth(8);
            } else {
                mPaint.setStrokeWidth(2);
            }

            canvas.drawLine(sp.x / 2, yOffset, getWidth() - (sp.x / 2), yOffset, mPaint);

            int markerR = 10;
            if (i == 3 || i == 5 || i == 7 || i == 9 || i == 15 || i == 17) {
                canvas.drawCircle(getWidth() / 2, yOffset - (sp.y / 2), markerR, mPaint);
            }

            if (i == 12) {
                canvas.drawCircle(sp.x, yOffset - (sp.y / 2), markerR, mPaint);
                canvas.drawCircle(getWidth() - sp.x, yOffset - (sp.y / 2), markerR, mPaint);
            }

            yOffset += sp.y;
        }

        xOffset = sp.x / 2;
        for (Note note : mNotes) {
            yOffset = sp.y / 2;
            for (int i = 0; i <= mFrets; i++) {
                Note cur = note.Step(i);
                if (mSelections.contains(cur)) {
                    float cx = xOffset;
                    float cy = yOffset - (sp.y / 2);
                    float radius = sp.y / 5;
                    canvas.drawCircle(cx, cy, radius, mPaint);
                    mPaint.setColor(Color.WHITE);
                    mPaint.setTextSize(radius);
                    canvas.drawText(cur.getName(), cx - (radius / 2.5f), cy + (radius / 2.5f), mPaint);
                    mPaint.setColor(Color.BLACK);
                }
                yOffset += sp.y;
            }
            xOffset += sp.x;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private class GestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Point sp = getSpacing();

            float x = e.getX();
            float y = e.getY() + (sp.y / 2);

            Note note = mNotes[((int) (x / sp.x))];
            toggleSelections(note.Step((int) (y / sp.y)));

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }
}
