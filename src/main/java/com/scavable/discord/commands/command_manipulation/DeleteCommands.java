package com.scavable.discord.commands.command_manipulation;

import com.scavable.discord.commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.service.ApplicationService;
import reactor.core.publisher.Mono;

public class DeleteCommands implements SlashCommand {
    private final ApplicationService applicationService;
    private final long applicationId;
    public DeleteCommands(ApplicationService applicationService, long applicationId){
        this.applicationService = applicationService;
        this.applicationId = applicationId;
    }
    public void Command(long commandID){
        boolean valid = false;
        RegisterCommands registerCommands = new RegisterCommands(applicationService, applicationId);
        for (String temp : registerCommands.getCommands().keySet()) {
            if(registerCommands.getCommands().get(temp).id().asLong() == commandID){
                System.out.println("Delete command ID exist");
                valid = true;
            }
        }

        if(valid){
            applicationService.deleteGlobalApplicationCommand(applicationId, commandID).subscribe();
        }
        else{
            System.out.println("Delete command ID does not exist");
        }



    }
    public void Commands(){

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return null;
    }
}
