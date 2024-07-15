package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.SectionFixtures;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationFixtures;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        this.line = new Line("2호선", "green", SectionFixtures.FIRST_SECTION);
    }

    @Test
    void addSection() {
        // given
        // when
        line.addSection(SectionFixtures.ADD_FIRST_SECTION);

        // then
        Assertions.assertThat(line.getSections()).hasSize(2)
                .extracting("upStation", "downStation")
                .containsExactly(
                        Tuple.tuple(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION),
                        Tuple.tuple(StationFixtures.DOWN_STATION, StationFixtures.NEW_UP_STATION)
                );
    }

    @Test
    void getStations() {
        // given
        // when
        List<Station> result = line.getStations();

        // then
        Assertions.assertThat(result).hasSize(2)
                .extracting("name")
                .containsExactly(StationFixtures.UP_STATION.getName(), StationFixtures.DOWN_STATION.getName());
    }

    @Test
    void removeSection() {
        // given
        line.addSection(SectionFixtures.ADD_FIRST_SECTION);

        // when
        Section targetSection = line.findDeleteTargetSection(StationFixtures.NEW_UP_STATION.getId());
        line.deleteSection(targetSection);

        // then
        Assertions.assertThat(line.getSections()).hasSize(1)
                .extracting("upStation", "downStation")
                .containsExactly(
                        Tuple.tuple(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION)
                );
    }
}
