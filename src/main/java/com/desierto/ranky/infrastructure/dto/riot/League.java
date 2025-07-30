package com.desierto.ranky.infrastructure.dto.riot;

public record League(String queueType, String tier, String rank, int leaguePoints, int wins,
                     int losses) {

}
