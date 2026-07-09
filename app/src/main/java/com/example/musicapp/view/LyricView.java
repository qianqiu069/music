package com.example.musicapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.musicapp.model.LyricLine;
import com.example.musicapp.utils.LyricParser;

import java.util.ArrayList;
import java.util.List;

/**
 * LyricView - 自定义歌词显示控件
 */
public class LyricView extends View {

    // 歌词列表
    private List<LyricLine> lines = new ArrayList<>();
    // 当前行索引
    private int currentLine = -1;

    // 画笔
    private final Paint normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // 行间距
    private final float lineSpacing = dp(16);
    private final float normalSize = dp(15);
    private final float highlightSize = dp(18);

    // 滚动偏移量
    private float offsetY = 0f;
    // 目标偏移量（动画终点）
    private float targetOffsetY = 0f;
    // 动画开始时间
    private long animStartTime = 0L;
    private static final long ANIM_DURATION = 300L;
    private final DecelerateInterpolator interpolator = new DecelerateInterpolator();

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 默认（未高亮）字号 + 半透明白
        normalPaint.setColor(Color.parseColor("#80FFFFFF"));
        normalPaint.setTextSize(normalSize);
        normalPaint.setTextAlign(Paint.Align.CENTER);

        // 高亮：纯白 + 更大字号 + 加粗
        highlightPaint.setColor(Color.WHITE);
        highlightPaint.setTextSize(highlightSize);
        highlightPaint.setTextAlign(Paint.Align.CENTER);
        highlightPaint.setFakeBoldText(true);
    }

    public void setLyric(String lrcText) {
        this.lines = LyricParser.parse(lrcText);
        this.currentLine = -1;
        this.offsetY = 0;
        this.targetOffsetY = 0;
        invalidate();
    }

    public List<LyricLine> getLines() {
        return lines;
    }

    /**
     * 根据当前播放时间（毫秒），更新高亮行 + 启动滚动
     */
    public void updateTime(long currentMs) {
        int newLine = LyricParser.findCurrentLineIndex(lines, currentMs);
        if (newLine != currentLine) {
            currentLine = newLine;
            // 启动滚动动画：让当前行滚到中间
            // offsetY = 当前行 * 行高，这样画的时候用 mid - offsetY 就能让当前行居中
            targetOffsetY = currentLine * lineHeight();
            animStartTime = System.currentTimeMillis();
            invalidate();
        } else if (offsetY != targetOffsetY) {
            // 动画进行中，持续刷新
            invalidate();
        }
    }

    private static final String EMPTY_HINT = "暂无歌词";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 视图中心点
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        if (lines == null || lines.isEmpty()) {
            // 没有歌词时画一行提示
            canvas.drawText(EMPTY_HINT, centerX, centerY, normalPaint);
            return;
        }

        // 更新当前 offset（线性插值，做出滑动动画）
        updateAnimatedOffset();

        float lh = lineHeight();
        // 计算需要画的范围，避免画屏幕外的行（提升性能）
        int firstVisible = Math.max(0, (int) ((offsetY - centerY) / lh) - 1);
        int lastVisible = Math.min(lines.size() - 1,
                (int) ((offsetY + centerY) / lh) + 1);

        for (int i = firstVisible; i <= lastVisible; i++) {
            float y = centerY + i * lh - offsetY;
            // 越靠近 centerY，越是当前行
            Paint p = (i == currentLine) ? highlightPaint : normalPaint;
            canvas.drawText(lines.get(i).content, centerX, y, p);
        }

        // 动画还没结束，继续刷新
        if (offsetY != targetOffsetY) {
            postInvalidateOnAnimation();
        }
    }

    /**
     * 根据时间推进 offsetY 朝 targetOffsetY 滑动
     */
    private void updateAnimatedOffset() {
        if (offsetY == targetOffsetY) return;
        long elapsed = System.currentTimeMillis() - animStartTime;
        if (elapsed >= ANIM_DURATION) {
            offsetY = targetOffsetY;
            return;
        }
        float fraction = interpolator.getInterpolation(elapsed / (float) ANIM_DURATION);
        float delta = targetOffsetY - offsetY;
        offsetY += delta * fraction;
        if (Math.abs(targetOffsetY - offsetY) < 0.5f) {
            offsetY = targetOffsetY;
        }
    }

    private float lineHeight() {
        return highlightSize + lineSpacing;
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
