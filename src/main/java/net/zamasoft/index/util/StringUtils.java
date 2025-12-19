package net.zamasoft.index.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

/**
 * 文字列操作に関するユーティリティクラスです。
 */
public final class StringUtils {
	/**
	 * インスタンス化を防止するためのプライベートコンストラクタです。
	 */
	private StringUtils() {
		// ユーティリティクラスのためインスタンス化不要
	}

	/** 全角アルファベットと半角アルファベットの文字コードの差 */
	private static final int DIFFERENCE = 'Ａ' - 'A';

	/** 変換対象となる全角記号の配列 */
	private static final char[] FULL_WIDTH_SIGNS = {
			'！', '＃', '＄', '％', '＆', '（', '）', '＊', '＋', '，', '−', '－', '．', '／', '：', '；',
			'＜', '＝', '＞', '？', '＠', '［', '］', '＾', '＿', '｛', '｜', '｝'
	};

	/**
	 * 指定された文字が変換対象の全角記号かどうかを判定します。
	 *
	 * @param ch 判定対象の文字
	 * @return 変換対象であれば true
	 */
	private static boolean isFullWidthSign(final char ch) {
		for (final char sign : FULL_WIDTH_SIGNS) {
			if (sign == ch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 文字列内の全角アルファベット、数値、および特定の記号を半角に変換します。
	 *
	 * @param str 変換対象の文字列
	 * @return 変換後の文字列
	 */
	public static String toHalfWidth(final String str) {
		if (str == null) {
			return null;
		}
		final char[] chars = str.toCharArray();
		final StringBuilder sb = new StringBuilder(chars.length);
		for (final char c : chars) {
			char newChar = c;
			if ((('Ａ' <= c) && (c <= 'Ｚ')) || (('ａ' <= c) && (c <= 'ｚ')) || (('０' <= c) && (c <= '９')) || isFullWidthSign(c)) {
				newChar = (char) (c - DIFFERENCE);
			}
			sb.append(newChar);
		}
		return sb.toString();
	}

	/**
	 * 文字列をトリムします。null の場合は空文字を返します。
	 *
	 * @param str トリム対象の文字列
	 * @return トリム後の文字列
	 */
	public static String trimToEmpty(final String str) {
		if (str == null) {
			return "";
		}
		return str.trim();
	}

	/**
	 * 全角スペース（\u3000）や不揮発性スペース（\u00A0）を含めて文字列の両端をトリムします。
	 *
	 * @param str トリム対象の文字列
	 * @return トリム後の文字列。null の場合は null を返します。
	 */
	public static String fullTrim(final String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		int start = 0;
		int end = str.length();
		final char[] chars = str.toCharArray();
		while ((start < end) && (chars[start] <= '\u0020' || chars[start] == '\u00A0' || chars[start] == '\u3000')) {
			start++;
		}
		while ((start < end) && (chars[end - 1] <= '\u0020' || chars[end - 1] == '\u00A0' || chars[end - 1] == '\u3000')) {
			end--;
		}
		return ((start > 0) || (end < str.length())) ? str.substring(start, end) : str;
	}

	/** 異体字などの変換マップ */
	private static final Map<Character, Character> VAR_MAP;

	static {
		final Map<Character, Character> varMap = new HashMap<Character, Character>();
		final java.io.InputStream resource = StringUtils.class.getResourceAsStream("var.txt");
		if (resource == null) {
			throw new RuntimeException("Resource 'var.txt' not found.");
		}
		try (final BufferedReader in = new BufferedReader(new java.io.InputStreamReader(resource, StandardCharsets.UTF_8))) {
			String line;
			while ((line = in.readLine()) != null) {
				if (line.length() != line.codePointCount(0, line.length())) {
					continue;
				}
				final String toCode = new String(new int[] { line.codePointAt(0) }, 0, 1);
				for (int i = 1; i < line.codePointCount(0, line.length()); ++i) {
					final String fromCode = new String(new int[] { line.codePointAt(i) }, 0, 1);
					varMap.put(fromCode.charAt(0), toCode.charAt(0));
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load var.txt", e);
		}
		VAR_MAP = Collections.unmodifiableMap(varMap);
	}

	/**
	 * ひらがなをカタカナに変換し、異体字を統一し、スペースを除去します。
	 *
	 * @param str 正規化対象の文字列
	 * @return 正規化後の文字列
	 */
	public static String normalize(final String str) {
		if (str == null) {
			return null;
		}
		final char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			char ch = chars[i];
			if (ch >= 'ぁ' && ch <= 'ん') {
				ch = (char) (ch - 'ぁ' + 'ァ');
			}
			final Character variant = VAR_MAP.get(ch);
			if (variant != null) {
				ch = variant;
			}
			chars[i] = ch;
		}
		return toZenkakuKatakana(new String(chars).replaceAll("[\\s\u3000]", ""));
	}

	/**
	 * 漢数字（正の整数）をアラビア数字の文字列に変換します。
	 *
	 * @param targetValue 対象の漢数字（例：「百二十三」）
	 * @return アラビア数字の文字列（例：「123」）
	 */
	public static String convertKansuji(final String targetValue) {
		if (targetValue == null || targetValue.isEmpty()) {
			throw new IllegalArgumentException("Input string is empty or null");
		}

		if ("零".equals(targetValue)) {
			return "0";
		}

		int firstDigit = 1;
		int fourthDigit = 0;
		int total = 0;
		for (int i = 0; i < targetValue.length(); i++) {
			final char kanjiNumber = targetValue.charAt(i);
			switch (kanjiNumber) {
			case '一':
				firstDigit = 1;
				break;
			case '二':
				firstDigit = 2;
				break;
			case '三':
				firstDigit = 3;
				break;
			case '四':
				firstDigit = 4;
				break;
			case '五':
				firstDigit = 5;
				break;
			case '六':
				firstDigit = 6;
				break;
			case '七':
				firstDigit = 7;
				break;
			case '八':
				firstDigit = 8;
				break;
			case '九':
				firstDigit = 9;
				break;
			case '十':
				fourthDigit += (firstDigit != 0 ? firstDigit : 1) * 10;
				firstDigit = 0;
				break;
			case '百':
				fourthDigit += (firstDigit != 0 ? firstDigit : 1) * 100;
				firstDigit = 0;
				break;
			case '千':
				fourthDigit += (firstDigit != 0 ? firstDigit : 1) * 1_000;
				firstDigit = 0;
				break;
			case '万':
				fourthDigit += firstDigit;
				total += (fourthDigit != 0 ? fourthDigit : 1) * 10_000;
				fourthDigit = 0;
				firstDigit = 0;
				break;
			case '億':
				fourthDigit += firstDigit;
				total += (fourthDigit != 0 ? fourthDigit : 1) * 100_000_000;
				fourthDigit = 0;
				firstDigit = 0;
				break;
			default:
				throw new InvalidParameterException("Invalid kanji character found: " + kanjiNumber);
			}
		}
		return String.valueOf(total + fourthDigit + firstDigit);
	}

	/**
	 * 数値を漢数字に変換します（100まで対応）。
	 *
	 * @param num 変換対象の数値
	 * @return 漢数字。対応範囲外の場合は null を返します。
	 */
	public static String toKanji(final int num) {
		if (num < 0) {
			return null;
		} else if (num == 0) {
			return "〇";
		} else if (num <= 10) {
			return getKanjiDigit(num);
		} else if (num < 100) {
			final int tens = num / 10;
			final int ones = num % 10;
			return (tens > 1 ? getKanjiDigit(tens) : "") + "十" + (ones > 0 ? getKanjiDigit(ones) : "");
		} else if (num == 100) {
			return "百";
		} else {
			return null;
		}
	}

	/**
	 * 1から10までの数値を漢数字一文字に変換します。
	 *
	 * @param num 変換対象の数値
	 * @return 漢数字。範囲外の場合は空文字を返します。
	 */
	public static String getKanjiDigit(final int num) {
		switch (num) {
		case 1:
			return "一";
		case 2:
			return "二";
		case 3:
			return "三";
		case 4:
			return "四";
		case 5:
			return "五";
		case 6:
			return "六";
		case 7:
			return "七";
		case 8:
			return "八";
		case 9:
			return "九";
		case 10:
			return "十";
		default:
			return "";
		}
	}

	private static final CRC32 CRC = new CRC32();
	private static final Adler32 ADLER = new Adler32();

	/**
	 * 文字列の64ビットハッシュ値を計算します。
	 * 内部で名前の正規化（normalize）を行ってからハッシュを生成します。
	 * 
	 * @param str 対象文字列
	 * @return 64ビットのハッシュ値
	 */
	public static synchronized long hash(final String str) {
		final String normalized = StringUtils.normalize(str);
		try {
			final byte[] b = normalized.getBytes("UTF-8");
			CRC.reset();
			CRC.update(b);
			ADLER.reset();
			ADLER.update(b);
			return (CRC.getValue() << 32) | ADLER.getValue();
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static final char[] HANKAKU_KATAKANA = { '｡', '｢', '｣', '､', '･', 'ｦ', 'ｧ', 'ｨ', 'ｩ', 'ｪ', 'ｫ', 'ｬ', 'ｭ',
			'ｮ', 'ｯ', 'ｰ', 'ｱ', 'ｲ', 'ｳ', 'ｴ', 'ｵ', 'ｶ', 'ｷ', 'ｸ', 'ｹ', 'ｺ', 'ｻ', 'ｼ', 'ｽ', 'ｾ', 'ｿ', 'ﾀ', 'ﾁ', 'ﾂ',
			'ﾃ', 'ﾄ', 'ﾅ', 'ﾆ', 'ﾇ', 'ﾈ', 'ﾉ', 'ﾊ', 'ﾋ', 'ﾌ', 'ﾍ', 'ﾎ', 'ﾏ', 'ﾐ', 'ﾑ', 'ﾒ', 'ﾓ', 'ﾔ', 'ﾕ', 'ﾖ', 'ﾗ',
			'ﾘ', 'ﾙ', 'ﾚ', 'ﾛ', 'ﾜ', 'ﾝ', 'ﾞ', 'ﾟ' };

	private static final char[] ZENKAKU_KATAKANA = { '。', '「', '」', '、', '・', 'ヲ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ',
			'ョ', 'ッ', 'ー', 'ア', 'イ', 'ウ', 'エ', 'オ', 'カ', 'キ', 'ク', 'ケ', 'コ', 'サ', 'シ', 'ス', 'セ', 'ソ', 'タ', 'チ', 'ツ',
			'テ', 'ト', 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ', 'ハ', 'ヒ', 'フ', 'ヘ', 'ホ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ヤ', 'ユ', 'ヨ', 'ラ',
			'リ', 'ル', 'レ', 'ロ', 'ワ', 'ン', '゛', '゜' };

	private static final char HANKAKU_KATAKANA_FIRST_CHAR = HANKAKU_KATAKANA[0];

	private static final char HANKAKU_KATAKANA_LAST_CHAR = HANKAKU_KATAKANA[HANKAKU_KATAKANA.length - 1];

	/**
	 * 半角カタカナ（1文字）を全角カタカナに変換します。
	 * 
	 * @param c 変換前の文字
	 * @return 変換後の文字
	 */
	public static char toZenkakuKatakana(final char c) {
		if (c >= HANKAKU_KATAKANA_FIRST_CHAR && c <= HANKAKU_KATAKANA_LAST_CHAR) {
			return ZENKAKU_KATAKANA[c - HANKAKU_KATAKANA_FIRST_CHAR];
		} else {
			return c;
		}
	}

	/**
	 * 2文字目が濁点・半濁点で、1文字目に加えることができる場合は、合成した文字を返します。
	 * 合成ができないときは、c1を返します。
	 * 
	 * @param c1 変換前の1文字目
	 * @param c2 変換前の2文字目
	 * @return 変換後の文字
	 */
	public static char mergeKatakana(final char c1, final char c2) {
		if (c2 == '\uFF9E') {
			final int i = "\uFF76\uFF77\uFF78\uFF79\uFF7A\uFF7B\uFF7C\uFF7D\uFF7E\uFF7F\uFF80\uFF81\uFF82\uFF83\uFF84\uFF8A\uFF8B\uFF8C\uFF8D\uFF8E\uFF73".indexOf(c1);
			if (i >= 0) {
				return "\u30AC\u30AE\u30B0\u30B2\u30B4\u30B6\u30B8\u30BA\u30BC\u30BE\u30C0\u30C2\u30C5\u30C7\u30C9\u30D0\u30D3\u30D6\u30D9\u30DC\u30F4".charAt(i);
			}
		} else if (c2 == '\uFF9F') {
			final int i = "\uFF8A\uFF8B\uFF8C\uFF8D\uFF8E".indexOf(c1);
			if (i >= 0) {
				return "\u30D1\u30D4\u30D7\u30DA\u30DD".charAt(i);
			}
		}
		return c1;
	}

	/**
	 * 文字列中の半角カタカナを全角カタカナに変換します。
	 * 濁点・半濁点の合成も行います。
	 * 
	 * @param s 変換前文字列
	 * @return 変換後文字列
	 */
	public static String toZenkakuKatakana(final String s) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (i < s.length() - 1) {
				final char c2 = s.charAt(i + 1);
				final char m = mergeKatakana(c, c2);
				if (m != c) {
					sb.append(m);
					i++;
					continue;
				}
			}
			sb.append(toZenkakuKatakana(c));
		}
		return sb.toString();
	}

	/**
	 * 文字列をスペース区切りのN-gram（バイグラム）形式に変換します。
	 * 括弧内の処理など、特殊な正規化ルールが含まれます。
	 * 
	 * @param str 対象文字列
	 * @return スペース区切りの文字列
	 */
	public static String toNgram(final String str) {
		int state = 0;
		final StringBuffer buff = new StringBuffer();
		for (int j = 0; j < str.length(); ++j) {
			final char d = str.charAt(j);
			switch (state) {
			case 0:
				if (d == '（') {
					state = 1;
					continue;
				}
				break;

			case 1:
				state = 2;
				continue;

			case 2:
				state = 0;
				if (d == '）') {
					buff.append(str.subSequence(j - 2, j));
				} else {
					buff.append(str.charAt(j - 2));
					buff.append(' ');
					buff.append(str.charAt(j - 1));
					buff.append(' ');
				}
				break;
			}

			buff.append(d);
			if (j < str.length() - 1) {
				buff.append(' ');
				if (str.charAt(j + 1) == '　') {
					++j;
				}
			}
		}
		switch (state) {
		case 1:
			buff.append(str.charAt(str.length() - 1));
			break;
		case 2:
			buff.append(str.charAt(str.length() - 2));
			buff.append(' ');
			buff.append(str.charAt(str.length() - 1));
			break;
		}
		return buff.toString();
	}
}
