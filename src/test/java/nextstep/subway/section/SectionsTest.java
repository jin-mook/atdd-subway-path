package nextstep.subway.section;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NotSameUpAndDownStationException;
import nextstep.subway.station.StationFixtures;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("새로운 구역을 추가할 때 새로운 구역의 상행역과 기존 마지막 구역의 하행역이 다르면 에러가 발생합니다.")
    @Test
    void noSameStation() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
        sections.addSection(section);

        Section newSection = new Section(StationFixtures.NEW_UP_STATION, StationFixtures.NEW_DOWN_STATION, 10L);
        // when
        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(NotSameUpAndDownStationException.class);
    }

    @DisplayName("새로운 구역의 하행역이 기존 구역에 존재한다면 에러가 발생합니다.")
    @Test
    void hasDownStation() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);

        sections.addSection(firstSection);

        Section secondSection = new Section(StationFixtures.DOWN_STATION, StationFixtures.NEW_DOWN_STATION, 10L);

        sections.addSection(secondSection);

        // when
        Section thirdSection = new Section(StationFixtures.NEW_DOWN_STATION, StationFixtures.UP_STATION, 10L);

        // then
        Assertions.assertThatThrownBy(() -> sections.addSection(thirdSection))
                .isInstanceOf(NotSameUpAndDownStationException.class);
    }

    @DisplayName("기존 구역이 존재하지 않으면 신규 구역을 추가합니다.")
    @Test
    void sectionEmpty() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
        // when
        sections.addSection(section);

        // then
        Assertions.assertThat(sections.getSections()).hasSize(1);
    }

    @DisplayName("기존 구역의 하행역과 신규 구역의 상행역이 동일하고 신규 구역의 하행역이 기존 구역에 존재하지 않으면 신규 구역을 추가합니다.")
    @Test
    void sameUpDownStation() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
        sections.addSection(section);

        Section newSection = new Section(StationFixtures.DOWN_STATION, StationFixtures.NEW_DOWN_STATION, 10L);
        // when
        sections.addSection(newSection);
        // then
        Assertions.assertThat(sections.getSections()).hasSize(2);
    }

    @Test
    @DisplayName("마지막 역을 삭제할 때 구역이 존재하지 않는다면 에러가 발생합니다.")
    void noSection() {
        // given
        Sections sections = new Sections();
        // when
        // then
        Assertions.assertThatThrownBy(() -> sections.getDeleteTargetSection(StationFixtures.DOWN_STATION.getId()))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage(ErrorMessage.CANNOT_DELETE_SECTION.getMessage());
    }

    @Test
    @DisplayName("구역이 한 개만 존재한다면 에러가 발생합니다.")
    void oneSection() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
        sections.addSection(section);
        // when
        // then
        Assertions.assertThatThrownBy(() -> sections.getDeleteTargetSection(StationFixtures.DOWN_STATION.getId()))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage(ErrorMessage.CANNOT_DELETE_SECTION.getMessage());
    }

    @Test
    @DisplayName("전달받은 역 정보가 마지막 구간의 하행역이 아닌경우 에러가 발생합니다.")
    void noLastStation() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
        sections.addSection(firstSection);
        Section secondSection = new Section(StationFixtures.DOWN_STATION, StationFixtures.NEW_UP_STATION, 20L);
        sections.addSection(secondSection);
        // when
        // then
        Assertions.assertThatThrownBy(() -> sections.getDeleteTargetSection(StationFixtures.NEW_DOWN_STATION.getId()))
                .isInstanceOf(CannotDeleteSectionException.class);
    }

    @Test
    @DisplayName("전달받은 역 정보가 2개 이상의 구간의 마지막 하행역인 경우 해당 구간을 전달합니다.")
    void targetSection() {
        // given
        Sections sections = new Sections();
        Section firstSection = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
        sections.addSection(firstSection);
        Section secondSection = new Section(StationFixtures.DOWN_STATION, StationFixtures.NEW_UP_STATION, 20L);
        sections.addSection(secondSection);
        // when
        Section targetSection = sections.getDeleteTargetSection(StationFixtures.NEW_UP_STATION.getId());
        // then
        Assertions.assertThat(targetSection).isEqualTo(secondSection);
    }
}