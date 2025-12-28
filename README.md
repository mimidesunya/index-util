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

## インストール

### Java (Gradle)

[JitPack](https://jitpack.io/) を使用してインストールします。

`build.gradle` に以下を追加してください。

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.mimidesunya:index-util:Tag'
}
```

※ `Tag` には [Releases](https://github.com/mimidesunya/index-util/releases) のバージョン（例: `v1.0.0`）を指定してください。

### PHP (Composer)

`composer.json` に以下を追加してください。

```json
{
    "repositories": [
        {
            "type": "vcs",
            "url": "https://github.com/mimidesunya/index-util"
        }
    ],
    "require": {
        "zamasoft/index-util": "dev-php"
    }
}
```

その後、`composer update` を実行します。

## 動作環境

- **Java**: Java 1.8 以上
- **PHP** (オプション): PHP 7.4 以上、Composer（PHP版のテストおよびビルドに必要）

## ビルド方法

Gradle を使用してビルドします。

```bash
./gradlew build
```

ビルドが成功すると、`build` ディレクトリ以下に成果物が出力されます。

※ PHP環境（`php`, `composer` コマンド）が利用可能な場合、ビルド時にPHPのテストも実行されます。

## 公開リソース

- **[API ドキュメント (Javadoc)](https://mimidesunya.github.io/index-util/)**
- **[PHP ライブラリ (ソースコード)](https://github.com/mimidesunya/index-util/blob/php/src/StringUtils.php)**

## PHP ライブラリの利用方法

Composer を使用しない場合でも、生成された `StringUtils.php` は `mbstring` 拡張モジュールが有効な PHP 環境であれば単独で動作します。
[PHP ライブラリ (ソースコード)](https://github.com/mimidesunya/index-util/blob/php/src/StringUtils.php) からダウンロードして利用してください。

```php
require_once 'path/to/StringUtils.php';
use Zamasoft\Index\Util\StringUtils;

$normalized = StringUtils::normalize("文字列");
```



## ライセンス

このプロジェクトは [CC0 1.0 Universal (CC0 1.0) Public Domain Dedication](https://creativecommons.org/publicdomain/zero/1.0/) の下で公開されています。

