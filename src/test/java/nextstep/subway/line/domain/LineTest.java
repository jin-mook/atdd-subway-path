package nextstep.subway.line.domain;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.line.SectionFixtures;
import nextstep.subway.station.StationFixtures;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("노선의 이름을 수정합니다.")
    void updateLineName() {
        // given
        Line line = new Line("기존 이름", "기존 색", SectionFixtures.FIRST_SECTION);
        String newName = "새로운 이름";
        // when
        line.updateName(newName);
        // then
        Assertions.assertThat(line.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("노선의 색을 수정합니다.")
    void updateLineColor() {
        // given
        Line line = new Line("기존 이름", "기존 색", SectionFixtures.FIRST_SECTION);
        String newColor = "새로운 색";
        // when
        line.updateColor(newColor);
        // then
        Assertions.assertThat(line.getColor()).isEqualTo(newColor);
    }

    @DisplayName("기존에 존재하지 않는 역의 구간은 추가할 수 없습니다.")
    @Test
    void notExistStationSection() {
        // given
        Line line = new Line("기존 이름", "기존 색", SectionFixtures.FIRST_SECTION);
        line.addSection(SectionFixtures.ADD_FIRST_SECTION);
        // when
        // then
        Assertions.assertThatThrownBy(() -> line.addSection(new Section(StationFixtures.THIRD_UP_STATION, StationFixtures.THIRD_DOWN_STATION, 10L)))
                .isInstanceOf(NoStationException.class)
                .hasMessage(ErrorMessage.CANNOT_ADD_STATION.getMessage());
    }

    @DisplayName("노선에 새로운 구간을 추가합니다.")
    @Test
    void addSection() {
        // given
        Line line = new Line("기존 이름", "기존 색", SectionFixtures.FIRST_SECTION);
        line.addSection(SectionFixtures.ADD_FIRST_SECTION);
        // when
        line.addSection(SectionFixtures.SECOND_SECTION);
        // then
        Assertions.assertThat(line.getSections()).hasSize(3)
                .containsExactly(
                        SectionFixtures.FIRST_SECTION,
                        SectionFixtures.ADD_FIRST_SECTION,
                        SectionFixtures.SECOND_SECTION
                );
    }

    @DisplayName("노선 중간에 존재하는 역은 삭제할 수 없습니다.")
    @Test
    void canNotDeleteSectionMiddle() {
        // given
        Line line = new Line("기존 이름", "기존 색", SectionFixtures.FIRST_SECTION);
        line.addSection(SectionFixtures.ADD_FIRST_SECTION);
        line.addSection(SectionFixtures.SECOND_SECTION);
        // when
        // then
        Assertions.assertThatThrownBy(() -> line.findDeleteTargetSectionByStationId(StationFixtures.FIRST_DOWN_STATION.getId()))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage(ErrorMessage.CANNOT_DELETE_SECTION.getMessage());
    }

    @DisplayName("노선 마지막에 존재하는 역은 삭제할 수 있습니다.")
    @Test
    void deleteSection() {
        // given
        Line line = new Line("기존 이름", "기존 색", SectionFixtures.FIRST_SECTION);
        line.addSection(SectionFixtures.ADD_FIRST_SECTION);
        line.addSection(SectionFixtures.SECOND_SECTION);
        // when
        Section targetSection = line.findDeleteTargetSectionByStationId(StationFixtures.SECOND_DOWN_STATION.getId());
        line.deleteSection(targetSection);
        // then
        Assertions.assertThat(line.getSections()).hasSize(2)
                .containsExactly(
                        SectionFixtures.FIRST_SECTION,
                        SectionFixtures.ADD_FIRST_SECTION
                );
    }
}