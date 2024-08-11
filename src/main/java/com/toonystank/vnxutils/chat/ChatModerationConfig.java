package com.toonystank.vnxutils.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toonystank.vnxutils.ConfigManger;
import com.toonystank.vnxutils.VNXUtils;
import lombok.Getter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.List;
import java.util.HashSet;

@Getter
public class ChatModerationConfig extends ConfigManger{

    private static final String JSON_URL = "https://raw.githubusercontent.com/zacanger/profane-words/master/words.json";
    private static final String YAML_FILE_NAME = "profanity.yml";
    private HashSet<String> profaneWords;

    public ChatModerationConfig(VNXUtils plugin) throws IOException {
        super(plugin, YAML_FILE_NAME, true, false);
        this.plugin = plugin;
        downloadAndConvertJson();
        setWords();
    }

    private void downloadAndConvertJson() throws IOException {
        URI uri = URI.create(JSON_URL);
        URL url = URL.of(uri, null);
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> profaneWords = gson.fromJson(reader, listType);
            getConfig().set("profanity", profaneWords);
            save();
        }
    }

    private void setWords() {
        List<String> wordsList = getConfig().getStringList("profanity");
        this.profaneWords = new HashSet<>(wordsList);
    }
    public void addProfaneWord(String word) throws IOException {
        addToStringList("profanity", word);
    }
}
