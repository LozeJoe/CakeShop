package com.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 系统配置管理 — 基于文件持久化（重启不丢失）
 */
@Component
public class SystemConfigService {

    private static final Path CONFIG_FILE = Paths.get("demo", "config.properties");
    private final Properties props = new Properties();
    private final Map<String, String> defaults = new LinkedHashMap<>();

    public SystemConfigService() {
        defaults.put("site_name", "CakeShop");
        defaults.put("hero_title", "每一口都是幸福。");
        defaults.put("hero_subtitle", "精选优质原料，匠心手作蛋糕，为您的生活增添甜蜜。");
        defaults.put("page_size", "8");
        defaults.put("copyright", "Copyright © 2026 CakeShop. All rights reserved.");
        defaults.put("contact_phone", "");
        defaults.put("contact_email", "");
    }

    @PostConstruct
    public void init() {
        load();
    }

    private void load() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            if (Files.exists(CONFIG_FILE)) {
                try (Reader r = new InputStreamReader(new FileInputStream(CONFIG_FILE.toFile()), "UTF-8")) {
                    props.load(r);
                }
            }
        } catch (IOException ignored) {}
        // 用默认值补全缺失项
        for (Map.Entry<String, String> e : defaults.entrySet()) {
            if (!props.containsKey(e.getKey())) {
                props.setProperty(e.getKey(), e.getValue());
            }
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (Writer w = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE.toFile()), "UTF-8")) {
                props.store(w, "CakeShop System Config");
            }
        } catch (IOException e) {
            throw new RuntimeException("保存配置失败", e);
        }
    }

    public String get(String key) {
        return props.getProperty(key, defaults.get(key));
    }

    public void set(String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            props.setProperty(key, value.trim());
        }
    }

    public void updateAll(Map<String, String> values) {
        for (Map.Entry<String, String> e : values.entrySet()) {
            set(e.getKey(), e.getValue());
        }
        save();
    }

    public Map<String, String> getAll() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String key : defaults.keySet()) {
            result.put(key, get(key));
        }
        return result;
    }

    public Map<String, String> getDefaults() {
        return new LinkedHashMap<>(defaults);
    }

    public int getInt(String key, int fallback) {
        try { return Integer.parseInt(get(key)); } catch (NumberFormatException e) { return fallback; }
    }
}
