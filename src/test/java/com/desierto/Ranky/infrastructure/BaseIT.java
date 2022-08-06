package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.TestConfig;
import com.desierto.Ranky.domain.entity.Tables;
import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BaseIT {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  @AfterEach
  public void resetState() throws IOException {
    cleanAllDatabases();
  }

  private void cleanAllDatabases() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, Tables.getAll());
  }
}
