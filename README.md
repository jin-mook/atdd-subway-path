# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 프로그램 요구사항
- 인수 테스트 주도 개발 프로세스에 맞춰서 기능을 구현하세요.
  - 요구사항 설명을 참고하여 인수 조건을 정의
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
  - 뼈대 코드의 인수 테스트를 참고
- 인수 테스트 이후 기능 구현은 TDD로 진행하세요.
  - 도메인 레이어 테스트는 필수
  - 서비스 레이어 테스트는 선택

## 요구사항
### 🚀 2단계 - 지하철 구간 추가 리팩터링
- [X] 역 사이에 새로운 역을 등록
  - [X] 기존 역 사이 길이보다 작아야 등록 할 수 있다(크거나 같으면 등록 불가)
- [ ] 새로운 역을 상행 종점으로 등록
- [ ] 새로운 역을 하행 종점으로 등록
- [X] 새로운 구간을 등록 할 때 이미 노선에 상/하행역이 모두 등록되어 있는 경우 등록 불가
- [ ] 새로운 구간을 등록 할 때 상/하행역 둘 중 하나라도 기존 노선에 등록되어 있지 않으면 등록 불가
- [ ] 노선 조회시 상행 종점역 부터 하행 종점역으로 순차적으로 조회