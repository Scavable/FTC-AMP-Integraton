package com.scavable.discord.commands.command_manipulation;

import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class RegisterCommands {
    private final Logger LOGGER = Logger.getLogger(String.valueOf(this.getClass()));
    private final ApplicationService applicationService;
    private final long applicationId;
    public RegisterCommands(ApplicationService applicationService, long applicationId) {
        this.applicationService = applicationService;
        this.applicationId = applicationId;

    }

    public void registerSingleCommand(ApplicationCommandRequest command) {
        applicationService.createGlobalApplicationCommand(applicationId, command).subscribe();
    }

    public void registerMultipleCommands(List<ApplicationCommandRequest> commandList) {
        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commandList)
                .doOnNext(cmd -> LOGGER.info("Successfully registered Global Command " + cmd.name()))
                .doOnError(e -> LOGGER.warning("Failed to register global commands"))
                .subscribe();
    }

    public Map<String, ApplicationCommandData> getCommands(){

        return applicationService.getGlobalApplicationCommands(applicationId)
                .collectMap(ApplicationCommandData::name)
                .block();
    }
    public void showCommands(){
        for (String temp : getCommands().keySet()) {
            ApplicationCommandData command = getCommands().get(temp);
            System.out.println(command.name());
        }
        System.out.println();
    }

    public void showCommandsAndID(){
        for (String temp : getCommands().keySet()) {
            ApplicationCommandData command = getCommands().get(temp);
            System.out.println(command.name() + ", " + command.id());
        }
        System.out.println();
    }
}
