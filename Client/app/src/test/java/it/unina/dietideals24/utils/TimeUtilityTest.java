package it.unina.dietideals24.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import it.unina.dietideals24.exceptions.TimePickerException;

class TimeUtilityTest {

    @Test
    void testConvertFieldsToMilliseconds_covers_1_1_1() throws TimePickerException{
        long result = TimeUtility.convertFieldsToMilliseconds(30, 12, 30);
        long oracle = 2637000000L;
        assertEquals(oracle, result);
    }

    @Test
    void testConvertFieldsToMilliseconds_covers_2_1_1() {
        assertThrows(TimePickerException.class, () -> TimeUtility.convertFieldsToMilliseconds(-25, 12, 30));
    }

    @Test
    void testConvertFieldsToMilliseconds_covers_1_2_1() {
        assertThrows(TimePickerException.class, () -> TimeUtility.convertFieldsToMilliseconds(14, -120, 30));
    }

    @Test
    void testConvertFieldsToMilliseconds_covers_1_3_1() {
        assertThrows(TimePickerException.class, () -> TimeUtility.convertFieldsToMilliseconds(12, 152, 30));
    }

    @Test
    void testConvertFieldsToMilliseconds_covers_1_1_2() {
        assertThrows(TimePickerException.class, () -> TimeUtility.convertFieldsToMilliseconds(10, 12, -12));
    }

    @Test
    void testConvertFieldsToMilliseconds_covers_1_1_3() {
        assertThrows(TimePickerException.class, () -> TimeUtility.convertFieldsToMilliseconds(19, 12, 300));
    }
}