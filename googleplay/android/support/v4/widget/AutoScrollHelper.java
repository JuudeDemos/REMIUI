package android.support.v4.widget;

import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.google.android.wallet.instrumentmanager.R;

public abstract class AutoScrollHelper implements OnTouchListener {
    private static final int DEFAULT_ACTIVATION_DELAY;
    private int mActivationDelay;
    private boolean mAlreadyDelayed;
    private boolean mAnimating;
    private final Interpolator mEdgeInterpolator;
    private int mEdgeType;
    private boolean mEnabled;
    private boolean mExclusive;
    private float[] mMaximumEdges;
    private float[] mMaximumVelocity;
    private float[] mMinimumVelocity;
    private boolean mNeedsCancel;
    private boolean mNeedsReset;
    private float[] mRelativeEdges;
    private float[] mRelativeVelocity;
    private Runnable mRunnable;
    private final ClampedScroller mScroller;
    private final View mTarget;

    private static class ClampedScroller {
        private long mDeltaTime;
        private int mDeltaX;
        private int mDeltaY;
        private int mEffectiveRampDown;
        private int mRampDownDuration;
        private int mRampUpDuration;
        private long mStartTime;
        private long mStopTime;
        private float mStopValue;
        private float mTargetVelocityX;
        private float mTargetVelocityY;

        public ClampedScroller() {
            this.mStartTime = Long.MIN_VALUE;
            this.mStopTime = -1;
            this.mDeltaTime = 0;
            this.mDeltaX = 0;
            this.mDeltaY = 0;
        }

        public void setRampUpDuration(int durationMillis) {
            this.mRampUpDuration = durationMillis;
        }

        public void setRampDownDuration(int durationMillis) {
            this.mRampDownDuration = durationMillis;
        }

        public void start() {
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mStopTime = -1;
            this.mDeltaTime = this.mStartTime;
            this.mStopValue = 0.5f;
            this.mDeltaX = 0;
            this.mDeltaY = 0;
        }

        public void requestStop() {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            this.mEffectiveRampDown = AutoScrollHelper.constrain((int) (currentTime - this.mStartTime), 0, this.mRampDownDuration);
            this.mStopValue = getValueAt(currentTime);
            this.mStopTime = currentTime;
        }

        public boolean isFinished() {
            return this.mStopTime > 0 && AnimationUtils.currentAnimationTimeMillis() > this.mStopTime + ((long) this.mEffectiveRampDown);
        }

        private float getValueAt(long currentTime) {
            if (currentTime < this.mStartTime) {
                return 0.0f;
            }
            if (this.mStopTime < 0 || currentTime < this.mStopTime) {
                return AutoScrollHelper.constrain(((float) (currentTime - this.mStartTime)) / ((float) this.mRampUpDuration), 0.0f, 1.0f) * 0.5f;
            }
            long elapsedSinceEnd = currentTime - this.mStopTime;
            return (AutoScrollHelper.constrain(((float) elapsedSinceEnd) / ((float) this.mEffectiveRampDown), 0.0f, 1.0f) * this.mStopValue) + (1.0f - this.mStopValue);
        }

        private float interpolateValue(float value) {
            return ((-4.0f * value) * value) + (4.0f * value);
        }

        public void computeScrollDelta() {
            if (this.mDeltaTime == 0) {
                throw new RuntimeException("Cannot compute scroll delta before calling start()");
            }
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            float scale = interpolateValue(getValueAt(currentTime));
            long elapsedSinceDelta = currentTime - this.mDeltaTime;
            this.mDeltaTime = currentTime;
            this.mDeltaX = (int) ((((float) elapsedSinceDelta) * scale) * this.mTargetVelocityX);
            this.mDeltaY = (int) ((((float) elapsedSinceDelta) * scale) * this.mTargetVelocityY);
        }

        public void setTargetVelocity(float x, float y) {
            this.mTargetVelocityX = x;
            this.mTargetVelocityY = y;
        }

        public int getHorizontalDirection() {
            return (int) (this.mTargetVelocityX / Math.abs(this.mTargetVelocityX));
        }

        public int getVerticalDirection() {
            return (int) (this.mTargetVelocityY / Math.abs(this.mTargetVelocityY));
        }

        public int getDeltaX() {
            return this.mDeltaX;
        }

        public int getDeltaY() {
            return this.mDeltaY;
        }
    }

    private class ScrollAnimationRunnable implements Runnable {
        private ScrollAnimationRunnable() {
        }

        public void run() {
            if (AutoScrollHelper.this.mAnimating) {
                if (AutoScrollHelper.this.mNeedsReset) {
                    AutoScrollHelper.this.mNeedsReset = false;
                    AutoScrollHelper.this.mScroller.start();
                }
                ClampedScroller scroller = AutoScrollHelper.this.mScroller;
                if (scroller.isFinished() || !AutoScrollHelper.this.shouldAnimate()) {
                    AutoScrollHelper.this.mAnimating = false;
                    return;
                }
                if (AutoScrollHelper.this.mNeedsCancel) {
                    AutoScrollHelper.this.mNeedsCancel = false;
                    AutoScrollHelper.this.cancelTargetTouch();
                }
                scroller.computeScrollDelta();
                AutoScrollHelper.this.scrollTargetBy(scroller.getDeltaX(), scroller.getDeltaY());
                ViewCompat.postOnAnimation(AutoScrollHelper.this.mTarget, this);
            }
        }
    }

    public abstract boolean canTargetScrollHorizontally(int i);

    public abstract boolean canTargetScrollVertically(int i);

    public abstract void scrollTargetBy(int i, int i2);

    static {
        DEFAULT_ACTIVATION_DELAY = ViewConfiguration.getTapTimeout();
    }

    public AutoScrollHelper(View target) {
        this.mScroller = new ClampedScroller();
        this.mEdgeInterpolator = new AccelerateInterpolator();
        this.mRelativeEdges = new float[]{0.0f, 0.0f};
        this.mMaximumEdges = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
        this.mRelativeVelocity = new float[]{0.0f, 0.0f};
        this.mMinimumVelocity = new float[]{0.0f, 0.0f};
        this.mMaximumVelocity = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
        this.mTarget = target;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int maxVelocity = (int) ((1575.0f * metrics.density) + 0.5f);
        int minVelocity = (int) ((315.0f * metrics.density) + 0.5f);
        setMaximumVelocity((float) maxVelocity, (float) maxVelocity);
        setMinimumVelocity((float) minVelocity, (float) minVelocity);
        setEdgeType(1);
        setMaximumEdges(Float.MAX_VALUE, Float.MAX_VALUE);
        setRelativeEdges(0.2f, 0.2f);
        setRelativeVelocity(1.0f, 1.0f);
        setActivationDelay(DEFAULT_ACTIVATION_DELAY);
        setRampUpDuration(500);
        setRampDownDuration(500);
    }

    public AutoScrollHelper setEnabled(boolean enabled) {
        if (this.mEnabled && !enabled) {
            requestStop();
        }
        this.mEnabled = enabled;
        return this;
    }

    public AutoScrollHelper setMaximumVelocity(float horizontalMax, float verticalMax) {
        this.mMaximumVelocity[0] = horizontalMax / 1000.0f;
        this.mMaximumVelocity[1] = verticalMax / 1000.0f;
        return this;
    }

    public AutoScrollHelper setMinimumVelocity(float horizontalMin, float verticalMin) {
        this.mMinimumVelocity[0] = horizontalMin / 1000.0f;
        this.mMinimumVelocity[1] = verticalMin / 1000.0f;
        return this;
    }

    public AutoScrollHelper setRelativeVelocity(float horizontal, float vertical) {
        this.mRelativeVelocity[0] = horizontal / 1000.0f;
        this.mRelativeVelocity[1] = vertical / 1000.0f;
        return this;
    }

    public AutoScrollHelper setEdgeType(int type) {
        this.mEdgeType = type;
        return this;
    }

    public AutoScrollHelper setRelativeEdges(float horizontal, float vertical) {
        this.mRelativeEdges[0] = horizontal;
        this.mRelativeEdges[1] = vertical;
        return this;
    }

    public AutoScrollHelper setMaximumEdges(float horizontalMax, float verticalMax) {
        this.mMaximumEdges[0] = horizontalMax;
        this.mMaximumEdges[1] = verticalMax;
        return this;
    }

    public AutoScrollHelper setActivationDelay(int delayMillis) {
        this.mActivationDelay = delayMillis;
        return this;
    }

    public AutoScrollHelper setRampUpDuration(int durationMillis) {
        this.mScroller.setRampUpDuration(durationMillis);
        return this;
    }

    public AutoScrollHelper setRampDownDuration(int durationMillis) {
        this.mScroller.setRampDownDuration(durationMillis);
        return this;
    }

    public boolean onTouch(View v, MotionEvent event) {
        boolean z = true;
        if (!this.mEnabled) {
            return false;
        }
        switch (MotionEventCompat.getActionMasked(event)) {
            case R.styleable.WalletImButtonBar_capitalizeButtonText /*0*/:
                this.mNeedsCancel = true;
                this.mAlreadyDelayed = false;
                break;
            case R.styleable.WalletImFormEditText_validatorErrorString /*1*/:
            case R.styleable.WalletImFormEditText_requiredErrorString /*3*/:
                requestStop();
                break;
            case R.styleable.WalletImFormEditText_validatorRegexp /*2*/:
                break;
        }
        this.mScroller.setTargetVelocity(computeTargetVelocity(0, event.getX(), (float) v.getWidth(), (float) this.mTarget.getWidth()), computeTargetVelocity(1, event.getY(), (float) v.getHeight(), (float) this.mTarget.getHeight()));
        if (!this.mAnimating && shouldAnimate()) {
            startAnimating();
        }
        if (!(this.mExclusive && this.mAnimating)) {
            z = false;
        }
        return z;
    }

    private boolean shouldAnimate() {
        ClampedScroller scroller = this.mScroller;
        int verticalDirection = scroller.getVerticalDirection();
        int horizontalDirection = scroller.getHorizontalDirection();
        return (verticalDirection != 0 && canTargetScrollVertically(verticalDirection)) || (horizontalDirection != 0 && canTargetScrollHorizontally(horizontalDirection));
    }

    private void startAnimating() {
        if (this.mRunnable == null) {
            this.mRunnable = new ScrollAnimationRunnable();
        }
        this.mAnimating = true;
        this.mNeedsReset = true;
        if (this.mAlreadyDelayed || this.mActivationDelay <= 0) {
            this.mRunnable.run();
        } else {
            ViewCompat.postOnAnimationDelayed(this.mTarget, this.mRunnable, (long) this.mActivationDelay);
        }
        this.mAlreadyDelayed = true;
    }

    private void requestStop() {
        if (this.mNeedsReset) {
            this.mAnimating = false;
        } else {
            this.mScroller.requestStop();
        }
    }

    private float computeTargetVelocity(int direction, float coordinate, float srcSize, float dstSize) {
        float value = getEdgeValue(this.mRelativeEdges[direction], srcSize, this.mMaximumEdges[direction], coordinate);
        if (value == 0.0f) {
            return 0.0f;
        }
        float relativeVelocity = this.mRelativeVelocity[direction];
        float minimumVelocity = this.mMinimumVelocity[direction];
        float maximumVelocity = this.mMaximumVelocity[direction];
        float targetVelocity = relativeVelocity * dstSize;
        if (value > 0.0f) {
            return constrain(value * targetVelocity, minimumVelocity, maximumVelocity);
        }
        return -constrain((-value) * targetVelocity, minimumVelocity, maximumVelocity);
    }

    private float getEdgeValue(float relativeValue, float size, float maxValue, float current) {
        float interpolated;
        float edgeSize = constrain(relativeValue * size, 0.0f, maxValue);
        float value = constrainEdgeValue(size - current, edgeSize) - constrainEdgeValue(current, edgeSize);
        if (value < 0.0f) {
            interpolated = -this.mEdgeInterpolator.getInterpolation(-value);
        } else if (value <= 0.0f) {
            return 0.0f;
        } else {
            interpolated = this.mEdgeInterpolator.getInterpolation(value);
        }
        return constrain(interpolated, -1.0f, 1.0f);
    }

    private float constrainEdgeValue(float current, float leading) {
        if (leading == 0.0f) {
            return 0.0f;
        }
        switch (this.mEdgeType) {
            case R.styleable.WalletImButtonBar_capitalizeButtonText /*0*/:
            case R.styleable.WalletImFormEditText_validatorErrorString /*1*/:
                if (current >= leading) {
                    return 0.0f;
                }
                if (current >= 0.0f) {
                    return 1.0f - (current / leading);
                }
                if (this.mAnimating && this.mEdgeType == 1) {
                    return 1.0f;
                }
                return 0.0f;
            case R.styleable.WalletImFormEditText_validatorRegexp /*2*/:
                if (current < 0.0f) {
                    return current / (-leading);
                }
                return 0.0f;
            default:
                return 0.0f;
        }
    }

    private static int constrain(int value, int min, int max) {
        if (value > max) {
            return max;
        }
        if (value < min) {
            return min;
        }
        return value;
    }

    private static float constrain(float value, float min, float max) {
        if (value > max) {
            return max;
        }
        if (value < min) {
            return min;
        }
        return value;
    }

    private void cancelTargetTouch() {
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent cancel = MotionEvent.obtain(eventTime, eventTime, 3, 0.0f, 0.0f, 0);
        this.mTarget.onTouchEvent(cancel);
        cancel.recycle();
    }
}
