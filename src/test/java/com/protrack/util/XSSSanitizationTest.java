package com.protrack.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XSSSanitizationTest {

    @Test
    @DisplayName("Verify JSP output escaping neutralizes script tags in HTML context")
    public void testScriptTagNeutralized() {
        String rawInput = "<script>alert('XSS Attack');</script>";
        String escaped = ValidationUtil.escapeXml(rawInput);

        assertFalse(escaped.contains("<"));
        assertFalse(escaped.contains(">"));
        assertEquals("&lt;script&gt;alert(&#x27;XSS Attack&#x27;);&lt;/script&gt;", escaped);
    }

    @Test
    @DisplayName("Verify HTML attribute breakout payload is neutralized")
    public void testAttributeBreakoutNeutralized() {
        String rawInput = "\"><img src=x onerror=alert(1)>";
        String escaped = ValidationUtil.escapeXml(rawInput);

        assertFalse(escaped.contains("\""));
        assertFalse(escaped.contains("<"));
        assertTrue(escaped.contains("&quot;&gt;&lt;img"));
    }

    @Test
    @DisplayName("Verify JSP output escaping handles real-world business names like 'R&D Team' without double-escaping")
    public void testOutputEscapingSingleEncoding() {
        String businessTitle = "R&D Team & Q&A \"Beta\"";
        String escaped = ValidationUtil.escapeXml(businessTitle);

        // Single entity encoding
        assertEquals("R&amp;D Team &amp; Q&amp;A &quot;Beta&quot;", escaped);
        assertFalse(escaped.contains("&amp;amp;"), "Must not suffer from double escaping");
    }

    @Test
    @DisplayName("Verify null and empty inputs are handled cleanly without NullPointerException")
    public void testNullAndEmptyInputs() {
        assertEquals("", ValidationUtil.escapeXml(null));
        assertEquals("", ValidationUtil.escapeXml("   "));
        assertEquals("", ValidationUtil.sanitize(null));
    }
}
