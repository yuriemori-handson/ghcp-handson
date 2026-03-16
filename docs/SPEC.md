# 配送管理システム（Delivery Tracker）仕様書

## 1. プロジェクト概要

| 項目 | 内容 |
|---|---|
| **アプリ名** | Delivery Tracker |
| **目的** | GitHub Copilot ハンズオン用サンプルアプリケーション |
| **対象ユーザー** | 運送会社の開発者（ハンズオン参加者） |
| **説明** | 荷物の配送状況を管理するシンプルなWebアプリ。1画面・1エンティティで構成し、参加者の認知負荷を最小化しつつ、GitHub Copilotの各機能（コード補完・テスト生成・Chat・Coding Agent）を体験できる素材として設計する。 |

---

## 2. 設計方針

| 項目 | 方針 |
|---|---|
| **画面数** | 1画面のみ |
| **エンティティ** | 1つ（Delivery） |
| **フロントエンド** | Thymeleaf（Spring Boot組み込み） + Bootstrap 5（CDN） |
| **データベース** | H2インメモリDB（設定不要・起動即使用可能） |
| **認知負荷** | 極力削減。Javaの知識だけで読めるコード構成にする |

---

## 3. 技術スタック

| 技術 | バージョン | 用途 |
|---|---|---|
| **Java** | 17 | メイン言語 |
| **Spring Boot** | 3.x | Webフレームワーク |
| **Thymeleaf** | Spring Boot同梱 | サーバーサイドテンプレートエンジン |
| **Spring Data JPA** | Spring Boot同梱 | データアクセス層 |
| **H2 Database** | Spring Boot同梱 | インメモリDB |
| **Bootstrap** | 5.x（CDN） | UIスタイリング |
| **Maven** | 3.x | ビルドツール |
| **JUnit 5** | Spring Boot同梱 | テストフレームワーク |

---

## 4. 開発・実行環境

### 4.1 GitHub Codespaces 設定

本プロジェクトはGitHub Codespacesで即座に起動できるよう `.devcontainer/devcontainer.json` を用意する。

```json
{
  "name": "Delivery Tracker - Java Dev",
  "image": "mcr.microsoft.com/devcontainers/java:17",
  "features": {
    "ghcr.io/devcontainers/features/java:1": {
      "version": "17",
      "installMaven": true
    }
  },
  "customizations": {
    "vscode": {
      "extensions": [
        "vscjava.vscode-java-pack",
        "vmware.vscode-spring-boot",
        "vscjava.vscode-spring-initializr",
        "vscjava.vscode-spring-boot-dashboard",
        "github.copilot",
        "github.copilot-chat"
      ],
      "settings": {
        "java.configuration.runtimes": [
          {
            "name": "JavaSE-17",
            "default": true
          }
        ]
      }
    }
  },
  "forwardPorts": [8080],
  "postCreateCommand": "mvn dependency:resolve",
  "portsAttributes": {
    "8080": {
      "label": "Delivery Tracker App",
      "onAutoForward": "openBrowser"
    }
  }
}
```

### 4.2 Codespaces 利用手順（参加者向け）

```
1. GitHubリポジトリのページを開く
2. 右上の「Code」ボタンをクリック
3. 「Codespaces」タブを選択
4. 「Create codespace on main」をクリック
5. ブラウザ上でVSCodeが起動するまで待つ（約1〜2分）
6. ターミナルで以下を実行：
   $ mvn spring-boot:run
7. ポート8080が自動転送され、ブラウザでアプリが開く
```

### 4.3 ローカル環境（任意）

| 必要ソフトウェア | バージョン |
|---|---|
| JDK | 17以上 |
| Maven | 3.8以上 |
| VSCode | 最新版 |
| VSCode拡張機能 | Extension Pack for Java, Spring Boot Extension Pack, GitHub Copilot, GitHub Copilot Chat |

```bash
git clone https://github.com/yuriemori-handson/ghcp-handson.git
cd ghcp-handson
mvn spring-boot:run
# http://localhost:8080 をブラウザで開く
```

---

## 5. ドメインモデル

### 5.1 Delivery エンティティ

| フィールド名 | 型 | 必須 | 説明 |
|---|---|---|---|
| `id` | Long | ✅ | 主キー（自動採番） |
| `trackingNumber` | String | ✅ | 送り状番号（例：TRK-0001） |
| `recipientName` | String | ✅ | 受取人名 |
| `recipientAddress` | String | ✅ | 届け先住所 |
| `status` | DeliveryStatus | ✅ | 配送ステータス |
| `createdAt` | LocalDateTime | ✅ | 登録日時（自動設定） |

### 5.2 DeliveryStatus（Enum）

| 値 | 表示名 | バッジ色（Bootstrap） |
|---|---|---|
| `PENDING` | 集荷待ち | `bg-secondary`（グレー） |
| `IN_TRANSIT` | 配送中 | `bg-warning text-dark`（黄） |
| `DELIVERED` | 配達完了 | `bg-success`（緑） |
| `DELAYED` | 遅延 | `bg-danger`（赤） |

---

## 6. 画面仕様（1画面のみ）

### 6.1 画面イメージ

```
┌─────────────────────────────────────────────────────────┐
│ 🚚 配送管理                                              │
├─────────────────────────────────────────────────────────┤
│ ステータスで絞り込み: [すべて ▼]                         │
├──────────┬───────────┬───────────────┬──────────────────┤
│ 送り状番号 │  受取人名  │  ステータス   │      操作        │
├──────────┼───────────┼───────────────┼──────────────────┤
│ TRK-0001 │ 山田 太郎  │ 🟡 配送中     │ [完了] [遅延]    │
│ TRK-0002 │ 鈴木 花子  │ 🟢 配達完了   │                  │
│ TRK-0003 │ 中村 健二  │ 🔴 遅延       │                  │
│ TRK-0004 │ 高橋 美咲  │ ⚪ 集荷待ち   │ [配送中へ]       │
└──────────┴───────────┴───────────────┴──────────────────┘

  ─── 新規登録フォーム ──────────────────────────────────
  受取人名  [ 山田 太郎           ]
  届け先住所 [ 東京都新宿区〇〇    ]
                               [ 登録する ]
  ───────────────────────────────────────────────────────
```

### 6.2 URL・エンドポイント一覧

| HTTPメソッド | URL | 説明 |
|---|---|---|
| `GET` | `/` | 配送一覧表示（トップページ） |
| `GET` | `/?status=IN_TRANSIT` | ステータスで絞り込み |
| `POST` | `/deliveries` | 新規配送を登録 |
| `POST` | `/deliveries/{id}/status` | ステータスを更新 |

### 6.3 画面の機能詳細

| 機能 | 説明 |
|---|---|
| 配送一覧表示 | 全配送をテーブルで表示。送り状番号・受取人名・ステータスバッジ・操作ボタンを表示 |
| ステータス絞り込み | プルダウンで選択するとGETパラメータで絞り込み |
| ステータス更新 | 現在のステータスに応じて操作ボタンを動的に変更（下表参照） |
| 新規登録 | 画面下部のインラインフォームから受取人名・住所を入力して登録 |

### 6.4 ステータス別 操作ボタン

| 現在のステータス | 表示ボタン | 遷移先ステータス |
|---|---|---|
| `PENDING` | [配送中へ] | `IN_TRANSIT` |
| `IN_TRANSIT` | [完了] [遅延] | `DELIVERED` / `DELAYED` |
| `DELIVERED` | （なし） | - |
| `DELAYED` | （なし） | - |

---

## 7. サービス層仕様

### DeliveryService メソッド一覧

| メソッド名 | 引数 | 戻り値 | 説明 | 実装状態 |
|---|---|---|---|---|
| `findAll()` | - | `List<Delivery>` | 全件取得（登録日時降順） | ✅ 実装済み |
| `findByStatus(status)` | `DeliveryStatus` | `List<Delivery>` | ステータスで絞り込み | ✅ 実装済み |
| `create(recipientName, recipientAddress)` | `String, String` | `Delivery` | 新規登録・送り状番号を採番 | ✅ 実装済み |
| `updateStatus(id, status)` | `Long, DeliveryStatus` | `Delivery` | ステータスを更新 | ✅ 実装済み |
| `countByStatus(status)` | `DeliveryStatus` | `long` | ステータス別件数を返す | ⭐ **未実装（補完課題①）** |
| `calcDelayedRate()` | - | `double` | 遅延率(%)を計算して返す | ⭐ **未実装（補完課題②）** |

### 送り状番号の採番ルール

- フォーマット：`TRK-XXXX`（4桁ゼロ埋め）
- 採番方法：現在の最大ID + 1 をゼロ埋めして生成
- 例：`TRK-0001`、`TRK-0002`

---

## 8. プロジェクト構成

```
ghcp-handson/
├── .devcontainer/
│   └── devcontainer.json           ← Codespaces設定
├── docs/
│   ├── SPEC.md                     ← 本仕様書
│   └── shipping-fee-spec.md        ← Doc to Code 課題用仕様書
├── src/
│   ├── main/
│   │   ├── java/com/example/delivery/
│   │   │   ├── DeliveryTrackerApplication.java
│   │   │   ├── controller/
│   │   │   │   └── DeliveryController.java
│   │   │   ├── service/
│   │   │   │   └── DeliveryService.java       ← 未実装メソッドあり
│   │   │   ├── repository/
│   │   │   │   └── DeliveryRepository.java
│   │   │   └── model/
│   │   │       ├── Delivery.java
│   │   │       └── DeliveryStatus.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   └── index.html                 ← 画面テンプレート（1ファイル）
│   │       ├── application.properties
│   │       └── data.sql                       ← 初期データ
│   └── test/
│       └── java/com/example/delivery/
│           └── service/
│               └── DeliveryServiceTest.java   ← テスト生成課題用（一部のみ実装）
└── pom.xml
```

---

## 9. 設定ファイル

### application.properties

```properties
spring.application.name=delivery-tracker
server.port=8080

# H2 インメモリDB
spring.datasource.url=jdbc:h2:mem:deliverydb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# 初期データ投入
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data.sql

# H2コンソール（開発確認用）
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Thymeleaf
spring.thymeleaf.cache=false
```

### 初期データ（data.sql）

```sql
INSERT INTO delivery (tracking_number, recipient_name, recipient_address, status, created_at)
VALUES ('TRK-0001', '山田 太郎', '東京都新宿区西新宿1-1-1', 'IN_TRANSIT', NOW());

INSERT INTO delivery (tracking_number, recipient_name, recipient_address, status, created_at)
VALUES ('TRK-0002', '鈴木 花子', '東京都渋谷区道玄坂2-2-2', 'DELIVERED', NOW());

INSERT INTO delivery (tracking_number, recipient_name, recipient_address, status, created_at)
VALUES ('TRK-0003', '中村 健二', '埼玉県さいたま市大宮区3-3-3', 'DELAYED', NOW());

INSERT INTO delivery (tracking_number, recipient_name, recipient_address, status, created_at)
VALUES ('TRK-0004', '高橋 美咲', '東京都品川区大崎4-4-4', 'PENDING', NOW());
```

---

## 10. ハンズオン課題一覧

### 課題①：コード補完（DeliveryService）

`DeliveryService.java` の以下2メソッドはメソッドシグネチャとJavadocコメントのみ記載され、本体が未実装。
Copilotの補完サジェストを使って実装する。

- `countByStatus(DeliveryStatus status)` ：指定ステータスの件数を返す
- `calcDelayedRate()` ：全配送に対する遅延率(%)を返す

### 課題②：テストコード生成

`DeliveryServiceTest.java` には `findAll()` のテストのみ実装済み。
`/tests` コマンドまたはチャットを使って `countByStatus()` と `calcDelayedRate()` のテストを生成する。

### 課題③：Code to Doc（Javadoc生成）

`DeliveryService.java` 全体のJavadocが未記載。
`/doc` コマンドを使ってJavadocを自動生成する。

### 課題④：Ask モード（コードベース解析）

`@workspace` を使ってプロジェクト全体に関する質問をする。
- 「このプロジェクトのアーキテクチャを説明して」
- 「配送ステータスの更新はどこで行われていますか？」

### 課題⑤：Plan モード（機能追加の計画）

以下のお題でPlanモードを使い、実装ステップを立案する。
- 「配送一覧に登録日時の列を追加したい」

### 課題⑥：Agent モード（実装）

課題⑤で作成したPlanをもとに、Agentモードで実装を依頼する。

### 課題⑦：Doc to Code（仕様書からコード生成）

`docs/shipping-fee-spec.md` の仕様書を参照させて、料金計算クラスを生成する。

### 課題⑧：Coding Agent（IssueからPR自動生成）

以下のIssueを起票し、CopilotをAssigneeにしてPRを自動生成させる。

```
タイトル：遅延配送の自動検出機能を追加する

本文：
登録から24時間以上経過しても DELIVERED になっていない配送を
自動的に DELAYED ステータスへ更新するスケジューラーを実装してほしい。

受け入れ条件：
- 1時間ごとに自動チェックが走ること（@Scheduled を使用）
- 対象：PENDING または IN_TRANSIT で24時間以上経過した配送
- ステータスを DELAYED に更新すること
```

---

## 11. Doc to Code 用仕様書（docs/shipping-fee-spec.md）

以下の内容を `docs/shipping-fee-spec.md` として別途作成する。

```markdown
# 配送料金計算仕様書

## 概要
配送サイズと距離に応じて配送料金を計算するクラスを実装すること。

## 料金テーブル

| サイズ区分 | 目安サイズ | 基本料金 |
|---|---|---|
| 小 | 60cm以内 | 800円 |
| 中 | 80cm以内 | 1,200円 |
| 大 | 100cm以内 | 1,600円 |
| 特大 | 100cm超 | 2,500円 |

## 距離割増料金

| 距離 | 割増料金 |
|---|---|
| 100km以内 | なし |
| 100km〜300km | +500円 |
| 300km超 | +1,000円 |

## 特別条件
- 離島向け配送：上記合計に一律 +1,500円
- 5個以上の同時配送：合計金額から10%割引（端数切り捨て）
```
