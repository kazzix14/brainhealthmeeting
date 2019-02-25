package com.example.brainhealth_meeting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleProfileContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<SimpleProfileContent.SimpleProfileItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, SimpleProfileContent.SimpleProfileItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createItem(i));
        }
    }

    private static void addItem(SimpleProfileContent.SimpleProfileItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.user_id), item);
    }

    private static SimpleProfileContent.SimpleProfileItem createItem(int position) {
        return new SimpleProfileContent.SimpleProfileItem(0, "test", 0, null);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class SimpleProfileItem {
        public final int user_id;
        public final String user_name;
        public final int user_age;
        public Bitmap user_photo;

        public SimpleProfileItem(int id, String name, int age, Bitmap bitmap) {

            this.user_id = id;
            this.user_photo = bitmap;
            this.user_name = name;
            this.user_age = age;
        }

    }
}
