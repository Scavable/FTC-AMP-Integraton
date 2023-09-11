package com.scavable;

import com.scavable.amp.AMP;
import com.scavable.discord.Discord;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(String.valueOf(Main.class));

    public static void main(String[] args) throws IOException {
        Runnable ampRunnable = new AMP();
        Runnable discordRunnable = new Discord();

        Thread discordThread = new Thread(discordRunnable);
        Thread ampThread = new Thread(ampRunnable);

        discordThread.start();
        ampThread.start();
    }
}