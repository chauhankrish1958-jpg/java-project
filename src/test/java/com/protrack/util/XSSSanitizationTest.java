package com.protrack.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XSSSanitizationTest {

    @Test
    @DisplayName("Verify XSS sanitization neutralizes script tags")
    public void testScriptTagNeutralized() {
        String input = "<script>alert('XSS Attack');</script>";
        String sanitized = ValidationUtil.sanitize(input);

        assertFalse(sanitized.contains("<"));
        assertFalse(sanitized.contains(">"));
        assertEquals("&lt;script&gt;alert(&#x27;XSS Attack&#x27;);&lt;/script&gt;", sanitized);
    }

    @Test
    @DisplayName("Verify HTML attribute breakout payload is neutralized")
    public void testAttributeBreakoutNeutralized() {
        String input = "\"><img src=x onerror=alert(1)>";
        String sanitized = ValidationUtil.sanitize(input);

        assertFalse(sanitized.contains("\""));
        assertFalse(sanitized.contains("<"));
        assertTrue(sanitized.contains("&quot;&gt;&lt;img"));
    }

    @Test
    @DisplayName("Verify escapeXml alias produces identical safe output")
    public void testEscapeXmlAlias() {
        String input = "<b>Bold & \"Quoted\" 'Values'</b>";
        String escaped = ValidationUtil.escapeXml(input);

        assertEquals("&lt;b&gt;Bold &amp; &quot;Quoted&quot; &#x27;Values&#x27;&lt;/b&gt;", escaped);
    }

    @Test
    @DisplayName("Verify null and empty inputs are handled cleanly without NullPointerException")
    public void testNullAndEmptyInputs() {
        assertEquals("", ValidationUtil.sanitize(null));
        assertEquals("", ValidationUtil.sanitize("   "));
        assertEquals("", ValidationUtil.escapeXml(null));
    }
}
