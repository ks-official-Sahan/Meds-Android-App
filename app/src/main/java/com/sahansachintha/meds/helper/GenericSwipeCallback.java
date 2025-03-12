package com.sahansachintha.meds.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public abstract class GenericSwipeCallback extends ItemTouchHelper.SimpleCallback {

    public interface SwipeActionListener {
        void onSwipeRight(int position);
        void onSwipeLeft(int position);
    }

    private final SwipeActionListener listener;
    private final Drawable leftIcon, rightIcon;
    private final int leftColor, rightColor;
    private final Paint textPaint;
    private final float cornerRadius;
    private final String leftActionText, rightActionText;
    private final Context context;

    public GenericSwipeCallback(Context context, SwipeActionListener listener,
                                Drawable leftIcon, int leftColor, String leftActionText,
                                Drawable rightIcon, int rightColor, String rightActionText) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        this.listener = listener;
        this.leftIcon = leftIcon;
        this.rightIcon = rightIcon;
        this.leftColor = leftColor;
        this.rightColor = rightColor;
        this.leftActionText = leftActionText;
        this.rightActionText = rightActionText;
        this.textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        // Set the corner radius (adjust as needed or pass as parameter)
        this.cornerRadius = context.getResources().getDisplayMetrics().density * 24;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.RIGHT) {
            listener.onSwipeRight(position);
        } else if (direction == ItemTouchHelper.LEFT) {
            listener.onSwipeLeft(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        float itemHeight = itemView.getBottom() - itemView.getTop();
        Paint paint = new Paint();

        if (dX > 0) { // Swiping right
            paint.setColor(rightColor);
            RectF background = new RectF(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + dX, itemView.getBottom());
            c.drawRoundRect(background, cornerRadius, cornerRadius, paint);

            // Draw right icon if available
            if (rightIcon != null) {
                int iconMargin = ((int)itemHeight - rightIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin;
                int iconLeft = itemView.getLeft() + iconMargin;
                rightIcon.setBounds(iconLeft, iconTop, iconLeft + rightIcon.getIntrinsicWidth(), iconTop + rightIcon.getIntrinsicHeight());
                rightIcon.draw(c);
            }

            // Draw text
            float textX = itemView.getLeft() + dX / 2;
            float textY = itemView.getTop() + itemHeight / 2 - ((textPaint.descent() + textPaint.ascent()) / 2);
            c.drawText(rightActionText, textX, textY, textPaint);
        } else if (dX < 0) { // Swiping left
            paint.setColor(leftColor);
            RectF background = new RectF(itemView.getRight() + dX, itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
            c.drawRoundRect(background, cornerRadius, cornerRadius, paint);

            // Draw left icon if available
            if (leftIcon != null) {
                int iconMargin = ((int)itemHeight - leftIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin;
                int iconRight = itemView.getRight() - iconMargin;
                leftIcon.setBounds(iconRight - leftIcon.getIntrinsicWidth(), iconTop, iconRight, iconTop + leftIcon.getIntrinsicHeight());
                leftIcon.draw(c);
            }

            // Draw text
            float textX = itemView.getRight() - (-dX) / 2;
            float textY = itemView.getTop() + itemHeight / 2 - ((textPaint.descent() + textPaint.ascent()) / 2);
            c.drawText(leftActionText, textX, textY, textPaint);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
