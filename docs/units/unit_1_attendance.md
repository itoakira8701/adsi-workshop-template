# Unit 1: 打刻・勤怠一覧

## 目的

社員が出退勤を打刻し、自分の勤怠を一覧で確認できるようにする。

## ユーザーストーリー

- 社員として、出勤ボタンをクリックして出勤時刻を記録したい
- 社員として、退勤ボタンをクリックして退勤時刻を記録したい
- 社員として、自分の勤怠一覧を日別・月別で確認したい
- 社員として、勤務時間と残業時間が自動計算されるのを確認したい

## 前提

- Unit 0 完了済み（Entity, Repository, 認証基盤）

## スコープ

### Backend

- AttendanceService interface + 実装
  - clockIn(): 出勤打刻
  - clockOut(): 退勤打刻 + 勤務時間計算
  - getToday(): 本日の打刻状態取得
  - getMonthly(): 月次勤怠一覧取得
- AttendanceController
- ビジネスルール実装:
  - 同日二重打刻の防止
  - 退勤は出勤後のみ可能
  - WorkDuration による勤務時間・残業時間計算
  - 休憩1時間の自動控除

### Frontend

- ダッシュボード画面（打刻ボタン + 本日の状態）
- 勤怠一覧画面（月別テーブル + 合計）
- 月切り替え（前月/翌月ナビゲーション）

## テーブル

- attendances（読み書き）

## API

| メソッド | パス | 説明 |
|---------|------|------|
| POST | /api/attendances/clock-in | 出勤打刻 |
| POST | /api/attendances/clock-out | 退勤打刻 |
| GET | /api/attendances/today | 本日の打刻状態 |
| GET | /api/attendances/me?yearMonth=YYYY-MM | 自分の月次勤怠一覧 |
| GET | /api/attendances/employees/{id}?yearMonth=YYYY-MM | 指定社員の月次勤怠（承認者/人事用） |

## ビジネスルール

1. 同一社員・同一日の出勤は1回のみ → 409 Conflict
2. 出勤記録がない日に退勤はできない → 400 Bad Request
3. 勤務時間 = 退勤 − 出勤 − 60分（休憩）
4. 残業時間 = max(0, 勤務時間 − 435分)

## 完了条件

- [ ] 打刻 API のユニットテスト・統合テストが通る
- [ ] 勤務時間計算ロジックのテストが通る
- [ ] Frontend: 打刻操作 → 一覧反映が確認できる
