package com.example.musicapp.model;

public class LyricLine {

    public long timeMs;
    // 歌词的文字内容
    public String content;

    public LyricLine(long timeMs, String content) {
        this.timeMs = timeMs;
        this.content = content;
    }
}
