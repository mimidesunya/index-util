<?php

namespace Zamasoft\Index\Util;

use RuntimeException;
use InvalidArgumentException;

/**
 * 文字列操作に関するユーティリティクラスです。
 */
final class StringUtils
{
    /**
     * インスタンス化を防止するためのプライベートコンストラクタです。
     */
    private function __construct()
    {
        // ユーティリティクラスのためインスタンス化不要
    }

    /** 全角アルファベットと半角アルファベットの文字コードの差 */
    private const DIFFERENCE = 0xFEE0; // 'Ａ' - 'A'

    /** 変換対象となる全角記号の配列 */
    private const FULL_WIDTH_SIGNS = [
        '！', '＃', '＄', '％', '＆', '（', '）', '＊', '＋', '，', '−', '－', '．', '／', '：', '；',
        '＜', '＝', '＞', '？', '＠', '［', '］', '＾', '＿', '｛', '｜', '｝'
    ];

    /**
     * 指定された文字が変換対象の全角記号かどうかを判定します。
     *
     * @param string $ch 判定対象の文字
     * @return bool 変換対象であれば true
     */
    private static function isFullWidthSign(string $ch): bool
    {
        return in_array($ch, self::FULL_WIDTH_SIGNS, true);
    }

    /**
     * 文字列内の全角アルファベット、数値、および特定の記号を半角に変換します。
     *
     * @param string|null $str 変換対象の文字列
     * @return string|null 変換後の文字列
     */
    public static function toHalfWidth(?string $str): ?string
    {
        if ($str === null) {
            return null;
        }

        $chars = mb_str_split($str, 1, 'UTF-8');
        $result = '';

        foreach ($chars as $c) {
            $code = mb_ord($c, 'UTF-8');
            $newCode = $code;

            // 'Ａ' (U+FF21) <= c <= 'Ｚ' (U+FF3A)
            // 'ａ' (U+FF41) <= c <= 'ｚ' (U+FF5A)
            // '０' (U+FF10) <= c <= '９' (U+FF19)
            if (($code >= 0xFF21 && $code <= 0xFF3A) ||
                ($code >= 0xFF41 && $code <= 0xFF5A) ||
                ($code >= 0xFF10 && $code <= 0xFF19) ||
                self::isFullWidthSign($c)) {
                $newCode = $code - self::DIFFERENCE;
            }
            $result .= mb_chr($newCode, 'UTF-8');
        }

        return $result;
    }

    /**
     * 文字列をトリムします。null の場合は空文字を返します。
     *
     * @param string|null $str トリム対象の文字列
     * @return string トリム後の文字列
     */
    public static function trimToEmpty(?string $str): string
    {
        if ($str === null) {
            return "";
        }
        return trim($str);
    }

    /**
     * 全角スペース（\u3000）や不揮発性スペース（\u00A0）を含めて文字列の両端をトリムします。
     *
     * @param string|null $str トリム対象の文字列
     * @return string|null トリム後の文字列。null の場合は null を返します。
     */
    public static function fullTrim(?string $str): ?string
    {
        if ($str === null || $str === '') {
            return $str;
        }

        // PHP's trim only handles standard whitespace by default.
        // We need to handle \u3000 (IDEOGRAPHIC SPACE) and \u00A0 (NO-BREAK SPACE)
        // Regex is the easiest way in PHP for multibyte trim
        return preg_replace('/^[\s\x{3000}\x{00A0}]+|[\s\x{3000}\x{00A0}]+$/u', '', $str);
    }

    /** 異体字などの変換マップ */
    private static $VAR_MAP = null;

    private static function loadVarMap(): void
    {
        if (self::$VAR_MAP !== null) {
            return;
        }

        self::$VAR_MAP = [];
        // Javaプロジェクトのリソースファイルを直接参照する
        $resourcePath = __DIR__ . '/../../src/main/resources/net/zamasoft/index/util/var.txt';
        
        if (!file_exists($resourcePath)) {
            throw new RuntimeException("Resource 'var.txt' not found at $resourcePath");
        }

        $lines = file($resourcePath, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
        if ($lines === false) {
            throw new RuntimeException("Failed to load var.txt");
        }

        foreach ($lines as $line) {
            $len = mb_strlen($line, 'UTF-8');
            if ($len < 2) {
                continue;
            }
            
            $toChar = mb_substr($line, 0, 1, 'UTF-8');
            for ($i = 1; $i < $len; $i++) {
                $fromChar = mb_substr($line, $i, 1, 'UTF-8');
                self::$VAR_MAP[$fromChar] = $toChar;
            }
        }
    }

    /**
     * ひらがなをカタカナに変換し、異体字を統一し、スペースを除去します。
     *
     * @param string|null $str 正規化対象の文字列
     * @return string|null 正規化後の文字列
     */
    public static function normalize(?string $str): ?string
    {
        if ($str === null) {
            return null;
        }

        self::loadVarMap();

        $chars = mb_str_split($str, 1, 'UTF-8');
        $result = '';

        foreach ($chars as $ch) {
            $code = mb_ord($ch, 'UTF-8');
            
            // 'ぁ' (U+3041) <= ch <= 'ん' (U+3093)
            // Convert to Katakana: 'ァ' (U+30A1)
            // Difference is 0x60 (96)
            if ($code >= 0x3041 && $code <= 0x3093) {
                $ch = mb_chr($code + 0x60, 'UTF-8');
            }

            if (isset(self::$VAR_MAP[$ch])) {
                $ch = self::$VAR_MAP[$ch];
            }
            $result .= $ch;
        }

        // Remove spaces (including full-width)
        $result = preg_replace('/[\s\x{3000}]/u', '', $result);

        return self::toZenkakuKatakana($result);
    }

    /**
     * 漢数字（正の整数）をアラビア数字の文字列に変換します。
     *
     * @param string $targetValue 対象の漢数字（例：「百二十三」）
     * @return string アラビア数字の文字列（例：「123」）
     */
    public static function convertKansuji(string $targetValue): string
    {
        if ($targetValue === '') {
            throw new InvalidArgumentException("Input string is empty or null");
        }

        if ($targetValue === "零") {
            return "0";
        }

        $firstDigit = 1;
        $fourthDigit = 0;
        $total = 0;
        
        $chars = mb_str_split($targetValue, 1, 'UTF-8');

        foreach ($chars as $kanjiNumber) {
            switch ($kanjiNumber) {
                case '一': $firstDigit = 1; break;
                case '二': $firstDigit = 2; break;
                case '三': $firstDigit = 3; break;
                case '四': $firstDigit = 4; break;
                case '五': $firstDigit = 5; break;
                case '六': $firstDigit = 6; break;
                case '七': $firstDigit = 7; break;
                case '八': $firstDigit = 8; break;
                case '九': $firstDigit = 9; break;
                case '十':
                    $fourthDigit += ($firstDigit != 0 ? $firstDigit : 1) * 10;
                    $firstDigit = 0;
                    break;
                case '百':
                    $fourthDigit += ($firstDigit != 0 ? $firstDigit : 1) * 100;
                    $firstDigit = 0;
                    break;
                case '千':
                    $fourthDigit += ($firstDigit != 0 ? $firstDigit : 1) * 1000;
                    $firstDigit = 0;
                    break;
                case '万':
                    $fourthDigit += $firstDigit;
                    $total += ($fourthDigit != 0 ? $fourthDigit : 1) * 10000;
                    $fourthDigit = 0;
                    $firstDigit = 0;
                    break;
                case '億':
                    $fourthDigit += $firstDigit;
                    $total += ($fourthDigit != 0 ? $fourthDigit : 1) * 100000000;
                    $fourthDigit = 0;
                    $firstDigit = 0;
                    break;
                default:
                    throw new InvalidArgumentException("Invalid kanji character found: " . $kanjiNumber);
            }
        }
        
        // Note: PHP integers can overflow if the number is too large, but for typical Kansuji usage it might be fine.
        // However, Java returns a String. PHP should also return a String to be safe and consistent.
        $result = $total + $fourthDigit + $firstDigit;
        return (string)$result;
    }

    /**
     * 数値を漢数字に変換します（100まで対応）。
     *
     * @param int $num 変換対象の数値
     * @return string|null 漢数字。対応範囲外の場合は null を返します。
     */
    public static function toKanji(int $num): ?string
    {
        if ($num < 0) {
            return null;
        } elseif ($num == 0) {
            return "〇";
        } elseif ($num <= 10) {
            return self::getKanjiDigit($num);
        } elseif ($num < 100) {
            $tens = intdiv($num, 10);
            $ones = $num % 10;
            return ($tens > 1 ? self::getKanjiDigit($tens) : "") . "十" . ($ones > 0 ? self::getKanjiDigit($ones) : "");
        } elseif ($num == 100) {
            return "百";
        } else {
            return null;
        }
    }

    /**
     * 1から10までの数値を漢数字一文字に変換します。
     *
     * @param int $num 変換対象の数値
     * @return string 漢数字。範囲外の場合は空文字を返します。
     */
    public static function getKanjiDigit(int $num): string
    {
        switch ($num) {
            case 1: return "一";
            case 2: return "二";
            case 3: return "三";
            case 4: return "四";
            case 5: return "五";
            case 6: return "六";
            case 7: return "七";
            case 8: return "八";
            case 9: return "九";
            case 10: return "十";
            default: return "";
        }
    }

    /**
     * 文字列の64ビットハッシュ値を計算します。
     * 内部で名前の正規化（normalize）を行ってからハッシュを生成します。
     * 
     * @param string $str 対象文字列
     * @return int 64ビットのハッシュ値
     */
    public static function hash(string $str): int
    {
        $normalized = self::normalize($str);
        
        // CRC32
        // PHP's crc32 returns an integer. On 32-bit systems it might be negative.
        // hash('crc32b', $str) returns hex string.
        // Java's CRC32 is standard.
        $crc = hexdec(hash('crc32b', $normalized));
        
        // Adler32
        $adler = hexdec(hash('adler32', $normalized));
        
        // Combine: (CRC << 32) | ADLER
        // PHP handles large integers automatically on 64-bit systems.
        return ($crc << 32) | $adler;
    }

    /**
     * 半角カタカナ（1文字）を全角カタカナに変換します。
     * 
     * @param string $c 変換前の文字
     * @return string 変換後の文字
     */
    public static function toZenkakuKatakanaChar(string $c): string
    {
        // mb_convert_kana with 'K' converts hankaku katakana to zenkaku katakana.
        // But we need to match Java's behavior exactly if possible.
        // Java uses a mapping array.
        // Let's use mb_convert_kana as it is standard in PHP.
        return mb_convert_kana($c, 'K', 'UTF-8');
    }

    /**
     * 文字列中の半角カタカナを全角カタカナに変換します。
     * 濁点・半濁点の合成も行います。
     * 
     * @param string|null $s 変換前文字列
     * @return string|null 変換後文字列
     */
    public static function toZenkakuKatakana(?string $s): ?string
    {
        if ($s === null) {
            return null;
        }
        // 'KV' option: K = Hankaku Katakana to Zenkaku Katakana, V = Collapse voiced sound marks
        return mb_convert_kana($s, 'KV', 'UTF-8');
    }

    /**
     * 文字列をスペース区切りのN-gram（バイグラム）形式に変換します。
     * 括弧内の処理など、特殊な正規化ルールが含まれます。
     * 
     * @param string $str 対象文字列
     * @return string スペース区切りの文字列
     */
    public static function toNgram(string $str): string
    {
        $state = 0;
        $buff = '';
        $chars = mb_str_split($str, 1, 'UTF-8');
        $len = count($chars);

        for ($j = 0; $j < $len; ++$j) {
            $d = $chars[$j];
            switch ($state) {
                case 0:
                    if ($d === '（') {
                        $state = 1;
                        continue 2;
                    }
                    break;

                case 1:
                    $state = 2;
                    continue 2;

                case 2:
                    $state = 0;
                    if ($d === '）') {
                        $buff .= $chars[$j - 2] . $chars[$j - 1];
                    } else {
                        $buff .= $chars[$j - 2];
                        $buff .= ' ';
                        $buff .= $chars[$j - 1];
                        $buff .= ' ';
                    }
                    break;
            }

            $buff .= $d;
            if ($j < $len - 1) {
                $buff .= ' ';
                if ($chars[$j + 1] === '　') {
                    ++$j;
                }
            }
        }
        switch ($state) {
            case 1:
                $buff .= $chars[$len - 1];
                break;
            case 2:
                $buff .= $chars[$len - 2];
                $buff .= ' ';
                $buff .= $chars[$len - 1];
                break;
        }
        return $buff;
    }
}
