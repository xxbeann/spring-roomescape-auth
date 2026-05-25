## 실행 환경 설정

JWT 서명에 사용할 비밀 키는 소스에 하드코딩하지 않고 외부에서 주입합니다.

### 빠른 시작

프로젝트 루트에 `.env` 파일을 만들고 다음 내용을 채웁니다.

```properties
JWT_SECRET_KEY=<openssl rand -base64 32로 생성한 값>
JWT_EXPIRE_LENGTH=3600000
```

이후 평소처럼 실행합니다.

```bash
./gradlew bootRun
```

### JWT 키 생성

```bash
openssl rand -base64 32
```

### 환경 변수 명세

| 변수 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- |
| `JWT_SECRET_KEY` | 필수 | — | HS256 서명에 사용할 Base64 인코딩 키 (32바이트 이상 권장) |
| `JWT_EXPIRE_LENGTH` | 선택 | `3600000` | 토큰 만료 시간 (ms) |

`.env`는 `.gitignore`에 포함되어 커밋되지 않습니다.
값이 누락되면 앱 시작이 실패(fail-fast)합니다.

운영 환경에서는 `.env` 대신 OS 환경 변수 또는 시크릿 매니저(AWS Secrets Manager,
HashiCorp Vault 등)를 사용합니다. `spring.config.import`의 `optional:` 접두사
덕분에 `.env` 파일 없이 OS 환경 변수만으로도 동작합니다.

### 테스트 실행

```bash
./gradlew test
```

테스트는 별도 profile(`application-test.properties`)의 고정 키를 사용하므로
`.env` 설정 없이 실행 가능합니다.

---

## 사이클 1

### 기능명세서

#### 화면

- [x]  사용자는 화면을 통해 예약을 할 수 있다

#### 실행 및 확인 방법

- 애플리케이션 실행

```bash
./gradlew bootRun
```

- 사용자 예약 화면 접속

```text
http://localhost:8080
```

- H2 Console 접속

```text
http://localhost:8080/h2-console
```

- H2 접속 정보

```text
JDBC URL: jdbc:h2:mem:database
User Name: sa
Password: 비워두기
```

- 화면 확인용 초기 데이터는 `src/main/resources/data.sql`에 정의되어 있다.

#### 1단계

- [x]  모든 테마의 시작 시간과 소요 시간은 동일하다고 가정한다.
- [x]  테마는 이름, 설명, 썸네일 이미지 url을 가진다.
- [x]  예약에 테마 정보를 포함한다.
- [x]  관리자가 테마를 추가할 수 있다.
- [x]  관리자가 아닐경우 예외를 던진다.
- [x]  관리자가 테마를 삭제할 수 있다.
- [x]  존재하지 않는 테마는 삭제할 수 없다.
- [x]  예약이 걸려있는 테마는 삭제할 수 없다.

#### API 명세서

##### Theme - 01

- API 설명: 관리자가 테마를 추가할 수 있다.
- URI: `/api/v1/admin/themes`
- Method: `POST`
- Path Variable: 없음
- Query Variable: 없음

RequestBody

```json
{
  "name": "String",
  "description": "String",
  "imgUrl": "String"
}
```

ResponseBody

```json
{
  "id": 1,
  "name": "String",
  "description": "String",
  "imgUrl": "String"
}
```

- Status Code: `201`

##### Theme - 02

- API 설명: 관리자가 테마를 삭제할 수 있다.
- URI: `/api/v1/admin/themes/{id}`
- Method: `DELETE`
- Path Variable: `id`
- Query Variable: 없음

- RequestBody: 없음

- ResponseBody: 없음

- Status Code: `204`

- 이유

    - theme에 필요한 값들을 요청 필드로 받아서 post 요청을 보냈고,
      반환 값에서는 id를 추가해줘서 했다.
    - 관리자 기능은 일반 사용자 API와 분리하기 위해 `/api/v1/admin/themes` 경로로 분리했다.

#### 2단계

- [x]  사용자가 **날짜와 테마를 선택** 하면 예약 가능한 시간을 조회할 수 있다.
- [x]  예약 가능한 시간이란, 관리자가 등록한 시간 중 해당 날짜+테마에 아직 예약이 없는 시간이다.
- [x]  사용자가 예약 가능한 시간을 선택하여 본인의 이름으로 예약한다.
- [x]  사용자가 예약된 시간을 예약할 경우 예외를 던진다.
- [x]  같은 날짜·시간이라도 테마가 다르면 각각 예약 가능하다.

#### API 명세서

##### Times - 01

- API 설명: 사용자가 예약 가능한 시간을 조회할 수 있다.
- URI: `/api/v1/reservations/times?date=2026-05-08&themeId=1`
- Method: `GET`
- Path Variable: 없음
- Query Variable: `date`, `themeId`

- RequestBody: 없음

ResponseBody

```json
[
  {
    "id": 1,
    "time": "10:00",
    "available": true
  },
  {
    "id": 2,
    "time": "11:00",
    "available": false
  }
]
```

- Status Code: `200`

- 이유: 토론 활동에서 정한 규칙을 적용했습니다.

  (If-Then) 만약 사용자가 실제로 조회하고 싶은 대상이 있고, 다른 값들은 그 대상을 좁히기 위한 조건이라면<br>
  → 조회 대상은 리소스로 두고, 나머지 값들은 쿼리 파라미터로 표현한다.<br>
  (이유: 리소스는 사용자가 받고 싶은 핵심 대상을 기준으로 식별하고, 조건 값들은 그 결과를 필터링하는 역할이기 때문이다.)
  <br><br>
  (If-Then) 만약 어떤 값의 판단은 서버만 할 수 있지만, 그 결과를 어떻게 보여줄지는 클라이언트가 결정해도 된다면<br>
  → 서버는 판단 결과를 포함한 원본 데이터를 내려주고, 클라이언트가 이를 기준으로 필터링하거나 표시 방식을 결정한다.<br>
  (이유: 정합성과 비즈니스 규칙 판단은 서버의 책임이고, 화면 표현과 사용자 경험은 클라이언트의 책임이기 때문이다.)

#### 3단계

- [x] 최근 1주 동안 예약이 많았던 테마 상위 10개를 조회한다.
- [x] 예: 오늘이 5월 8일이면, 게임 날짜가 5월 1일~5월 7일인 예약을 집계해 인기 순서대로 10개를 응답한다.

#### API 명세서

##### Theme - 03

- API 설명: 최근 1주 동안 예약이 많았던 테마 상위 10개를 내림차순으로 조회한다.
- URI: `/api/v1/themes?from=2026-05-01&to=2026-05-07`
- Method: `GET`
- Path Variable: 없음
- Query Variable: `from`, `to`

- RequestBody: 없음

ResponseBody

```json
[
  {
    "id": 1,
    "name": "String",
    "description": "String",
    "imgUrl": "String",
    "rank": 1,
    "reservationCount": 10
  },
  {
    "id": 2,
    "name": "String",
    "description": "String",
    "imgUrl": "String",
    "rank": 2,
    "reservationCount": 9
  }
]
```

- Status Code: `200`

- 이유

    - (If-Then) 만약 사용자가 실제로 조회하고 싶은 대상이 있고, 다른 값들은 그 대상을 좁히기 위한 조건이라면<br>
      → 조회 대상은 리소스로 두고, 나머지 값들은 쿼리 파라미터로 표현한다.<br>
      (이유: 리소스는 사용자가 받고 싶은 핵심 대상을 기준으로 식별하고, 조건 값들은 그 결과를 필터링하는 역할이기 때문이다.)

    - rank 추가 이유 <br>
      → 백엔드에서 정렬해서 보내줄 수도 있지만, 프론트에서 볼 때 백엔드에서 넘어온 값이 순서대로<br>
      rank라는 보장이 없기 때문에, 가독성과 디버깅 편의성을 위해 rank를 추가해서 값을 넘겨줬습니다.<br>

    - 최근 1주 API를 from/to로 받은 이유<br>
      → 인기 테마 조회 API는 조회 기간을 명시적으로 표현하기 위해 `from`, `to` 쿼리 파라미터를 사용했다.<br>
      프론트가 현재 날짜만 전달하고 백엔드가 최근 1주를 계산하는 방식도 가능하지만,<br>
      이번 API에서는 클라이언트가 조회하고 싶은 기간을 명확히 전달하도록 설계했다.<br>
      이를 통해 `/api/v1/themes?from=2026-05-01&to=2026-05-07`처럼 어떤 기간의 인기 테마를 조회하는지 URL만 보고도 이해할 수 있다.
      즉, 현재 날짜 하나만 전달하는 방식보다 `from`, `to`를 사용한 방식이 조회 조건을 더 명시적으로 드러낸다고 판단했다.

### API 설계 규칙

1. 리소스 식별 기준

   (If-Then) 만약 사용자가 실제로 조회하고 싶은 대상이 있고, 다른 값들은 그 대상을 좁히기 위한 조건이라면
   → 조회 대상은 리소스로 두고, 나머지 값들은 쿼리 파라미터로 표현한다.
   (이유: 리소스는 사용자가 받고 싶은 핵심 대상을 기준으로 식별하고, 조건 값들은 그 결과를 필터링하는 역할이기 때문이다.)

2. 서버/클라이언트 책임 기준

   (If-Then) 만약 어떤 값의 판단은 서버만 할 수 있지만, 그 결과를 어떻게 보여줄지는 클라이언트가 결정해도 된다면
   → 서버는 판단 결과를 포함한 원본 데이터를 내려주고, 클라이언트가 이를 기준으로 필터링하거나 표시 방식을 결정한다.
   (이유: 정합성과 비즈니스 규칙 판단은 서버의 책임이고, 화면 표현과 사용자 경험은 클라이언트의 책임이기 때문이다.)

3. 관리자/사용자 API 분리 기준

   (If-Then) 만약 관리자와 사용자가 같은 리소스를 조회하더라도, 권한에 따라 제공해야 하는 정보나 수행 가능한 동작이 달라진다면
   → URL은 같게 두고 권한으로 분기한다.
   (이유: 같은 리소스를 다루더라도 목적, 응답 범위, 책임이 달라지면 엔드포인트를 분리하는 편이 더 명확하다.)

4. 우리 그룹의 "좋은 API" 정의

   만약 API를 추가해야한다면
   -> 이미 구현되어있는 API를 재사용 할 수 있는지 확인한다.
   (재사용 가능한 API를 좋은 API라고 부른다.)

(우선순위) URL을 결정할 때 순서:

1. 리소스를 명확히 한다 (명사형, 복수형)
2. 행위를 HTTP 메서드로 표현한다
3. 부가 조건은 쿼리/경로/본문 중 의미에 맞게 배치

(금지)

1. 이번 사이클에서 동사형 URL은 쓰지 않는다 (예: /reservations/create)
   이유: HTTP 메서드가 이미 동사 역할을 한다

2. 화면 명세가 바뀌었을 때 API가 바뀌면 안된다.<br>
   이유: 화면 명세가 바뀌었을 때 API가 수정되면, 화면에 API가 종속되어 재사용이 불가능하다는 신호이기 때문이다.

(페어규칙)

Controller는 가능한 Response DTO를 반환한다.
단, 단순 조회 API는 현재 단계에서 도메인 반환을 허용한다.<br>
계산값, 조합값, 화면 전용 필드가 포함되는 응답은 반드시 Response DTO를 사용한다.

## 사이클 2

### 기능명세서

#### **1단계 - 서비스 정책 적용**

다음 정책을 만족하지 않는 요청은 거부한다.

- [x] 지나간 날짜·시간에 대한 예약 생성은 불가능하다.
- [x] 같은 날짜+시간+테마에 이미 예약이 있으면 중복 예약을 거부한다.
- [x] 예약이 존재하는 시간을 삭제할 수 없다.
- [x] 유효하지 않은 입력값(빈 이름, 잘못된 날짜 형식 등)을 거부한다.

#### **2단계 - 에러 응답 설계**

- [x] 서비스 정책 위반, 유효하지 않은 입력, 존재하지 않는 리소스 등에 대해**의도된 에러 응답**을 반환한다.
- [x] **500(서버 에러)이 사용자에게 노출되지 않도록**한다.
- [x] 에러 응답 본문에**어떤 정보를 담을지 결정**한다.
- [x] 브라우저에서 에러 발생 시 사용자에게**의미 있는 메시지가 표시**되어야 한다.

#### **3단계 - 내 예약 조회/변경/취소**

- [x] 사용자가**자신의 이름으로 본인의 예약 목록을 조회**할 수 있다.
- [x] 사용자가 본인의 예약을**취소**할 수 있다.
- [x] 이미 지난 예약은 취소할 수 없다.
- [x] 사용자가 본인의 예약의**날짜·시간을 변경**할 수 있다.
- [x] 변경·취소 시 발생하는 에러 케이스(이미 지난 예약을 취소, 변경하려는 시간이 이미 차 있음 등)도 2단계의 규칙에 맞춰 처리한다.

## 사이클 3

### 기능명세서

#### **1단계 - 웹 세션/쿠키 로그인**

- [x] 사용자가 이메일·비밀번호로 로그인할 수 있다.
- [x] 로그인 성공 시 서버가 인증 정보를 발급하여 클라이언트에 전달한다.
- [x] 인증이 필요한 API는 비로그인 요청을 거부한다 (`401 AUTH401_002`).
- [x] 예약 생성 시 `memberId`는 요청 본문이 아닌 인증 정보에서 추출한다.

#### **2단계 - 모바일 인증 / JWT stateless 전환**

- [x] **JWT 기반 stateless 인증**으로 전환한다. 세션은 사용하지 않는다.
- [x] 웹 클라이언트: `Set-Cookie: access_token=<JWT>; HttpOnly; SameSite=Lax`로 발급.
- [x] 모바일 클라이언트: `POST /api/v1/auth/login/token` 응답 본문에 `{ "token": "..." }` 발급.
- [x] 서버는 `Authorization: Bearer ...` 헤더 우선, 없으면 `access_token` 쿠키로 토큰을 추출한다.
- [x] 토큰 검증 실패 유형을 구분한다: 위조·형식 오류(`AUTH401_003`) / 만료(`AUTH401_004`).
- [x] 컨트롤러에서 토큰·세션 직접 접근 코드를 제거하고 `@LoginMember`와 ArgumentResolver로 위임한다.
- [x] JWT 서명 키는 `.env`/환경 변수로 외부 주입한다 (소스에 하드코딩하지 않음).

#### **3단계 - 인가 / 매장 매니저 권한**

- [x] 회원에 `role`(USER / MANAGER) 개념을 도입한다.
- [x] 매니저는 `store_id`로 자기 매장에 묶인다.
- [x] 예약은 어떤 매장의 예약인지 식별할 수 있어야 한다 (`reservation.store_id`).
- [x] 매장 매니저는**자기 매장의 예약만** 조회·변경·삭제할 수 있다.
- [x] 다른 매장의 예약에 접근하면 거부한다 (`403 AUTH403_002`).
- [x] 매니저 권한이 없는 사용자가 매니저 API에 접근하면 거부한다 (`403 AUTH403_001`).
- [x] 인증 실패(`401`)와 인가 실패(`403`)를 같은 코드로 뭉개지 않고 명확히 구분한다.

#### 인가 판단 위치 — 설계 결정

| 판단 | 위치 | 메커니즘 |
| --- | --- | --- |
| 토큰 유효성 (인증) | `LoginCheckInterceptor` | JWT 서명·형식 검증 |
| 역할 (role check) | `LoginMemberArgumentResolver` | `@LoginMember(role = MANAGER)` 어노테이션 |
| 자원 범위 (storeId 비교) | `Reservation` 도메인 | `reservation.validateStoreOwnership(member)` |

- **Resolver**는 어노테이션에 명시된 role을 검증하고 Member를 주입한다.
- **도메인**은 자기 자신의 무결성(다른 매장 매니저가 수정 못 함)을 자기-방어한다.
- 컨트롤러 메서드 본문에는 권한 코드가 한 줄도 없다 — 시그니처(`@LoginMember(role = MANAGER) Member manager`)가 정책을 선언한다.

#### API 명세서

##### Stores - 01

- API 설명: 매장 목록을 조회한다.
- URI: `/api/v1/stores`
- Method: `GET`
- 인증: 공개

##### Admin Store Reservations - 01 ~ 03

- 자기 매장 예약 조회: `GET /api/v1/admin/store/reservations`
- 자기 매장 예약 변경: `PATCH /api/v1/admin/store/reservations/{id}`
- 자기 매장 예약 삭제: `DELETE /api/v1/admin/store/reservations/{id}`
- 인증: 로그인 + `MANAGER` 권한 필요
- 자세한 명세는 `API.md`의 §7 참고.

#### 회고

```
선택 도구: HandlerMethodArgumentResolver(@LoginMember) + 도메인 자기-방어 (validateStoreOwnership)
다른 후보:
  - Spring Security 도입 (학습 미션 범위 밖, 도입 비용 큼)
  - Interceptor 단독 처리 (어떤 자원에 어떤 권한 필요한지 path 패턴으로만 표현 → URL과 권한 결합)
  - Service 분산 처리 (모든 보호 메서드에 권한 코드 반복 → 누락 위험)
선택 이유:
  - role check는 자원과 무관한 결정 → 한 곳(Resolver)에 모으면 흩어짐 자체가 발생 안 함.
  - storeId 비교는 (사용자, 자원) 쌍이 필요 → 두 객체가 메모리에 있을 때 도메인이 자기 자신 보호.
  - 컨트롤러 시그니처가 정책 선언이 되어 의도가 시각적으로 드러남.
인가 판단 위치:
  - Resolver: role (사용자 인가)
  - Domain (Reservation): storeId 비교 (자원 인가)
유지하거나 변경하고 싶은 점:
  - 유지: 시그니처-기반 선언적 인가, 도메인 자기-방어 패턴.
  - 변경하고 싶은 점: 실제 운영 시스템이라면 Spring Security로 이관해 typed SecurityContext +
    표준 필터 체인을 사용. 토큰 만료 시 refresh token / 단일 세션 무효화 메커니즘 도입.
```
