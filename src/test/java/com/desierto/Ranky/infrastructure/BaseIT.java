package com.desierto.Ranky.infrastructure;

import com.desierto.Ranky.TestConfig;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BaseIT {

  @Autowired
  private EntityManagerFactory entityManagerFactory;
  
}
