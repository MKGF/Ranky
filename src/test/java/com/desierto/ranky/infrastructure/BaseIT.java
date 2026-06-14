package com.desierto.ranky.infrastructure;

import static org.mockito.Mockito.lenient;

import com.desierto.ranky.TestConfig;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BaseIT {

  @MockitoBean
  private DataSource dataSource;

  @MockitoBean
  private Connection connection;

  @SneakyThrows
  @BeforeEach
  void setup() {
    lenient().when(connection.getMetaData()).thenReturn(Mockito.mock(DatabaseMetaData.class));
  }
}
