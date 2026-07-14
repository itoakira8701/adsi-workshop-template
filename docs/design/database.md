# DB 設計

## ER 図

```
┌─────────────────────┐
│     employees       │
├─────────────────────┤
│ PK id               │
│    employee_code    │──┐
│    name             │  │
│    email            │  │
│    password         │  │
│    role             │  │
│    active           │  │
│    version          │  │
│    created_at       │  │
│    updated_at       │  │
└─────────────────────┘  │
         │               │
         │ 1             │
         │               │
         │ *             │
┌─────────────────────┐  │
│    attendances      │  │
├─────────────────────┤  │
│ PK id               │  │
│ FK employee_id      │──┤
│    work_date        │  │
│    clock_in_time    │  │
│    clock_out_time   │  │
│    working_minutes  │  │
│    overtime_minutes │  │
│    version          │  │
│    created_at       │  │
│    updated_at       │  │
└─────────────────────┘  │
                         │
         │ 1             │
         │               │
         │ *             │
┌─────────────────────┐  │
│ monthly_approvals   │  │
├─────────────────────┤  │
│ PK id               │  │
│ FK employee_id      │──┤
│    year_month       │  │
│    status           │  │
│ FK approver_id      │──┘
│    approved_at      │
│    version          │
│    created_at       │
│    updated_at       │
└─────────────────────┘
```

## テーブル定義

### employees（社員マスタ）

| カラム | 型 | NULL | 制約 | 説明 |
|--------|-----|:----:|------|------|
| id | BIGSERIAL | NO | PK | 主キー |
| employee_code | VARCHAR(50) | NO | UNIQUE | 社員コード（ログインID） |
| name | VARCHAR(100) | NO | | 氏名 |
| email | VARCHAR(255) | NO | UNIQUE | メールアドレス |
| password | VARCHAR(255) | NO | | BCrypt ハッシュ |
| role | VARCHAR(20) | NO | | EMPLOYEE / APPROVER / HR |
| active | BOOLEAN | NO | DEFAULT true | 有効フラグ |
| version | BIGINT | NO | DEFAULT 0 | 楽観ロック |
| created_at | TIMESTAMP | NO | DEFAULT now() | 作成日時 |
| updated_at | TIMESTAMP | NO | DEFAULT now() | 更新日時 |

### attendances（勤怠記録）

| カラム | 型 | NULL | 制約 | 説明 |
|--------|-----|:----:|------|------|
| id | BIGSERIAL | NO | PK | 主キー |
| employee_id | BIGINT | NO | FK(employees.id) | 社員ID |
| work_date | DATE | NO | | 勤務日 |
| clock_in_time | TIME | NO | | 出勤時刻 |
| clock_out_time | TIME | YES | | 退勤時刻 |
| working_minutes | INTEGER | YES | | 勤務時間（分） |
| overtime_minutes | INTEGER | YES | | 残業時間（分） |
| version | BIGINT | NO | DEFAULT 0 | 楽観ロック |
| created_at | TIMESTAMP | NO | DEFAULT now() | 作成日時 |
| updated_at | TIMESTAMP | NO | DEFAULT now() | 更新日時 |

**UNIQUE制約**: (employee_id, work_date) — 同一社員・同一日は1レコード

### monthly_approvals（月次承認）

| カラム | 型 | NULL | 制約 | 説明 |
|--------|-----|:----:|------|------|
| id | BIGSERIAL | NO | PK | 主キー |
| employee_id | BIGINT | NO | FK(employees.id) | 対象社員ID |
| year_month | VARCHAR(7) | NO | | 対象年月（YYYY-MM） |
| status | VARCHAR(20) | NO | DEFAULT 'PENDING' | PENDING / APPROVED |
| approver_id | BIGINT | YES | FK(employees.id) | 承認者ID |
| approved_at | TIMESTAMP | YES | | 承認日時 |
| version | BIGINT | NO | DEFAULT 0 | 楽観ロック |
| created_at | TIMESTAMP | NO | DEFAULT now() | 作成日時 |
| updated_at | TIMESTAMP | NO | DEFAULT now() | 更新日時 |

**UNIQUE制約**: (employee_id, year_month) — 同一社員・同一月は1レコード

## インデックス

```sql
-- attendances: 社員×年月での検索（一覧表示で多用）
CREATE INDEX idx_attendances_employee_work_date ON attendances (employee_id, work_date);

-- monthly_approvals: 年月での一括取得（承認一覧）
CREATE INDEX idx_monthly_approvals_year_month ON monthly_approvals (year_month);
```

## Flyway マイグレーション方針

- `V1__create_employees.sql` — employees テーブル作成
- `V2__create_attendances.sql` — attendances テーブル作成
- `V3__create_monthly_approvals.sql` — monthly_approvals テーブル作成
- `V4__insert_seed_data.sql` — 初期データ（管理者アカウント等）
