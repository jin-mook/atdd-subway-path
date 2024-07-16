# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 1단계 요구사항 정리
- 구간 중간에 역을 추가할 수 있도록 변경됨
  - 기존에는 마지막 구간 뒤에만 역을 추가할 수 있었음
  - 추가하는 upStation 은 존재하고 downStation 은 노선에 없어야 합니다.
  - 반대로 추가하는 downStation 은 존재하고 upStation 은 노선에 없어야 합니다.
  - 즉, 둘 중 하나만 있어야 합니다.
  - 둘 다 있는 경우 에러 발생
  - 기존 구간보다 큰 distance 로 추가할 수 없습니다.

## Sections 기능 정의
1. 구간 추가
- upStation, downStation 중 하나는 반드시 기존 구간에 존재해야 한다.
- 나머지 Station 은 반드시 기존 구간에 없어야 한다.
- 전달받은 distance 값은 추가하려는 기존 구간의 distance 보다 작아야 한다.
