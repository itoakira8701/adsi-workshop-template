# Unit 0: 共通基盤

## 目的

プロジェクトの骨格を作り、**他の Unit がテスト付きで実装を開始できる状態**にする。

## スコープ

### Backend

- Spring Boot プロジェクト初期化（Gradle / Maven）
- PostgreSQL + Flyway 設定
- Flyway マイグレーション: V1（employees）、V2（attendances）、V3（monthly_approvals）
- 全 Entity クラス（Employee, Attendance, MonthlyApproval）
- 全 Enum（Role, ApprovalStatus）
- Value Object（WorkDuration）
- Repository interface（Spring Data JPA）
- Spring Security + JWT 認証基盤
  - SecurityFilterChain（認可ルール設定）
  - JwtTokenProvider（トークン生成・検証）
  - UserDetailsService 実装
- 認証 API: POST /api/auth/login, GET /api/auth/me
- 共通エラーハンドリング（@RestControllerAdvice）
- テスト基盤（test profile / H2 or Testcontainers 設定）
- V4: シードデータ（管理者アカウント）

### Frontend

- Next.js プロジェクト初期化
- 共通レイアウト（ナビゲーション）
- API クライアント基盤（withBasePath 対応）
- 認証ヘルパー（トークン保存・リクエスト付与）
- ログイン画面
- 認証ガード（未ログイン時リダイレクト）

## テーブル

- employees（作成のみ。CRUD は Unit 2）
- attendances（スキーマのみ。書き込みは Unit 1）
- monthly_approvals（スキーマのみ。書き込みは Unit 3）

## API

| メソッド | パス | 説明 |
|---------|------|------|
| POST | /api/auth/login | ログイン（JWT 発行） |
| GET | /api/auth/me | ログインユーザー情報取得 |

## 完了条件

- [ ] `./gradlew test` (or `mvn test`) が通る
- [ ] Flyway マイグレーションが実行される
- [ ] ログイン API でトークンが返る
- [ ] テストプロファイルで Repository テストが実行できる
- [ ] Frontend: ログイン画面 → トークン取得 → 認証付きリクエストが通る
