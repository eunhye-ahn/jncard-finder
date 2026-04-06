# jncard-finder
전남청년문화복지카드 가맹점 검색 서비스

## 트러블슈팅

### 문제
엑셀 파일로부터 19,000건의 데이터를 파싱하여 Elasticsearch에 저장을 시도했으나,
최종적으로 인덱스에는 22건의 데이터만 남는 현상 발생

- 애플리케이션 로그상에서는 19,000건 모두 저장 성공(saveAll 완료)으로 기록됨

### 원인
Elasticsearch의 Upsert(Update + Insert) 메커니즘에 의한 ID 중복 덮어쓰기

- 엑셀의 연번 컬럼을 StoreDocument의 id로 지정
- 파싱 과정에서 NUMERIC 타입의 연번이 `1.0, 2.0 ...` 형태의 문자열로 변환됨
- 소수점 이하가 동일한 값들이 같은 ID로 처리되어 Upsert로 덮어씌워짐

### 해결
ID 직접 지정 방식 → Elasticsearch 자동 생성 방식으로 변경

- StoreDocument 객체 생성 시 `.id()` 설정 제거
- @Id 필드가 null이면 Spring Data Elasticsearch가 ID 없이 요청을 전송
- Elasticsearch 서버가 각 문서에 고유 해시값(UUID)을 자동 부여
- 중복 문제 해소 및 19,000건 전량 저장 성공

### 공부
- 외부 데이터 기반 ID는 직접 지정보다 **자동 생성 방식을 지향**
- **로그상의 성공 수치와 실제 DB 저장 수치는 다를 수 있음** → 저장 후 건수 검증 필요
