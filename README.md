# jncard-finder 
전남청년문화복지카드 가맹점 검색 서비스

## 설계전략

### RDB - ES 관계 전략
- RDB의 `id(PK)` 값을 ES의 `storeId(keyword)`에 동일하게 저장
- 검색은 ES에서, 상세 조회/연산은 RDB에서 처리
- ES 검색 결과의 `storeId`로 RDB 조회

### id 설계 전략
- ES `_id`는 메타필드로 정렬 불가 → 커서 페이징 보조 정렬키로 사용 불가
- `storeId(keyword)` 를 별도 필드로 저장 → 정렬 + RDB 연결 두 역할 담당

| | 필드 | 타입 | 역할 |
|--|------|------|------|
| ES | `_id` | 메타필드 | ES 문서 식별 (정렬 불가) |
| ES | `storeId` | keyword | RDB 연결 + 커서 정렬키 |
| RDB | `id` | PK | storeId와 동일한 값 |

### 커서 페이징 전략
- offset 페이징은 데이터가 많을수록 성능 저하 → 커서 페이징 채택
- 기본 정렬키 : `_score` (검색 연관도)
- 보조 정렬키 : `storeId` (동점일 때 순서 보장 + RDB 연결)
- 다음 페이지 있을 때만 nextCursor 생성 (없으면 null)
  - lastHit → encodeCursor() → url로 전달
