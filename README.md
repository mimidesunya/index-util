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

## 動作環境

- Java 1.8 以上

## ビルド方法

Gradle を使用してビルドします。

```bash
./gradlew build
```

ビルドが成功すると、`public/libs` に JAR ファイルが、`public/javadoc` に API ドキュメントが自動的に出力されます。

## 公開リソース

GitHub 上のファイルおよびプレビュー表示へのリンクです。

- **[API ドキュメント (Javadoc)](https://raw.githack.com/mimidesunya/index-util/main/public/javadoc/index.html)** （ブラウザで表示）
- **[最新の JAR ファイル (ダウンロード)](https://github.com/mimidesunya/index-util/raw/main/public/libs/index-util-1.0-SNAPSHOT.jar)**

## 公開ディレクトリ (`public/`)

このプロジェクトでは、Git 管理および公開用に以下のディレクトリに成果物を集約しています。

- `public/libs/`: ビルド済みの JAR ファイル
- `public/javadoc/`: 生成された API ドキュメント (HTML)

## ライセンス

このプロジェクトのライセンスについては、別途お問い合わせください。
