package com.desierto.Ranky.domain;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.desierto.Ranky.TestConfig;
import com.desierto.Ranky.domain.valueobject.AccountInformation;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public class AccountInformationTest extends BaseTest {

  @Autowired
  private Validator validator;

  @Test
  public void accountInformationLeaguePointsCannotExceed100() {
    Set<ConstraintViolation<AccountInformation>> violations = validator
        .validate(AccountInformation.builder().leaguePoints(101).build());

    assertNotEquals(0, violations.size());
  }
}
