package com.desierto.Ranky.infrastructure.commands;

import lombok.ToString;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@ToString
public class Parameter {

  private final String name;

  private final String description;

  private final Boolean required;

  private final OptionType optionType;

  private Parameter(String name, String description, Boolean required, OptionType optionType) {
    this.name = name;
    this.description = description;
    this.required = required;
    this.optionType = optionType;
  }

  public static Parameter of(String name, String description, Boolean required,
      OptionType optionType) {
    return new Parameter(name.toLowerCase(), description, required, optionType);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Boolean getRequired() {
    return required;
  }

  public OptionType getOptionType() {
    return optionType;
  }
}
