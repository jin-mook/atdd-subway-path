package nextstep.subway.line.domain;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.*;
import nextstep.subway.station.StationFixtures;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("새로운 구역을 추가할 때 새로운 구역의 상행역과 하행역이 기존 구역에 존재하지 않으면 에러가 발생합니다.")
    @Test
    void noExist() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(section);

        // when
        Section newSection = new Section(StationFixtures.SECOND_UP_STATION, StationFixtures.SECOND_DOWN_STATION, 10L);
        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(NoStationException.class)
                .hasMessage(ErrorMessage.CANNOT_ADD_STATION.getMessage());
    }

    @Test
    @DisplayName("새로운 구역을 추가할 때 새로운 구역의 상행역과 하행역이 둘다 기존 구역에 존재한다면 에러가 발생합니다.")
    void existBoth() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(section);

        // when
        Section newSection = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.FIRST_UP_STATION, 10L);
        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(AlreadyHasUpAndDownStationException.class)
                .hasMessage(ErrorMessage.CANNOT_ADD_STATION.getMessage());

    }

    @Test
    @DisplayName("upStation 기준으로 새로운 구역을 추가할 때 새로운 구역의 길이가 기존 길이보다 크거나 같으면 에러가 발생합니다.")
    void largeDistanceUpStation() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(section);
        // when
        Section newSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.SECOND_DOWN_STATION, 10L);
        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalDistanceValueException.class)
                .hasMessage(ErrorMessage.LARGE_DISTANCE_THAN_CURRENT_SECTION.getMessage());
    }

    @Test
    @DisplayName("downStation 기준으로 새로운 구역을 추가할 때 새로운 구역의 길이가 기존 길이보다 크거나 같으면 에러가 발생합니다.")
    void largeDistanceDownStation() {
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(section);
        // when
        Section newSection = new Section(StationFixtures.SECOND_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalDistanceValueException.class)
                .hasMessage(ErrorMessage.LARGE_DISTANCE_THAN_CURRENT_SECTION.getMessage());
    }

    @DisplayName("새로운 구역을 추가할 때 기존에 존재하는 역이 아니라면 에러가 발생합니다.")
    @Test
    void noSameStation() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(section);

        Section newSection = new Section(StationFixtures.SECOND_UP_STATION, StationFixtures.SECOND_DOWN_STATION, 10L);
        // when
        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(NoStationException.class);
    }

    @DisplayName("새로운 구역의 하행역이 기존 구역에 존재한다면 에러가 발생합니다.")
    @Test
    void hasDownStation() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);

        sections.addSection(firstSection);

        Section secondSection = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.SECOND_DOWN_STATION, 10L);

        sections.addSection(secondSection);

        // when
        Section thirdSection = new Section(StationFixtures.SECOND_DOWN_STATION, StationFixtures.FIRST_UP_STATION, 10L);

        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(thirdSection))
                .isInstanceOf(AlreadyHasUpAndDownStationException.class);
    }

    @DisplayName("기존 구역이 존재하지 않으면 신규 구역을 추가합니다.")
    @Test
    void sectionEmpty() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        // when
        sections.addSection(section);

        // then
        Assertions.assertThat(sections.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("새로운 구간의 상행역이 기존 노선에 존재하는 역 조건을 만족할 때 신규 구간을 추가합니다.")
    void addWithUpStation() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(firstSection);

        // when
        // 1. 가장 뒤에 추가하는 경우
        Section secondSection = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.SECOND_DOWN_STATION, 10L);
        sections.addSection(secondSection);

        // 2. 중간에 추가하는 경우
        Section targetSection = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.SECOND_UP_STATION, 5L);
        sections.addSection(targetSection);

        // 3. 가장 앞에 추가하는 경우
        Section secondTargetSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.THIRD_UP_STATION, 3L);
        sections.addSection(secondTargetSection);

        // then
        Assertions.assertThat(sections.getSections()).hasSize(4)
                .containsExactly(secondTargetSection, firstSection, targetSection, secondSection);
        Assertions.assertThat(secondSection.getDistance()).isEqualTo(5L);
        Assertions.assertThat(firstSection.getDistance()).isEqualTo(7L);

        Assertions.assertThat(firstSection.getUpStation()).isEqualTo(StationFixtures.THIRD_UP_STATION);
        Assertions.assertThat(secondSection.getUpStation()).isEqualTo(StationFixtures.SECOND_UP_STATION);
    }

    @Test
    @DisplayName("새로운 구간의 하행역이 기존 노선에 존재하는 역 조건을 만족할 때 신규 구간을 추가합니다.")
    void addWithDownStation() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(firstSection);

        // when
        // 1. 가장 앞에 추가하는 경우
        Section secondSection = new Section(StationFixtures.SECOND_UP_STATION, StationFixtures.FIRST_UP_STATION, 10L);
        sections.addSection(secondSection);

        // 2. 중간에 추가하는 경우
        Section thirdSection = new Section(StationFixtures.THIRD_UP_STATION, StationFixtures.FIRST_UP_STATION, 3L);
        sections.addSection(thirdSection);

        // 3. 가장 마지막에 추가하는 경우
        Section targetSection = new Section(StationFixtures.THIRD_DOWN_STATION, StationFixtures.FIRST_DOWN_STATION, 5L);
        sections.addSection(targetSection);

        // then
        Assertions.assertThat(sections.getSections()).hasSize(4)
                .contains(secondSection, thirdSection, firstSection, targetSection);
        Assertions.assertThat(secondSection.getDistance()).isEqualTo(7L);
        Assertions.assertThat(firstSection.getDistance()).isEqualTo(5L);

        Assertions.assertThat(secondSection.getDownStation()).isEqualTo(StationFixtures.THIRD_UP_STATION);
        Assertions.assertThat(firstSection.getDownStation()).isEqualTo(StationFixtures.THIRD_DOWN_STATION);

    }

    @Test
    @DisplayName("마지막 역을 삭제할 때 구역이 존재하지 않는다면 에러가 발생합니다.")
    void noSection() {
        // given
        Sections sections = new Sections();
        // when
        // then
        Assertions.assertThatThrownBy(() -> sections.deleteSection(StationFixtures.FIRST_DOWN_STATION))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage(ErrorMessage.CANNOT_DELETE_SECTION.getMessage());
    }

    @Test
    @DisplayName("구역이 한 개만 존재한다면 에러가 발생합니다.")
    void oneSection() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(section);
        // when
        // then
        Assertions.assertThatThrownBy(() -> sections.deleteSection(StationFixtures.FIRST_DOWN_STATION))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage(ErrorMessage.CANNOT_DELETE_SECTION.getMessage());
    }

    @DisplayName("전달받은 역 정보가 상행역이라면 정상적으로 삭제합니다.")
    @Test
    void upStationDelete() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(firstSection);
        Section secondSection = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.SECOND_UP_STATION, 20L);
        sections.addSection(secondSection);
        // when
        sections.deleteSection(StationFixtures.FIRST_UP_STATION);
        // then
        Assertions.assertThat(sections.getSections()).hasSize(1)
                .contains(secondSection);
        Assertions.assertThat(secondSection.getLineOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("전달받은 역 정보가 마지막 구간에 존재하는 경우 삭제합니다.")
    void noLastStation() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(firstSection);
        Section secondSection = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.SECOND_UP_STATION, 20L);
        sections.addSection(secondSection);
        // when
        sections.deleteSection(StationFixtures.SECOND_UP_STATION);
        // then
        Assertions.assertThat(sections.getSections()).hasSize(1)
                .contains(firstSection);
        Assertions.assertThat(firstSection.getLineOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("전달받은 역 정보가 2개 이상의 구간에 존재하는 경우 정상적으로 역을 삭제합니다.")
    void targetSection() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
        sections.addSection(firstSection);
        Section secondSection = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.SECOND_UP_STATION, 20L);
        sections.addSection(secondSection);
        // when
        sections.deleteSection(StationFixtures.FIRST_DOWN_STATION);
        // then
        Assertions.assertThat(sections.getSections()).hasSize(1)
                .extracting("lineOrder", "upStation", "downStation", "distance")
                .contains(
                        Tuple.tuple(1, StationFixtures.FIRST_UP_STATION, StationFixtures.SECOND_UP_STATION, 30L)
                );
    }
}