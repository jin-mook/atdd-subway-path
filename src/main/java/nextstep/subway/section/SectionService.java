package nextstep.subway.section;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SectionService {

    private final StationService stationService;
    private final LineService lineService;

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.findLine(lineId);

        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        Section section = new Section(upStation, downStation, sectionRequest.getDistance());
        line.addSection(section);

        return LineResponse.from(line);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineService.findLine(lineId);

        Section targetSection = line.findDeleteTargetSection(stationId);
        line.deleteSection(targetSection);
    }
}
