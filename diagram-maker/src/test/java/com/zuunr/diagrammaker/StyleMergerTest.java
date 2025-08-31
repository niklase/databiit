package com.zuunr.diagrammaker;

import com.zuunr.diagrammaker.mxcell.StyleMerger;
import com.zuunr.json.JsonValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StyleMergerTest {

    @Test
    void test1() {
        String css1 = "text;a=a1;b=b1;";
        String css2 = "b=b2=b3;c=c2;";
        JsonValue merged = StyleMerger.merge(JsonValue.of(css1), JsonValue.of(css2));
        assertEquals("a=a1;b=b2=b3;c=c2;text;", merged.getString());
    }

    @Test
    void test2() {
        String css1 = "";
        String css2 = "a=a1;b=b1;";
        JsonValue merged = StyleMerger.merge(JsonValue.of(css1), JsonValue.of(css2));
        assertEquals("a=a1;b=b1;", merged.getString());
    }

    @Test
    void test3() {
        String css1 = "a=a1;b=b1;";
        String css2 = "";
        JsonValue merged = StyleMerger.merge(JsonValue.of(css1), JsonValue.of(css2));
        assertEquals("a=a1;b=b1;", merged.getString());
    }
}
