package com.example.musicapp.model;

import java.util.List;

public class Song {

    // 歌曲名，对应 JSON 的 "name"
    public String name;
    // 歌曲 ID
    public String id;
    // 歌手列表（一首歌可能有多位歌手）
    public List<Artist> artists;
    // 所属专辑
    public Album album;
    // 歌曲时长（秒）
    public int duration;
    // 专辑封面（包含 small / medium / large 三种尺寸）
    public Cover cover;
    // 播放 URL（直接拿去给 MediaPlayer 用）
    public String url;
    // 歌词信息
    public Lyric lyric;

    public String getArtistNames() {
        if (artists == null || artists.isEmpty()) {
            return "未知歌手";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < artists.size(); i++) {
            if (i > 0) sb.append(" / ");
            sb.append(artists.get(i).name);
        }
        return sb.toString();
    }

    public static class Artist {
        public String name;
        public String id;
        public String role;
    }

    public static class Album {
        public String name;
        public String id;
    }

    public static class Cover {
        public String small;
        public String medium;
        public String large;
    }

    public static class Lyric {
        public String text;
    }
}
