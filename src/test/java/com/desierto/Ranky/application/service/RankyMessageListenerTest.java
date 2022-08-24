package com.desierto.Ranky.application.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class RankyMessageListenerTest extends BaseTest {

  private static RankyMessageListener ranky;

  @BeforeAll
  public static void setUp() {
    ranky = new RankyMessageListener(null);
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

  @Test
  public void isCreateCommandTest() {
    String firstCommand = "/create \"RANKING\"";
    String secondCommand = "/\"RANKING\" /create";
    String thirdCommand = "/create RANKING";
    String fourthCommand = "/create \"RANKING\" I DO NOT CARE ABOUT THE REST";
    assertTrue(ranky.isCreateCommand(firstCommand));
    assertFalse(ranky.isCreateCommand(secondCommand));
    assertFalse(ranky.isCreateCommand(thirdCommand));
    assertFalse(ranky.isCreateCommand(fourthCommand));
  }

  @Test
  public void isAddAccountCommandTest() {
    String firstCommand = "/addAccount \"RANKING\" Test Account";
    String secondCommand = "/\"RANKING\" /addAccount \"RANKING\" account";
    String thirdCommand = "/addAccount RANKING account";
    String fourthCommand = "/addAccount \"RANKING\" anAccount, anotherAccount";
    assertTrue(ranky.isAddAccountCommand(firstCommand));
    assertFalse(ranky.isAddAccountCommand(secondCommand));
    assertFalse(ranky.isAddAccountCommand(thirdCommand));
    assertFalse(ranky.isAddAccountCommand(fourthCommand));
  }

  @Test
  public void isAddMultipleCommandTest() {
    String firstCommand = "/addMultiple \"RANKING\" Test Account";
    String secondCommand = "/\"RANKING\" /addMultiple \"RANKING\" account";
    String thirdCommand = "/addMultiple RANKING account";
    String fourthCommand = "/addMultiple \"RANKING\" anAccount, anotherAccount";
    String fifthCommand = "/addMultiple \"RANKING\" Test Account, Test Account 2";
    assertTrue(ranky.isAddMultipleCommand(firstCommand));
    assertFalse(ranky.isAddMultipleCommand(secondCommand));
    assertFalse(ranky.isAddMultipleCommand(thirdCommand));
    assertTrue(ranky.isAddMultipleCommand(fourthCommand));
    assertTrue(ranky.isAddMultipleCommand(fifthCommand));
  }

  @Test
  public void isRemoveAccountCommandTest() {
    String firstCommand = "/removeAccount \"RANKING\" Test Account";
    String secondCommand = "/\"RANKING\" /removeAccount \"RANKING\" account";
    String thirdCommand = "/removeAccount RANKING account";
    String fourthCommand = "/removeAccount \"RANKING\" anAccount, anotherAccount";
    assertTrue(ranky.isRemoveAccountCommand(firstCommand));
    assertFalse(ranky.isRemoveAccountCommand(secondCommand));
    assertFalse(ranky.isRemoveAccountCommand(thirdCommand));
    assertFalse(ranky.isRemoveAccountCommand(fourthCommand));
  }
}
