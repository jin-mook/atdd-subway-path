package nextstep.subway.line;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<LineStationsResponse> stations;

    public LineResponse(Line line, List<LineStationsResponse> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        List<LineStationsResponse> stationList = line.mapSectionStations(LineStationsResponse::new);

        return new LineResponse(line, stationList);
    }
}
