# Index Util

文字列の正規化、漢数字変換、ハッシュ計算などを行う Java 用のユーティリティライブラリです。
Java 1.8 (Java 8) 互換でビルドされており、レガシーな環境でも動作します。

## 主な機能

- **文字列正規化 (`normalize`)**: 
  - ひらがなからカタカナへの変換
  - 異体字の統一（`var.txt` に基づく）
  - 半角カタカナから全角カタカナへの変換（濁点・半濁点の合成を含む）
  - 空白文字の除去
- **漢数字変換 (`convertKansuji`, `toKanji`)**:
  - 漢数字（例：「百二十三」）からアラビア数字文字列（例：「123」）への変換
  - 数値から漢数字への変換（100まで対応）
- **ハッシュ計算 (`hash`)**:
  - 文字列を正規化した上での 64ビットハッシュ値（CRC32 と Adler32 の組み合わせ）の生成
- **N-gram 生成 (`toNgram`)**:
  - 文字列をバイグラム形式に変換
- **トリム機能 (`fullTrim`)**:
  - 全角スペースや不揮発性スペース（`\u00A0`）を含めたトリム
- **半角変換 (`toHalfWidth`)**:
  - 全角アルファベット、数値、記号を半角に変換
- **全角カタカナ変換 (`toZenkakuKatakana`)**:
  - 半角カタカナを全角カタカナに変換（濁点合成対応）

また、同等の機能を持つ **PHP ライブラリ** も自動生成されます。

## 動作環境

- **Java**: Java 1.8 以上
- **PHP** (オプション): PHP 7.4 以上、Composer（PHP版のテストおよびビルドに必要）

## ビルド方法

Gradle を使用してビルドします。

```bash
./gradlew build
```

ビルドが成功すると、以下の成果物が `publish` ディレクトリに出力されます。

- `publish/libs`: JAR ファイル
- `publish/javadoc`: API ドキュメント
- `publish/php-lib`: PHP ライブラリ

※ PHP環境（`php`, `composer` コマンド）が利用可能な場合、ビルド時にPHPのテストも実行されます。

## 公開リソース

GitHub 上のファイルおよびプレビュー表示へのリンクです。

- **[API ドキュメント (Javadoc)](https://raw.githack.com/mimidesunya/index-util/main/publish/javadoc/index.html)** （ブラウザで表示）
- **[最新の JAR ファイル (ダウンロード)](https://raw.githack.com/mimidesunya/index-util/main/publish/libs/index-util-1.0-SNAPSHOT.jar)**
- **[PHP ライブラリ](https://raw.githack.com/mimidesunya/index-util/main/publish/php-lib/src/StringUtils.php)** (ソースコード)


## 公開ディレクトリ (`publish/`)

このプロジェクトでは、Git 管理および公開用に以下のディレクトリに成果物を集約しています。

- `publish/libs/`: ビルド済みの JAR ファイル
- `publish/javadoc/`: 生成された API ドキュメント (HTML)
- `publish/php-lib/`: 生成された PHP ライブラリ
  - `src/StringUtils.php`: 辞書データが埋め込まれたスタンドアロンで利用可能な PHP ファイル
  - `composer.json`: Composer 用設定ファイル

## PHP ライブラリの利用方法

生成された `publish/php-lib/src/StringUtils.php` は、`mbstring` 拡張モジュールが有効な PHP 環境であれば単独で動作します。

```php
require_once 'path/to/StringUtils.php';
use Zamasoft\Index\Util\StringUtils;

$normalized = StringUtils::normalize("文字列");
```


## ライセンス

このプロジェクトは [CC0 1.0 Universal (CC0 1.0) Public Domain Dedication](https://creativecommons.org/publicdomain/zero/1.0/) の下で公開されています。

