package com.scavable.discord.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public class Whitelist implements SlashCommand{

    static ApplicationCommandRequest whitelistCommandRequest;
    public static ApplicationCommandRequest WhitelistCommand(){
         whitelistCommandRequest = ApplicationCommandRequest.builder()
                .name("whitelist_add")
                .description("Adds user to server whitelist")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("ign")
                        .description("Player's in game name")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build()
                ).addOption(ApplicationCommandOptionData.builder()
                        .name("server")
                        .description("Modpack to use")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build()
                ).build();

        return whitelistCommandRequest;
    }

    @Override
    public String getName() {
        return "whitelist_add";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String ign, server;
        ign = event.getOption("ign")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();

        server = event.getOption("server")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();

        String prettyString = String.format("Whitelisted user: %s on server: %s", ign, server);

        return event.reply()
                .withEphemeral(true)
                .withContent(prettyString);
    }
}
