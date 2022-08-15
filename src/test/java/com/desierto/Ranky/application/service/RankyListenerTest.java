package com.desierto.Ranky.application.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.desierto.Ranky.TestConfig;
import com.desierto.Ranky.domain.BaseTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public class RankyListenerTest extends BaseTest {

  private static RankyListener ranky;

  @BeforeAll
  public static void setUp() {
    ranky = new RankyListener(null);
  }

  @Test
  public void getRankingNameTest() {
    String firstTest = "/command \"I want to be able to read this sentence.\"";
    String secondTest = "/anotherCommand \"I want to be able to read this sentence again.\" I don't want to read this though...";
    String thirdTest = "Here I can have as many words as I want because \"what matters is in here\" and not outside";
    assertEquals("I want to be able to read this sentence.", ranky.getRankingName(firstTest));
    assertEquals("I want to be able to read this sentence again.",
        ranky.getRankingName(secondTest));
    assertEquals("what matters is in here", ranky.getRankingName(thirdTest));
  }

  @Test
  public void getParameterTest() {
    String firstTest = "/command \"RANKING NAME\" account name";
    String secondTest = "/anotherCommand \"ANOTHER RANKING NAME\" THIS IS A LONG NAMED ACCOUNT";
    String thirdTest = "I actually do not care about commands since \"as long as the syntax is respected\" my account will always be present in the ranking";
    String fourthTest = "/command \"RANKING NAME\" 15-08-2022";
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    assertEquals("account name", ranky.getParameter(firstTest, "RANKING NAME"));
    assertEquals("THIS IS A LONG NAMED ACCOUNT",
        ranky.getParameter(secondTest, "ANOTHER RANKING NAME"));
    assertEquals("my account will always be present in the ranking",
        ranky.getParameter(thirdTest, "as long as the syntax is respected"));
    assertEquals(LocalDateTime.of(2022, 8, 15, 0, 0),
        LocalDate.parse(ranky.getParameter(fourthTest, "RANKING NAME"), df).atStartOfDay());
  }

  @Test
  public void getAccountsToAddTest() {
    String firstTest = "/command \"RANKING NAME\" these,are,several,accounts,that,I,want,to,keep";
    ArrayList<String> accounts = new ArrayList(List
        .of("these", "are", "several", "accounts", "that", "I", "want", "to", "keep"));
    assertArrayEquals(accounts.toArray(new String[0]),
        ranky.getAccountsToAdd(firstTest, "RANKING NAME").toArray(new String[0]));
  }
}
