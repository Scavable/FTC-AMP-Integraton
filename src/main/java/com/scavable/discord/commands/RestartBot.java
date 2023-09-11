package com.scavable.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public class RestartBot implements SlashCommand{

    static ApplicationCommandRequest restartCommandRequest;
    public static ApplicationCommandRequest RestartBotCommand() {
        restartCommandRequest = ApplicationCommandRequest.builder()
                .name("restart_bot")
                .description("Restarts the bot")
                .build();

        return restartCommandRequest;
    }

    @Override
    public String getName() {
        return "restart_bot";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return event.reply()
                .withEphemeral(true)
                .withContent("Bot Restarting");
    }
}
