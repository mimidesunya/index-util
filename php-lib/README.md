# index-util (PHP)

日本語テキスト処理のための文字列ユーティリティライブラリです。

## インストール

Composer を使用してインストールします:

```bash
composer require zamasoft/index-util
```

## 使用方法

```php
<?php

require_once __DIR__ . '/vendor/autoload.php';

use Zamasoft\Index\Util\StringUtils;

// 全角英数字を半角に変換
$result = StringUtils::toHalfWidth('Ａｂｃ１２３');
// 結果: 'Abc123'

// 文字列の正規化（ひらがな→カタカナ変換、異体字統一など）
$result = StringUtils::normalize('あいう');
// 結果: 'アイウ'

// 漢数字をアラビア数字に変換
$result = StringUtils::convertKansuji('百二十三');
// 結果: '123'

// 数値を漢数字に変換
$result = StringUtils::toKanji(42);
// 結果: '四十二'

// 全角・半角スペースを考慮したトリム
$result = StringUtils::fullTrim('　テスト　');
// 結果: 'テスト'

// 半角カタカナを全角に変換
$result = StringUtils::toZenkakuKatakana('ｱｲｳ');
// 結果: 'アイウ'
```

## 主な機能

| メソッド | 説明 |
|---------|------|
| `toHalfWidth($str)` | 全角英数字・記号を半角に変換 |
| `normalize($str)` | ひらがな→カタカナ変換、異体字統一、スペース除去 |
| `convertKansuji($str)` | 漢数字をアラビア数字に変換 |
| `toKanji($num)` | 数値を漢数字に変換（100まで対応） |
| `fullTrim($str)` | 全角スペースを含めてトリム |
| `trimToEmpty($str)` | トリム（nullの場合は空文字を返す） |
| `toZenkakuKatakana($str)` | 半角カタカナを全角に変換（濁点合成含む） |
| `hash($str)` | 正規化した文字列の64ビットハッシュを計算 |
| `toNgram($str)` | N-gram形式（バイグラム）に変換 |

## 要件

- PHP 7.4 以上
- mbstring 拡張機能

## ライセンス

MIT License
