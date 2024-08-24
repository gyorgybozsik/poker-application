package hu.bgy.pokerapp.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoundRoleTest {


    public static Stream<Arguments> nextRole() {
        return Stream.of(
                Arguments.of(RoundRole.SMALL_BLIND, RoundRole.BIG_BLIND),
                Arguments.of(RoundRole.SPEAKER_3, RoundRole.SPEAKER_4),
                Arguments.of(RoundRole.SPEAKER_7, RoundRole.SMALL_BLIND),
                Arguments.of(RoundRole.SPEAKER_6, RoundRole.SPEAKER_7)
        );
    }

    @ParameterizedTest
    @MethodSource("nextRole")
    void isSelectableTest(
            final RoundRole actual,
            final RoundRole expected) {
        assertEquals(expected, actual.nextRole());
    }
}
