# Unit 2: 社員マスタ管理

## 目的

人事ロールが社員の登録・編集・削除（論理削除）・ロール割り当てを画面から行えるようにする。

## ユーザーストーリー

- 人事として、社員を新規登録したい
- 人事として、社員情報を編集したい（氏名・メール・ロール）
- 人事として、社員を無効化（論理削除）したい
- 人事として、社員一覧を確認したい

## 前提

- Unit 0 完了済み（Entity, Repository, 認証基盤）

## スコープ

### Backend

- EmployeeService interface + 実装
  - getAll(): 社員一覧
  - getById(): 社員詳細
  - create(): 社員登録（パスワード BCrypt ハッシュ化）
  - update(): 社員更新（楽観ロック付き）
  - delete(): 論理削除（active = false）
- EmployeeController
- バリデーション:
  - 社員コード: 必須、一意
  - メール: 必須、一意、形式チェック
  - パスワード: 8文字以上
  - ロール: EMPLOYEE / APPROVER / HR のいずれか
- 権限チェック: HR ロールのみアクセス可

### Frontend

- 社員マスタ一覧画面
- 社員登録フォーム
- 社員編集フォーム
- 削除確認ダイアログ
- バリデーションエラー表示

## テーブル

- employees（CRUD）

## API

| メソッド | パス | 説明 |
|---------|------|------|
| GET | /api/employees | 社員一覧 |
| GET | /api/employees/{id} | 社員詳細 |
| POST | /api/employees | 社員登録 |
| PUT | /api/employees/{id} | 社員更新 |
| DELETE | /api/employees/{id} | 社員削除（論理削除） |

## ビジネスルール

1. HR ロールのみ操作可能 → 403 Forbidden
2. 社員コード・メールの重複禁止 → 409 Conflict
3. 楽観ロック: version 不一致 → 409 Conflict
4. 削除は論理削除（active = false）

## 完了条件

- [ ] CRUD API のユニットテスト・統合テストが通る
- [ ] 権限チェックのテストが通る
- [ ] Frontend: 社員の登録→一覧表示→編集→削除が確認できる
