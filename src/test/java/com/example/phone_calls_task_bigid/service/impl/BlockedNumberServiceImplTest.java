package com.example.phone_calls_task_bigid.service.impl;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.example.phone_calls_task_bigid.model.BlockedNumber;
import com.example.phone_calls_task_bigid.repository.BlockedNumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class BlockedNumberServiceImplTest {

    @Mock
    private BlockedNumberRepository blockedNumberRepository;

    @InjectMocks
    private BlockedNumberServiceImpl blockedNumberService;

    @BeforeEach
    void setUp() {
        // Initialization code if needed
    }

    @Test
    void loadBlacklistFromCsv_ValidCsv_AddsNumbersToBlacklist() {
        // Arrange
        String csvData = "phone\n123456789\n987654321";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        // Act
        blockedNumberService.loadBlacklistFromCsv(inputStream);
        // Assert
        verify(blockedNumberRepository, times(2)).save(any(BlockedNumber.class));
    }

    @Test
    void loadBlacklistFromCsv_InvalidCsv_ThrowsException() {
        // Arrange
        String csvData = "invalid_header\n123456789";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        // Act
        assertThrows(RuntimeException.class, () -> blockedNumberService.loadBlacklistFromCsv(inputStream));
        // Assert
        verify(blockedNumberRepository, never()).save(any(BlockedNumber.class));
    }
}
