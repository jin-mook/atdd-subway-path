package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.ErrorMessage;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineStationsResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationFixtures;
import nextstep.subway.utils.LineAssuredTemplate;
import nextstep.subway.utils.SectionAssuredTemplate;
import nextstep.subway.utils.StationAssuredTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("지하철 노선 구간 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {

    private long upStationId;
    private long downStationId;
    private long lineId;

    @BeforeEach
    void setUp() {
        this.upStationId = StationAssuredTemplate.createStation(StationFixtures.UP_STATION.getName())
                .then().extract().jsonPath().getLong("id");
        this.downStationId = StationAssuredTemplate.createStation(StationFixtures.DOWN_STATION.getName())
                .then().extract().jsonPath().getLong("id");
        this.lineId = LineAssuredTemplate.createLine(new LineRequest("신분당선", "red", upStationId, downStationId, 10L))
                .then().extract().jsonPath().getLong("id");
    }

    /**
     * Given 노선에 구간이 하나 등록되어 있습니다.
     * When 해당 노선에 신규 구간을 추가합니다.
     * Then 신규 구간의 상행역이 기존 구간의 하행역과 같지 않아 에러 응답을 보냅니다.
     */
    @DisplayName("새로운 구간 등록 시 새 구간의 상행역이 기존 구간의 하행역과 같지 않다면 에러 응답을 보냅니다.")
    @Test
    void invalidSection() {
        // given
        long newUpStationId = StationAssuredTemplate.createStation(StationFixtures.NEW_UP_STATION.getName())
                .then().extract().jsonPath().getLong("id");
        long newDownStationId = StationAssuredTemplate.createStation(StationFixtures.NEW_DOWN_STATION.getName())
                .then().extract().jsonPath().getLong("id");

        // when
        SectionRequest sectionRequest = new SectionRequest(newUpStationId, newDownStationId, 10L);
        ExtractableResponse<Response> result = SectionAssuredTemplate.addSection(lineId, sectionRequest)
                .then().log().all().extract();

        // then
        Assertions.assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(result.body().asString()).isEqualTo(ErrorMessage.NOT_SAME_UP_AND_DOWN_STATION.getMessage());
    }

    /**
     * Given 노선에 구간이 하나 등록되어 있습니다.
     * When 해당 노선에 신규 구간을 추가합니다.
     * Then 신규 구간의 하행역이 기존에 구역에 존재하기 때문에 에러 응답을 보냅니다.
     */
    @DisplayName("새로운 구간 등록 시 새 구간의 하행역이 기존 구간에 존재하는 역이라면 에러 응답을 보냅니다.")
    @Test
    void existDownStation() {
        // given

        // when
        SectionRequest sectionRequest = new SectionRequest(downStationId, upStationId, 10L);
        ExtractableResponse<Response> result = SectionAssuredTemplate.addSection(lineId, sectionRequest)
                .then().log().all().extract();

        // then
        Assertions.assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(result.body().asString()).isEqualTo(ErrorMessage.NOT_SAME_UP_AND_DOWN_STATION.getMessage());
    }

    /**
     * Given 노선에 구간이 하나 등록되어 있습니다.
     * When 해당 노선에 신규 구간을 추가합니다.
     * Then 추가된 신규 구간이 정상적으로 응답으로 보입니다.
     */
    @DisplayName("새로운 구간을 등록합니다.")
    @Test
    void addSection() {
        // given
        long newDownStationId = StationAssuredTemplate.createStation(StationFixtures.NEW_DOWN_STATION.getName())
                .then().extract().jsonPath().getLong("id");

        // when
        SectionRequest sectionRequest = new SectionRequest(downStationId, newDownStationId, 10L);
        ExtractableResponse<Response> result = SectionAssuredTemplate.addSection(lineId, sectionRequest)
                .then().log().all().extract();

        // then
        Assertions.assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(result.jsonPath().getList("stations", LineStationsResponse.class)).hasSize(3)
                .extracting("name")
                .containsExactly(
                        StationFixtures.UP_STATION.getName(),
                        StationFixtures.DOWN_STATION.getName(),
                        StationFixtures.NEW_DOWN_STATION.getName()
                );
    }

    /**
     * Given 노선에 구간이 한 개 등록되어 있습니다.
     * When 노선에 해당 구간을 제거합니다.
     * Then 구간이 한 개 이므로 제거할 수 없다는 응답을 전달받습니다.
     */
    @Test
    @DisplayName("구간이 한 개인 경우 구간을 제거할 수 없습니다.")
    void hasOneSection() {
        // given
        // when
        ExtractableResponse<Response> result = SectionAssuredTemplate.deleteSection(lineId, downStationId)
                .then().log().all().extract();

        // then
        Assertions.assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(result.body().asString()).isEqualTo(ErrorMessage.CANNOT_DELETE_SECTION.getMessage());
    }

    /**
     * Given 노선에 구간이 2개 등록되어 있습니다.
     * When 노선의 중간 역에 대해 삭제 요청을 보냅니다.
     * Then 삭제에 실패하고 실패 응답을 전달받습니다.
     */
    @Test
    @DisplayName("삭제하려는 역이 하행 종점역이 아닌 경우 삭제할 수 없습니다.")
    void notDownStation() {
        // given
        long newDownStationId = StationAssuredTemplate.createStation(StationFixtures.NEW_DOWN_STATION.getName())
                .then().extract().jsonPath().getLong("id");

        SectionRequest sectionRequest = new SectionRequest(downStationId, newDownStationId, 10L);
        SectionAssuredTemplate.addSection(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> result = SectionAssuredTemplate.deleteSection(lineId, downStationId)
                .then().log().all().extract();

        // then
        Assertions.assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(result.body().asString()).isEqualTo(ErrorMessage.CANNOT_DELETE_SECTION.getMessage());
    }

    /**
     * Given 노선에 구간이 2개 등록되어 있습니다.
     * When 가장 마지막 하행 종점역에 대한 삭제를 진행합니다.
     * Then 노선 정보를 요청하면 마지막 하행 종점역이 정상 삭제된 것을 확인할 수 있습니다.
     */
    @Test
    @DisplayName("정상적으로 하행 종점역 삭제가 진행됩니다.")
    void deleteStation() {
        // given
        long newDownStationId = StationAssuredTemplate.createStation(StationFixtures.NEW_DOWN_STATION.getName())
                .then().extract().jsonPath().getLong("id");

        SectionRequest sectionRequest = new SectionRequest(downStationId, newDownStationId, 10L);
        SectionAssuredTemplate.addSection(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> deleteResult = SectionAssuredTemplate.deleteSection(lineId, newDownStationId)
                .then().log().all().extract();

        Assertions.assertThat(deleteResult.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> lineResult = LineAssuredTemplate.searchOneLine(lineId)
                .then().log().all().extract();

        Assertions.assertThat(lineResult.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(lineResult.jsonPath().getList("stations", LineStationsResponse.class)).hasSize(2)
                .extracting("id", "name")
                .containsExactly(
                        Tuple.tuple(upStationId, StationFixtures.UP_STATION.getName()),
                        Tuple.tuple(downStationId, StationFixtures.DOWN_STATION.getName())
                );
    }
}
