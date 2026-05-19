# API 명세서

현재 프로젝트의 실제 컨트롤러 구현 기준 API 명세서다.

## 공통 규칙

- Base URL: `/api/v1`
- Content-Type
  - 요청 본문이 있는 API: `application/json` (로그인은 `application/x-www-form-urlencoded`)
  - 응답 본문이 있는 API: `application/json`
- 날짜 형식: `yyyy-MM-dd`
- 시간 형식: `HH:mm`

## 인증

- **세션 기반 인증**을 사용한다. 로그인 성공 시 `Set-Cookie: JSESSIONID=...; HttpOnly` 응답으로 세션 쿠키를 발급한다.
- 인증이 필요한 API는 동일 origin에서 자동 첨부되는 `JSESSIONID` 쿠키로 사용자를 식별한다.
- 인증이 필요한 API에 비로그인 상태로 접근하면 `401 Unauthorized` (`AUTH401_002`)를 반환한다.

### 인증 정책

| 분류 | API |
| --- | --- |
| **공개** | `POST /api/v1/auth/login`, `GET /api/v1/themes`, `GET /api/v1/themes/popular`, `GET /api/v1/reservations/times`, `GET /api/v1/reservations/times/availability` |
| **인증 필요** | `GET /api/v1/auth/me`, `POST /api/v1/auth/logout`, `GET/POST/PATCH/DELETE /api/v1/reservations/**`, `POST/DELETE /api/v1/admin/themes/**`, `POST/DELETE /api/v1/admin/times/**` |

## 공통 에러 응답

```json
{
  "message": "올바른 입력값 형식이 아닙니다.",
  "errorCode": "COMMON400_004"
}
```

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `message` | `String` | 백엔드 에러 메시지 |
| `errorCode` | `String` | 프론트 분기 및 문서화용 에러 코드 |

---

## 0. 인증

### 0-1. 로그인

- `POST /api/v1/auth/login`
- Content-Type: `application/x-www-form-urlencoded`

#### 요청 본문 (form-encoded)

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `email` | `String` | Y | 회원 이메일 |
| `password` | `String` | Y | 회원 비밀번호 |

#### 응답

- 본문 없음
- 성공 시 `Set-Cookie: JSESSIONID=...; HttpOnly` 헤더로 세션 쿠키 발급

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 로그인 성공 |
| `401 Unauthorized` | 이메일 없음 또는 비밀번호 불일치 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `AUTH401_001` | 이메일 또는 비밀번호가 일치하지 않습니다 |

### 0-2. 현재 사용자 조회

- `GET /api/v1/auth/me`
- 인증 필요

#### 응답 예시

```json
{
  "id": 1,
  "email": "brown@email.com",
  "name": "브라운"
}
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 현재 로그인 사용자 정보 반환 |
| `401 Unauthorized` | 비로그인 상태 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `AUTH401_002` | 로그인이 필요한 요청입니다 |

### 0-3. 로그아웃

- `POST /api/v1/auth/logout`

#### 응답

- 본문 없음
- 서버 세션을 무효화한다 (`session.invalidate()`).

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `204 No Content` | 로그아웃 성공 (세션 없어도 동일) |

---

## 1. 예약

> 모든 예약 API는 **인증 필요**. 비로그인 시 `401 Unauthorized` (`AUTH401_002`).

### 1-1. 내 예약 목록 조회

- `GET /api/v1/reservations`
- 인증 필요

#### 조회 규칙

- 로그인한 사용자가 만든 예약만 반환한다.
- 쿼리 파라미터 없음.

#### 응답 예시

```json
[
  {
    "id": 1,
    "memberId": 1,
    "date": "2026-05-14",
    "time": {
      "id": 1,
      "startAt": "10:00"
    },
    "themeId": 1
  }
]
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 예약 목록 조회 성공 |
| `401 Unauthorized` | 비로그인 |

### 1-2. 예약 생성

- `POST /api/v1/reservations`
- 인증 필요. 예약자(`memberId`)는 **세션에서 자동 주입**되므로 요청 본문에 없다.

#### 요청 본문

```json
{
  "date": "2026-05-14",
  "timeId": 1,
  "themeId": 1
}
```

#### 요청 필드

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `date` | `String` | Y | 예약 날짜 |
| `timeId` | `Long` | Y | 예약 시간 ID |
| `themeId` | `Long` | Y | 테마 ID |

#### 검증 규칙

- `date`: null 불가, `yyyy-MM-dd` 형식
- `timeId`: null 불가, 양수
- `themeId`: null 불가, 양수
- 지나간 날짜·시간 예약 불가
- 같은 날짜 + 시간 + 테마 중복 예약 불가

#### 응답 예시

```json
{
  "id": 1,
  "memberId": 1,
  "date": "2026-05-14",
  "time": {
    "id": 1,
    "startAt": "10:00"
  },
  "themeId": 1
}
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `201 Created` | 예약 생성 성공 |
| `400 Bad Request` | 입력값 오류, 잘못된 날짜 형식, 지나간 날짜·시간 |
| `401 Unauthorized` | 비로그인 |
| `409 Conflict` | 같은 날짜 + 시간 + 테마 중복 예약 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `COMMON400_001` | 유효하지 않은 요청 필드 |
| `COMMON400_004` | 올바르지 않은 입력값 형식 |
| `RESERVATION400_001` | 지나간 날짜와 시간 예약 시도 |
| `RESERVATION409_001` | 이미 예약이 존재함 |
| `AUTH401_002` | 비로그인 |

### 1-3. 예약 삭제

- `DELETE /api/v1/reservations/{id}`
- 인증 필요. 본인 예약만 삭제 가능.

#### 경로 변수

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| `id` | `Long` | 예약 ID |

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `204 No Content` | 예약 삭제 성공 |
| `400 Bad Request` | 이미 지난 예약 취소 시도, 본인 예약이 아님 |
| `401 Unauthorized` | 비로그인 |
| `404 Not Found` | 존재하지 않는 예약 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `RESERVATION400_002` | 이미 지난 예약은 취소할 수 없음 |
| `RESERVATION400_003` | 본인 예약이 아님 |
| `RESERVATION404_001` | 존재하지 않는 예약 |
| `AUTH401_002` | 비로그인 |

### 1-4. 예약 날짜·시간 변경

- `PATCH /api/v1/reservations/{id}`
- 인증 필요. 본인 예약만 변경 가능.

#### 경로 변수

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| `id` | `Long` | 예약 ID |

#### 요청 본문

```json
{
  "date": "2026-05-15",
  "timeId": 2
}
```

#### 요청 필드

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `date` | `String` | Y | 변경할 예약 날짜 |
| `timeId` | `Long` | Y | 변경할 예약 시간 ID |

#### 검증 규칙

- `date`: null 불가, `yyyy-MM-dd` 형식
- `timeId`: null 불가, 양수
- **로그인 사용자가 예약 소유자와 일치해야 한다.**
- 존재하는 예약 시간으로만 변경할 수 있다.
- 지나간 날짜·시간으로 변경할 수 없다.

#### 응답 예시

```json
{
  "id": 1,
  "memberId": 1,
  "date": "2026-05-15",
  "time": {
    "id": 2,
    "startAt": "11:00"
  },
  "themeId": 1
}
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 예약 변경 성공 |
| `400 Bad Request` | 입력값 오류, 잘못된 날짜 형식, 지나간 날짜·시간, 본인 예약 아님 |
| `401 Unauthorized` | 비로그인 |
| `404 Not Found` | 존재하지 않는 예약 또는 예약 시간 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `COMMON400_001` | 유효하지 않은 요청 필드 |
| `COMMON400_004` | 올바르지 않은 입력값 형식 |
| `RESERVATION400_001` | 지나간 날짜와 시간으로 변경 시도 |
| `RESERVATION400_003` | 본인 예약이 아님 |
| `RESERVATION404_001` | 존재하지 않는 예약 |
| `RESERVATION_TIME404_001` | 존재하지 않는 예약 시간 |
| `AUTH401_002` | 비로그인 |

---

## 2. 예약 시간

### 2-1. 예약 시간 목록 조회

- `GET /api/v1/reservations/times`
- 공개

#### 응답 예시

```json
[
  {
    "id": 1,
    "startAt": "10:00"
  },
  {
    "id": 2,
    "startAt": "11:00"
  }
]
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 예약 시간 목록 조회 성공 |

### 2-2. 예약 가능 시간 조회

- `GET /api/v1/reservations/times/availability?date={date}&themeId={themeId}`
- 공개

#### 쿼리 파라미터

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `date` | `String` | Y | 조회 날짜 |
| `themeId` | `Long` | Y | 테마 ID |

#### 응답 예시

```json
[
  {
    "id": 1,
    "time": "10:00",
    "available": false
  },
  {
    "id": 2,
    "time": "11:00",
    "available": true
  }
]
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 예약 가능 시간 조회 성공 |
| `400 Bad Request` | 쿼리 문자열 누락, 날짜 형식 오류 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `COMMON400_003` | 쿼리 문자열 누락 |
| `COMMON400_005` | 쿼리 문자열 형식 오류 |

---

## 3. 테마

### 3-1. 테마 목록 조회

- `GET /api/v1/themes`
- 공개

#### 응답 예시

```json
[
  {
    "id": 1,
    "name": "이든의 공포 하우스",
    "description": "이든이 귀신으로 나오는 공포 테마",
    "imgUrl": "https://images.example.com/themes/horror-house.jpg"
  }
]
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 테마 목록 조회 성공 |

### 3-2. 인기 테마 조회

- `GET /api/v1/themes/popular?from={from}&to={to}`
- 공개

#### 쿼리 파라미터

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `from` | `String` | Y | 조회 시작일 |
| `to` | `String` | Y | 조회 종료일 |

#### 응답 예시

```json
[
  {
    "id": 1,
    "name": "이든의 공포 하우스",
    "description": "이든이 귀신으로 나오는 공포 테마",
    "imgUrl": "https://images.example.com/themes/horror-house.jpg",
    "rank": 1,
    "reservationCount": 7
  }
]
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `200 OK` | 인기 테마 조회 성공 |
| `400 Bad Request` | 쿼리 문자열 누락, 날짜 형식 오류 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `COMMON400_003` | 쿼리 문자열 누락 |
| `COMMON400_005` | 쿼리 문자열 형식 오류 |

---

## 4. 관리자 예약 시간

> 인증 필요. 비로그인 시 `401 Unauthorized` (`AUTH401_002`).

### 4-1. 예약 시간 생성

- `POST /api/v1/admin/times`
- 인증 필요

#### 요청 본문

```json
{
  "startAt": "10:00"
}
```

#### 요청 필드

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `startAt` | `String` | Y | 예약 시작 시간 |

#### 검증 규칙

- `startAt`: null 불가, `HH:mm` 형식

#### 응답 예시

```json
{
  "id": 1,
  "startAt": "10:00"
}
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `201 Created` | 예약 시간 생성 성공 |
| `400 Bad Request` | 입력값 오류, 시간 형식 오류 |
| `401 Unauthorized` | 비로그인 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `COMMON400_001` | 유효하지 않은 요청 필드 |
| `COMMON400_004` | 올바르지 않은 입력값 형식 |
| `AUTH401_002` | 비로그인 |

### 4-2. 예약 시간 삭제

- `DELETE /api/v1/admin/times/{id}`
- 인증 필요

#### 경로 변수

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| `id` | `Long` | 예약 시간 ID |

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `204 No Content` | 예약 시간 삭제 성공 |
| `401 Unauthorized` | 비로그인 |
| `404 Not Found` | 존재하지 않는 예약 시간 |
| `409 Conflict` | 예약이 존재하는 예약 시간 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `RESERVATION_TIME404_001` | 존재하지 않는 예약 시간 |
| `RESERVATION_TIME409_001` | 예약이 존재하는 예약 시간 |
| `AUTH401_002` | 비로그인 |

---

## 5. 관리자 테마

> 인증 필요. 비로그인 시 `401 Unauthorized` (`AUTH401_002`).

### 5-1. 테마 생성

- `POST /api/v1/admin/themes`
- 인증 필요

#### 요청 본문

```json
{
  "name": "이든의 공포 하우스",
  "description": "이든이 귀신으로 나오는 공포 테마",
  "imgUrl": "https://images.example.com/themes/horror-house.jpg"
}
```

#### 요청 필드

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `name` | `String` | Y | 테마 이름 |
| `description` | `String` | Y | 테마 설명 |
| `imgUrl` | `String` | Y | 테마 이미지 URL |

#### 검증 규칙

- `name`: 공백 불가, 2자 이상 100자 이하
- `description`: 공백 불가, 100자 이하
- `imgUrl`: 공백 불가, URL 형식

#### 응답 예시

```json
{
  "id": 1,
  "name": "이든의 공포 하우스",
  "description": "이든이 귀신으로 나오는 공포 테마",
  "imgUrl": "https://images.example.com/themes/horror-house.jpg"
}
```

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `201 Created` | 테마 생성 성공 |
| `400 Bad Request` | 입력값 오류 |
| `401 Unauthorized` | 비로그인 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `COMMON400_001` | 유효하지 않은 요청 필드 |
| `AUTH401_002` | 비로그인 |

### 5-2. 테마 삭제

- `DELETE /api/v1/admin/themes/{id}`
- 인증 필요

#### 경로 변수

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| `id` | `Long` | 테마 ID |

#### 응답 코드

| 상태 코드 | 설명 |
| --- | --- |
| `204 No Content` | 테마 삭제 성공 |
| `401 Unauthorized` | 비로그인 |
| `404 Not Found` | 존재하지 않는 테마 |
| `409 Conflict` | 예약이 존재하는 테마 |

#### 주요 에러 코드

| 에러 코드 | 설명 |
| --- | --- |
| `THEME404_001` | 존재하지 않는 테마 |
| `THEME409_001` | 예약이 존재하는 테마 |
| `AUTH401_002` | 비로그인 |

---

## 빠른 요약

| 메서드 | 경로 | 인증 | 설명 |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/login` | ❌ | 로그인 (세션 발급) |
| `GET` | `/api/v1/auth/me` | ✅ | 현재 사용자 조회 |
| `POST` | `/api/v1/auth/logout` | ❌ | 로그아웃 (세션 무효화) |
| `GET` | `/api/v1/reservations` | ✅ | 내 예약 목록 조회 |
| `POST` | `/api/v1/reservations` | ✅ | 예약 생성 |
| `PATCH` | `/api/v1/reservations/{id}` | ✅ | 예약 날짜·시간 변경 |
| `DELETE` | `/api/v1/reservations/{id}` | ✅ | 예약 삭제 |
| `GET` | `/api/v1/reservations/times` | ❌ | 예약 시간 목록 조회 |
| `GET` | `/api/v1/reservations/times/availability?date=&themeId=` | ❌ | 예약 가능 시간 조회 |
| `GET` | `/api/v1/themes` | ❌ | 테마 목록 조회 |
| `GET` | `/api/v1/themes/popular?from=&to=` | ❌ | 인기 테마 조회 |
| `POST` | `/api/v1/admin/times` | ✅ | 예약 시간 생성 |
| `DELETE` | `/api/v1/admin/times/{id}` | ✅ | 예약 시간 삭제 |
| `POST` | `/api/v1/admin/themes` | ✅ | 테마 생성 |
| `DELETE` | `/api/v1/admin/themes/{id}` | ✅ | 테마 삭제 |
