package com.scavable.discord;

import com.scavable.discord.commands.RestartBot;
import com.scavable.discord.commands.Whitelist;
import com.scavable.discord.commands.command_manipulation.DeleteCommands;
import com.scavable.discord.commands.command_manipulation.RegisterCommands;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class Discord implements Runnable {
    final GatewayDiscordClient client;
    final ApplicationService applicationService;
    long applicationId;
    public Discord(){
        client = DiscordClientBuilder.create(System.getenv("BOT_TOKEN")).build()
                .login()
                .block();

        client.on(ReadyEvent.class, readyEvent -> Mono.fromRunnable(() -> {
            final User self = readyEvent.getSelf();
            System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
        })).subscribe();

        applicationId = client.getRestClient().getApplicationId().block();
        applicationService = client.getRestClient().getApplicationService();

        //Auto Complete
        client.on(ChatInputAutoCompleteEvent .class, chatInputAutoCompleteEvent -> {
            if(chatInputAutoCompleteEvent.getCommandName().equalsIgnoreCase("whitelist_add")) {
                String typing = chatInputAutoCompleteEvent.getFocusedOption().getValue()
                        .map(ApplicationCommandInteractionOptionValue::asString)
                        .orElse("");
            }

            List<ApplicationCommandOptionChoiceData> suggestions = new ArrayList<>();
            //suggestions.add(ApplicationCommandOptionChoiceData.builder().name(AMP_SERVER_NAME).value(AMP_SERVER_NAME).build());

            return chatInputAutoCompleteEvent.respondWithSuggestions(suggestions);
        }).subscribe();

        //Slash Commands
        client.on(ChatInputInteractionEvent.class, chatInputInteractionEvent -> {

            switch (chatInputInteractionEvent.getCommandName()) {
                case "whitelist_add" -> {
                    return new Whitelist().handle(chatInputInteractionEvent);
                }
                case "restart_bot" -> {
                    return new RestartBot().handle(chatInputInteractionEvent);
                }
                default -> System.out.println("Default");
            }

            return null;
        }).subscribe();


        List<ApplicationCommandRequest> commands = new ArrayList<>();
        commands.add(Whitelist.WhitelistCommand());
        commands.add(RestartBot.RestartBotCommand());

        //Register Commands
        RegisterCommands registerCommands = new RegisterCommands(applicationService, applicationId);

        //registerCommands.registerSingleCommand(Whitelist.WhitelistCommand());
        registerCommands.registerMultipleCommands(commands);
        registerCommands.showCommands();
        registerCommands.showCommandsAndID();

        //Delete Commands
        DeleteCommands deleteCommands = new DeleteCommands(client.getRestClient().getApplicationService(), applicationId);
        //deleteCommands.Command(1146193827871330335L);

        //Keep client running
        client.onDisconnect().block();
    }

    public GatewayDiscordClient getClient(){
        return client;
    }


    @Override
    public void run() {

    }
}
