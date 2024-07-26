package nextstep.subway.path.infrastructure;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.exception.NotConnectedStationException;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationFixtures;
import nextstep.subway.utils.LineAssuredTemplate;
import nextstep.subway.utils.SectionAssuredTemplate;
import nextstep.subway.utils.StationAssuredTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JgraphtPathRepositoryTest {

    private long 논현역_id;
    private long 양재역_id;

    @Autowired
    private PathRepository pathRepository;

    @BeforeEach
    void setUp() {
        long 강남역_id = StationAssuredTemplate.createStation(StationFixtures.강남역.getName()).then().extract().jsonPath().getLong("id");
        long 양재역_id = StationAssuredTemplate.createStation(StationFixtures.양재역.getName()).then().extract().jsonPath().getLong("id");
        long 논현역_id = StationAssuredTemplate.createStation(StationFixtures.논현역.getName()).then().extract().jsonPath().getLong("id");
        long 고속터미널역_id = StationAssuredTemplate.createStation(StationFixtures.고속터미널역.getName()).then().extract().jsonPath().getLong("id");
        long 교대역_id = StationAssuredTemplate.createStation(StationFixtures.교대역.getName()).then().extract().jsonPath().getLong("id");

        long 신분당선_id = LineAssuredTemplate.createLine(new LineRequest("신분당선", "red", 논현역_id, 강남역_id, 4L)).then().extract().jsonPath().getLong("id");
        long 삼호선_id = LineAssuredTemplate.createLine(new LineRequest("3호선", "orange", 논현역_id, 고속터미널역_id, 2L)).then().extract().jsonPath().getLong("id");

        SectionAssuredTemplate.addSection(신분당선_id, new SectionRequest(강남역_id, 양재역_id, 3L));
        SectionAssuredTemplate.addSection(삼호선_id, new SectionRequest(고속터미널역_id, 교대역_id, 1L));
        SectionAssuredTemplate.addSection(삼호선_id, new SectionRequest(교대역_id, 양재역_id, 3L));

        this.논현역_id = 논현역_id;
        this.양재역_id = 양재역_id;
    }


    @DisplayName("source 역이 존재하지 않으면 에러가 발생합니다.")
    @Test
    void noSource() {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> pathRepository.findShortestPath(Long.MAX_VALUE, 양재역_id))
                .isInstanceOf(NoStationException.class)
                .hasMessage(ErrorMessage.NO_STATION_EXIST.getMessage());
    }

    @DisplayName("target 역이 존재하지 않으면 에러가 발생합니다.")
    @Test
    void noTarget() {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> pathRepository.findShortestPath(논현역_id, Long.MAX_VALUE))
                .isInstanceOf(NoStationException.class)
                .hasMessage(ErrorMessage.NO_STATION_EXIST.getMessage());
    }

    @DisplayName("source 역과 target 역이 연결되어 있지 않다면 에러가 발생합니다.")
    @Test
    void notConnect() {
        // given
        long 사당역_id = StationAssuredTemplate.createStation(StationFixtures.사당역.getName()).then().extract().jsonPath().getLong("id");
        long 방배역_id = StationAssuredTemplate.createStation(StationFixtures.방배역.getName()).then().extract().jsonPath().getLong("id");
        LineAssuredTemplate.createLine(new LineRequest("2호선", "green", 사당역_id, 방배역_id, 4L))
                .then().extract().jsonPath().getLong("id");
        // when
        // then
        Assertions.assertThatThrownBy(() -> pathRepository.findShortestPath(논현역_id, 사당역_id))
                .isInstanceOf(NotConnectedStationException.class)
                .hasMessage(ErrorMessage.NOT_CONNECTED_STATION.getMessage());
    }

    @DisplayName("source 역 id와 target 역 id 를 입력받으면 최단거리를 구간 목록을 전달합니다.")
    @Test
    void shortestSections() {
        // given
        // when
        Path path = pathRepository.findShortestPath(논현역_id, 양재역_id);
        List<Section> shortestPath = path.getSections();

        // then
        Assertions.assertThat(shortestPath).hasSize(3);

        List<Station> stationList = shortestPath.stream().flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());

        Assertions.assertThat(stationList).hasSize(4)
                .extracting("name")
                .containsExactly(
                        StationFixtures.논현역.getName(),
                        StationFixtures.고속터미널역.getName(),
                        StationFixtures.교대역.getName(),
                        StationFixtures.양재역.getName()
                );
    }
}