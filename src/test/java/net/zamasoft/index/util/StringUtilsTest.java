package net.zamasoft.index.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void testToHalfWidth() {
        assertEquals("ABCabc012!#$", StringUtils.toHalfWidth("ＡＢＣａｂｃ０１２！＃＄"));
        assertEquals("Hello World", StringUtils.toHalfWidth("Hello World"));
    }

    @Test
    public void testTrimToEmpty() {
        assertEquals("abc", StringUtils.trimToEmpty("  abc  "));
        assertEquals("", StringUtils.trimToEmpty(null));
        assertEquals("", StringUtils.trimToEmpty("   "));
    }

    @Test
    public void testFullTrim() {
        assertEquals("abc", StringUtils.fullTrim("　 abc　 "));
        assertEquals("abc", StringUtils.fullTrim("\u00A0abc\u00A0"));
        assertNull(StringUtils.fullTrim(null));
        assertEquals("", StringUtils.fullTrim("　 "));
    }

    @Test
    public void testNormalize() {
        // ひらがな -> カタカナ, 異体字統一, 半角カタカナ -> 全角カタカナ, スペース除去
        String n1 = StringUtils.normalize("坂﨑 ともゑ");
        assertEquals("坂崎トモエ", n1);
        String n2 = StringUtils.normalize("坂﨑 ﾏﾛｶ");
        assertEquals("坂崎マロカ", n2);
        assertEquals("前島", StringUtils.normalize("前島"));
    }

    @Test
    public void testConvertKansuji() {
        assertEquals("1", StringUtils.convertKansuji("一"));
        assertEquals("10", StringUtils.convertKansuji("十"));
        assertEquals("11", StringUtils.convertKansuji("十一"));
        assertEquals("21", StringUtils.convertKansuji("二十一"));
        assertEquals("100", StringUtils.convertKansuji("百"));
        assertEquals("123", StringUtils.convertKansuji("百二十三"));
        assertEquals("1000", StringUtils.convertKansuji("千"));
        assertEquals("10000", StringUtils.convertKansuji("一万"));
        assertEquals("100000000", StringUtils.convertKansuji("一億"));
        assertEquals("0", StringUtils.convertKansuji("零"));
    }

    @Test
    public void testToKanji() {
        assertEquals("〇", StringUtils.toKanji(0));
        assertEquals("一", StringUtils.toKanji(1));
        assertEquals("十", StringUtils.toKanji(10));
        assertEquals("三十四", StringUtils.toKanji(34));
        assertEquals("百", StringUtils.toKanji(100));
        assertNull(StringUtils.toKanji(101));
        assertNull(StringUtils.toKanji(-1));
    }

    @Test
    public void testHash() {
        long h1 = StringUtils.hash("坂﨑 ともゑ");
        long h2 = StringUtils.hash("坂崎トモヱ");
        assertEquals(h1, h2);
    }

    @Test
    public void testToZenkakuKatakana() {
        String result = StringUtils.toZenkakuKatakana("\uFF71\uFF72\uFF73\uFF74\uFF75");
        assertEquals("アイウエオ", result);
        assertEquals("ガギグゲゴ", StringUtils.toZenkakuKatakana("\uFF76\uFF9E\uFF77\uFF9E\uFF78\uFF9E\uFF79\uFF9E\uFF7A\uFF9E"));
        assertEquals("パピプペポ", StringUtils.toZenkakuKatakana("\uFF8A\uFF9F\uFF8B\uFF9F\uFF8C\uFF9F\uFF8D\uFF9F\uFF8E\uFF9F"));
    }

    @Test
    public void testToNgram() {
        String ng1 = StringUtils.toNgram("あいう");
        assertEquals("あ い う", ng1);
        String ng2 = StringUtils.toNgram("（あい）");
        assertEquals("（ あ い ）", ng2);
    }
}
