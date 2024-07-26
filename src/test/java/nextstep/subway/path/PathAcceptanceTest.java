package nextstep.subway.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationFixtures;
import nextstep.subway.utils.LineAssuredTemplate;
import nextstep.subway.utils.SectionAssuredTemplate;
import nextstep.subway.utils.StationAssuredTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("경로 조회 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest {

    /**
     * Given 필요한 역과, 구간, 노선을 등록합니다.
     * When source 에서 sink 역으로 갈 수 있는 길을 조회합니다.
     * Then source 부터 sink 까지의 가장 빠른 길의 역들과 총 거리를 응답받습니다.
     */
    @DisplayName("가장 빠른 길을 조회합니다.")
    @Test
    void shortest() {
        // given
        long 강남역_id = StationAssuredTemplate.createStation(StationFixtures.강남역.getName()).then().extract().jsonPath().getLong("id");
        long 양재역_id = StationAssuredTemplate.createStation(StationFixtures.양재역.getName()).then().extract().jsonPath().getLong("id");
        long 논현역_id = StationAssuredTemplate.createStation(StationFixtures.논현역.getName()).then().extract().jsonPath().getLong("id");
        long 고속터미널역_id = StationAssuredTemplate.createStation(StationFixtures.고속터미널역.getName()).then().extract().jsonPath().getLong("id");
        long 교대역_id = StationAssuredTemplate.createStation(StationFixtures.교대역.getName()).then().extract().jsonPath().getLong("id");

        long 신분당선_id = LineAssuredTemplate.createLine(new LineRequest("신분당선", "green", 논현역_id, 강남역_id, 4L)).then().extract().jsonPath().getLong("id");
        SectionAssuredTemplate.addSection(신분당선_id, new SectionRequest(강남역_id, 양재역_id, 3L));

        long 삼호선_id = LineAssuredTemplate.createLine(new LineRequest("3호선", "orange", 논현역_id, 고속터미널역_id, 2L)).then().extract().jsonPath().getLong("id");
        SectionAssuredTemplate.addSection(삼호선_id, new SectionRequest(고속터미널역_id, 교대역_id, 1L));
        SectionAssuredTemplate.addSection(삼호선_id, new SectionRequest(교대역_id, 양재역_id, 3L));

        // when
        ExtractableResponse<Response> result = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParam("source", 논현역_id)
                .queryParam("target", 양재역_id)
                .get("/paths")
                .then().log().all()
                .extract();

        // then
        Assertions.assertThat(result.jsonPath().getList("stations")).hasSize(4)
                .extracting("id", "name")
                .contains(
                        Tuple.tuple((int) 논현역_id, StationFixtures.논현역.getName()),
                        Tuple.tuple((int) 고속터미널역_id, StationFixtures.고속터미널역.getName()),
                        Tuple.tuple((int) 교대역_id, StationFixtures.교대역.getName()),
                        Tuple.tuple((int) 양재역_id, StationFixtures.양재역.getName())
                );

        Assertions.assertThat(result.jsonPath().getLong("distance")).isEqualTo(6);
    }
}
