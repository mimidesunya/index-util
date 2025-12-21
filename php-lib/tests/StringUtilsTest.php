<?php

use PHPUnit\Framework\TestCase;
use Zamasoft\Index\Util\StringUtils;

require_once __DIR__ . '/../src/StringUtils.php';

class StringUtilsTest extends TestCase
{
    public function testToHalfWidth()
    {
        $this->assertEquals("ABCabc012!#$", StringUtils::toHalfWidth("ＡＢＣａｂｃ０１２！＃＄"));
        $this->assertEquals("Hello World", StringUtils::toHalfWidth("Hello World"));
    }

    public function testTrimToEmpty()
    {
        $this->assertEquals("abc", StringUtils::trimToEmpty("  abc  "));
        $this->assertEquals("", StringUtils::trimToEmpty(null));
        $this->assertEquals("", StringUtils::trimToEmpty("   "));
    }

    public function testFullTrim()
    {
        $this->assertEquals("abc", StringUtils::fullTrim("　 abc　 "));
        $this->assertEquals("abc", StringUtils::fullTrim("\u{00A0}abc\u{00A0}"));
        $this->assertNull(StringUtils::fullTrim(null));
        $this->assertEquals("", StringUtils::fullTrim("　 "));
    }

    public function testNormalize()
    {
        // ひらがな -> カタカナ, 異体字統一, 半角カタカナ -> 全角カタカナ, スペース除去
        $n1 = StringUtils::normalize("坂﨑 ともゑ");
        $this->assertEquals("坂崎トモエ", $n1);
        $n2 = StringUtils::normalize("坂﨑 ﾏﾛｶ");
        $this->assertEquals("坂崎マロカ", $n2);
        $this->assertEquals("前島", StringUtils::normalize("前島"));
    }

    public function testConvertKansuji()
    {
        $this->assertEquals("1", StringUtils::convertKansuji("一"));
        $this->assertEquals("10", StringUtils::convertKansuji("十"));
        $this->assertEquals("11", StringUtils::convertKansuji("十一"));
        $this->assertEquals("21", StringUtils::convertKansuji("二十一"));
        $this->assertEquals("100", StringUtils::convertKansuji("百"));
        $this->assertEquals("123", StringUtils::convertKansuji("百二十三"));
        $this->assertEquals("1000", StringUtils::convertKansuji("千"));
        $this->assertEquals("10000", StringUtils::convertKansuji("一万"));
        $this->assertEquals("100000000", StringUtils::convertKansuji("一億"));
        $this->assertEquals("0", StringUtils::convertKansuji("零"));
    }

    public function testToKanji()
    {
        $this->assertEquals("〇", StringUtils::toKanji(0));
        $this->assertEquals("一", StringUtils::toKanji(1));
        $this->assertEquals("十", StringUtils::toKanji(10));
        $this->assertEquals("三十四", StringUtils::toKanji(34));
        $this->assertEquals("百", StringUtils::toKanji(100));
        $this->assertNull(StringUtils::toKanji(101));
        $this->assertNull(StringUtils::toKanji(-1));
    }

    public function testHash()
    {
        $h1 = StringUtils::hash("坂﨑 ともゑ");
        $h2 = StringUtils::hash("坂崎トモヱ");
        $this->assertEquals($h1, $h2);
    }

    public function testToZenkakuKatakana()
    {
        $result = StringUtils::toZenkakuKatakana("\u{FF71}\u{FF72}\u{FF73}\u{FF74}\u{FF75}");
        $this->assertEquals("アイウエオ", $result);
        $this->assertEquals("ガギグゲゴ", StringUtils::toZenkakuKatakana("\u{FF76}\u{FF9E}\u{FF77}\u{FF9E}\u{FF78}\u{FF9E}\u{FF79}\u{FF9E}\u{FF7A}\u{FF9E}"));
        $this->assertEquals("パピプペポ", StringUtils::toZenkakuKatakana("\u{FF8A}\u{FF9F}\u{FF8B}\u{FF9F}\u{FF8C}\u{FF9F}\u{FF8D}\u{FF9F}\u{FF8E}\u{FF9F}"));
    }

    public function testToNgram()
    {
        $ng1 = StringUtils::toNgram("あいう");
        $this->assertEquals("あ い う", $ng1);
        $ng2 = StringUtils::toNgram("（あい）");
        $this->assertEquals("（ あ い ）", $ng2);
    }
}
