# ドメインモデル設計

## ドメイン概要図

```
┌─────────────────────────────────────────────────────────┐
│                   勤怠管理ドメイン                         │
│                                                         │
│  ┌──────────┐      ┌──────────────┐     ┌───────────┐  │
│  │ Employee │─────>│ Attendance   │────>│ Approval  │  │
│  │  (社員)  │ 1  * │  (勤怠記録)  │ *  1│  (承認)   │  │
│  └──────────┘      └──────────────┘     └───────────┘  │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## Entity

### Employee（社員）

社員マスタ。認証情報とロールを保持する。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | Long | 主キー（自動採番） |
| employeeCode | String | 社員コード（ログインID） |
| name | String | 氏名 |
| email | String | メールアドレス |
| password | String | パスワード（BCrypt ハッシュ） |
| role | Role | ロール（EMPLOYEE / APPROVER / HR） |
| active | boolean | 有効フラグ（論理削除用） |
| version | Long | 楽観ロック用バージョン |
| createdAt | LocalDateTime | 作成日時 |
| updatedAt | LocalDateTime | 更新日時 |

### Attendance（勤怠記録）

1社員・1日につき1レコード。打刻情報と計算結果を保持する。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | Long | 主キー（自動採番） |
| employeeId | Long | 社員ID（FK） |
| workDate | LocalDate | 勤務日 |
| clockInTime | LocalTime | 出勤時刻 |
| clockOutTime | LocalTime | 退勤時刻（null可: 未退勤） |
| workingMinutes | Integer | 勤務時間（分）※計算値 |
| overtimeMinutes | Integer | 残業時間（分）※計算値 |
| version | Long | 楽観ロック用バージョン |
| createdAt | LocalDateTime | 作成日時 |
| updatedAt | LocalDateTime | 更新日時 |

### MonthlyApproval（月次承認）

1社員・1月につき1レコード。承認ステータスを管理する。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | Long | 主キー（自動採番） |
| employeeId | Long | 対象社員ID（FK） |
| yearMonth | YearMonth | 対象年月 |
| status | ApprovalStatus | 承認ステータス |
| approverId | Long | 承認者ID（FK、null可） |
| approvedAt | LocalDateTime | 承認日時（null可） |
| version | Long | 楽観ロック用バージョン |
| createdAt | LocalDateTime | 作成日時 |
| updatedAt | LocalDateTime | 更新日時 |

## Value Object

### Role（ロール）

```java
public enum Role {
    EMPLOYEE,  // 一般社員
    APPROVER,  // 承認者
    HR         // 人事
}
```

### ApprovalStatus（承認ステータス）

```java
public enum ApprovalStatus {
    PENDING,   // 未承認
    APPROVED   // 承認済み
}
```

### WorkDuration（勤務時間）

勤務時間の計算ロジックをカプセル化する Value Object。

```java
public record WorkDuration(int workingMinutes, int overtimeMinutes) {

    private static final int BREAK_MINUTES = 60;
    private static final int STANDARD_WORKING_MINUTES = 435; // 7時間15分

    public static WorkDuration calculate(LocalTime clockIn, LocalTime clockOut) {
        int totalMinutes = (int) Duration.between(clockIn, clockOut).toMinutes();
        int workingMinutes = totalMinutes - BREAK_MINUTES;
        int overtimeMinutes = Math.max(0, workingMinutes - STANDARD_WORKING_MINUTES);
        return new WorkDuration(workingMinutes, overtimeMinutes);
    }
}
```

## Repository（interface）

| Repository | 主な操作 |
|-----------|---------|
| EmployeeRepository | findByEmployeeCode, findAll, save, delete |
| AttendanceRepository | findByEmployeeIdAndWorkDate, findByEmployeeIdAndYearMonth, save |
| MonthlyApprovalRepository | findByEmployeeIdAndYearMonth, findByYearMonth, save |

## Service

| Service | 責務 |
|---------|------|
| AuthService | ログイン認証、トークン発行 |
| AttendanceService | 打刻（出勤/退勤）、勤務時間計算、勤怠一覧取得 |
| ApprovalService | 月次承認処理、承認ステータス取得 |
| EmployeeService | 社員マスタ CRUD |

## ビジネスルール

1. **打刻の重複禁止**: 同一社員・同一日の出勤は1回のみ
2. **退勤は出勤後**: 出勤記録がない日に退勤はできない
3. **休憩自動控除**: 12:00〜13:00 の1時間を勤務時間から控除
4. **残業は0以上**: 所定労働時間（435分）未満の場合、残業時間は0
5. **承認権限**: APPROVER / HR ロールのみ承認可能
6. **社員マスタ管理権限**: HR ロールのみ操作可能
