package com.desierto.ranky.infrastructure.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.ranky.infrastructure.dto.EntryDto;
import com.desierto.ranky.infrastructure.utils.DiscordRankingFormatter;
import java.util.List;
import net.dv8tion.jda.api.events.GenericEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrintRankingServiceTest {

  @InjectMocks
  PrintRankingService cut;

  @Mock
  ConfigLoader config;

  @Mock
  DiscordRankingFormatter discordRankingFormatter;

  @BeforeEach
  void setFormatterUp() {
    lenient().when(discordRankingFormatter.footer()).thenReturn("");
    lenient().when(discordRankingFormatter.title(anyString())).thenReturn("");
    lenient().when(discordRankingFormatter.title(anyString(), anyString())).thenReturn("");
    lenient().when(config.getAccountLimit()).thenReturn(1);
  }

  @Test
  void whenSinglePageIsCalled_usesFunctionPrint() {
    GenericEvent event = mock(GenericEvent.class);
    when(discordRankingFormatter.formatRankingEntries(anyList())).thenReturn("formatted");
    SinglePagePrintingFunction function = mock(SinglePagePrintingFunction.class);

    cut.printSinglePage(event, "rankingName", List.of(), function);

    verify(function, times(1)).print(event, "formatted");
  }

  @Test
  void whenMultiPageIsCalled_usesAllPrintingMethodsFromFunction() {
    GenericEvent event = mock(GenericEvent.class);
    EntryDto entry1 = mock(EntryDto.class);
    EntryDto entry2 = mock(EntryDto.class);
    EntryDto entry3 = mock(EntryDto.class);
    EntryDto entry4 = mock(EntryDto.class);
    when(discordRankingFormatter.formatRankingEntries(anyList())).thenReturn("formatted");
    MultiPagePrintingFunction function = mock(MultiPagePrintingFunction.class);

    cut.printMultiPage(event, "rankingName", List.of(entry1, entry2, entry3, entry4), function);

    verify(function, times(1)).printBeginning(event, "formatted");
    verify(function, times(3)).printGeneric(event, "formatted");
    verify(function, times(1)).printEnding(event, "formatted");
  }

}
