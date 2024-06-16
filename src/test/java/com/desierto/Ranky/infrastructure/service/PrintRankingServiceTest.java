package com.desierto.Ranky.infrastructure.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.dto.EntryDTO;
import com.desierto.Ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.List;
import net.dv8tion.jda.api.events.GenericEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PrintRankingServiceTest {

  PrintRankingService cut;

  @Mock
  ConfigLoader config;

  @Mock
  DiscordRankingFormatter discordRankingFormatter;

  @BeforeEach
  public void setup() {
    cut = new PrintRankingService(config, discordRankingFormatter);
    when(discordRankingFormatter.footer()).thenReturn("");
    when(discordRankingFormatter.title(anyString())).thenReturn("");
    when(discordRankingFormatter.title(anyString(), anyString())).thenReturn("");
    when(config.getAccountLimit()).thenReturn(2);
  }

  @Test
  public void whenSinglePageIsCalled_usesFunctionPrint() {
    GenericEvent event = mock(GenericEvent.class);
    when(discordRankingFormatter.formatRankingEntries(anyList())).thenReturn("formatted");
    SinglePagePrintingFunction function = mock(SinglePagePrintingFunction.class);

    cut.printSinglePage(event, "rankingName", List.of(), function);

    verify(function, times(1)).print(event, "formatted");
  }

  @Test
  public void whenMultiPageIsCalled_usesAllPrintingMethodsFromFunction() {
    GenericEvent event = mock(GenericEvent.class);
    EntryDTO entry1 = mock(EntryDTO.class);
    EntryDTO entry2 = mock(EntryDTO.class);
    EntryDTO entry3 = mock(EntryDTO.class);
    EntryDTO entry4 = mock(EntryDTO.class);
    when(discordRankingFormatter.formatRankingEntries(anyList())).thenReturn("formatted");
    MultiPagePrintingFunction function = mock(MultiPagePrintingFunction.class);

    cut.printMultiPage(event, "rankingName", List.of(entry1, entry2, entry3, entry4), function);

    verify(function, times(1)).printBeginning(event, "formatted");
    verify(function, times(1)).printGeneric(event, "formatted");
    verify(function, times(1)).printEnding(event, "formatted");

  }

}
