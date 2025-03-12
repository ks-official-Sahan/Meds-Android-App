package com.sahansachintha.meds.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.sahansachintha.meds.R;

public abstract class SwipeToActionCallback extends ItemTouchHelper.SimpleCallback {

    private final Drawable deleteIcon;
    private final Drawable doneIcon;
    private final int iconWidth;
    private final int iconHeight;
    private final Paint paint;
    private final float cornerRadius;

    public SwipeToActionCallback(Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        doneIcon = ContextCompat.getDrawable(context, R.drawable.ic_done);
        iconWidth = deleteIcon != null ? deleteIcon.getIntrinsicWidth() : 0;
        iconHeight = deleteIcon != null ? deleteIcon.getIntrinsicHeight() : 0;
        paint = new Paint();
        // Set the same corner radius as the card view (24dp)
        cornerRadius = context.getResources().getDisplayMetrics().density * 24;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();

        if (dX > 0) { // Swipe to right (mark as done)
            paint.setColor(Color.parseColor("#4CAF50")); // green
            RectF background = new RectF(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + dX, itemView.getBottom());
            c.drawRoundRect(background, cornerRadius, cornerRadius, paint);

            // Draw done icon
            int iconMargin = (itemHeight - iconHeight) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft + iconWidth;
            int iconBottom = iconTop + iconHeight;
            if (doneIcon != null) {
                doneIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                doneIcon.draw(c);
            }

            // Draw "Done" text
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setAntiAlias(true);
            String action = "Done";
            float textWidth = paint.measureText(action);
            float textX = itemView.getLeft() + (dX - textWidth) / 2;
            float textY = itemView.getTop() + (float) itemHeight / 2 - ((paint.descent() + paint.ascent()) / 2);
            c.drawText(action, textX, textY, paint);

        } else if (dX < 0) { // Swipe to left (delete)
            paint.setColor(Color.parseColor("#F44336")); // red
            RectF background = new RectF(itemView.getRight() + dX, itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
            c.drawRoundRect(background, cornerRadius, cornerRadius, paint);

            // Draw delete icon
            int iconMargin = (itemHeight - iconHeight) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconRight = itemView.getRight() - iconMargin;
            int iconLeft = iconRight - iconWidth;
            int iconBottom = iconTop + iconHeight;
            if (deleteIcon != null) {
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }

            // Draw "Delete" text
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            paint.setAntiAlias(true);
            String action = "Delete";
            float textWidth = paint.measureText(action);
            float textX = itemView.getRight() - (-dX - textWidth) / 2 - textWidth;
            float textY = itemView.getTop() + (float) itemHeight / 2 - ((paint.descent() + paint.ascent()) / 2);
            c.drawText(action, textX, textY, paint);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
