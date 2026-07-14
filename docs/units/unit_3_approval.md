# Unit 3: 月次承認

## 目的

承認者・人事が社員の月次勤怠を一覧で確認し、承認できるようにする。

## ユーザーストーリー

- 承認者として、全社員の月次勤怠サマリーを一覧で確認したい
- 承認者として、特定社員の月次勤怠詳細を確認したい
- 承認者として、社員の月次勤怠を承認したい
- 人事として、全社員の月次勤怠を承認したい

## 前提

- Unit 0 完了済み（Entity, Repository, 認証基盤）
- Unit 1 完了済み（勤怠データが存在する状態）

## スコープ

### Backend

- ApprovalService interface + 実装
  - getApprovalList(): 月次承認一覧（全社員のサマリー）
  - approve(): 承認実行
- ApprovalController
- 権限チェック: APPROVER / HR ロールのみ
- 承認時に MonthlyApproval レコードを作成 or 更新

### Frontend

- 承認一覧画面（社員ごとの月次サマリー + 承認ボタン）
- 社員勤怠詳細画面（承認前の確認用）
- 承認完了フィードバック

## テーブル

- monthly_approvals（読み書き）
- attendances（読み取り: サマリー計算用）

## API

| メソッド | パス | 説明 |
|---------|------|------|
| GET | /api/approvals?yearMonth=YYYY-MM | 承認対象一覧 |
| PUT | /api/approvals/{employeeId} | 月次勤怠を承認 |

## ビジネスルール

1. APPROVER / HR ロールのみ操作可能 → 403 Forbidden
2. 承認ステータス: PENDING → APPROVED（一方向）
3. 承認者ID・承認日時を記録する
4. 既に承認済みの場合は再承認しない（冪等）

## 完了条件

- [ ] 承認 API のユニットテスト・統合テストが通る
- [ ] 権限チェックのテストが通る
- [ ] Frontend: 承認一覧 → 詳細確認 → 承認実行が確認できる
