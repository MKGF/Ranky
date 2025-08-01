package com.desierto.ranky.infrastructure.dto;

public record EntryDto(int index, String name, String emoji, String division, int leaguePoints,
                       String wins, String losses, String winrate) {

}
