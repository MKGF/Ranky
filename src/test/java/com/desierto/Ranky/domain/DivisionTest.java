package com.desierto.Ranky.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.merakianalytics.orianna.types.common.Division;
import org.junit.jupiter.api.Test;

public class DivisionTest extends BaseTest {

  @Test
  public void I_higher_than_II() {
    assertThat(Division.I.compare(Division.II) > 0);
  }

  @Test
  public void II_higher_than_III() {
    assertThat(Division.II.compare(Division.III) > 0);
  }

  @Test
  public void III_higher_than_IV() {
    assertThat(Division.III.compare(Division.IV) > 0);

  }

  @Test
  public void IV_lower_than_I() {
    assertThat(Division.IV.compare(Division.I) < 0);
  }

}
