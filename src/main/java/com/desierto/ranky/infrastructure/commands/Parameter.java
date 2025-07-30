package com.desierto.ranky.infrastructure.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public record Parameter(String name, String description, Boolean required, OptionType optionType) {

}
