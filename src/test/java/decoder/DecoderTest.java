package decoder;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class DecoderTest {

    static Stream<Arguments> encodedIntegers(){
        return Stream.of(
                Arguments.of("i52e", "52"),
                Arguments.of("i-52e", "-52")
        );
    }

    static Stream<Arguments> encodedLists(){
        return Stream.of(
                Arguments.of("l5:helloi52ee", "[\"hello\",52]"),
                Arguments.of("lli747e6:orangeee", "[[747,\"orange\"]]")
        );
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testDecodeString() {
        String testString = "3:abc";

        String decoded = new Decoder().decode(testString);
        Assertions.assertEquals("\"abc\"", decoded);
    }

    @ParameterizedTest
    @MethodSource("encodedIntegers")
    void testDecodeInteger(String encodedString, String expected) {
        String decoded = new Decoder().decode(encodedString);
        Assertions.assertEquals(expected, decoded);

    }

    @ParameterizedTest
    @MethodSource("encodedLists")
    void testDecodeList(String encodedString, String expected) {
        String decoded = new Decoder().decode(encodedString);
        Assertions.assertEquals(expected, decoded);
    }
}