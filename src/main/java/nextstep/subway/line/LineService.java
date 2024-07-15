package nextstep.subway.line;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.NoLineExistException;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LineService {

    private final LineRepository lineRepository;

    private final StationService stationService;

    public LineResponse saveLine(LineRequest lineRequest) {

        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());

        Section section = new Section(upStation, downStation, lineRequest.getDistance());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), section);
        Line savedLine = lineRepository.save(line);

        return LineResponse.from(savedLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAllWithSectionsAndStations();

        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineResponse(Long lineId) {
        Line line = findLineByIdWithSectionsAndStations(lineId);

        return LineResponse.from(line);
    }

    @Transactional(readOnly = true)
    public Line findLine(Long lineId) {
        return findLineByIdWithSectionsAndStations(lineId);
    }

    public void updateLine(Long lineId, UpdateLineRequest updateLineRequest) {
        Line line = findLineById(lineId);
        line.updateName(updateLineRequest.getName());
        line.updateColor(updateLineRequest.getColor());
    }

    public void deleteLine(Long lineId) {
        Line line = findLineByIdWithSectionsAndStations(lineId);
        lineRepository.delete(line);
    }

    private Line findLineByIdWithSectionsAndStations(Long lineId) {
        return lineRepository.findByIdWithSectionsAndStations(lineId)
                .orElseThrow(() -> new NoLineExistException(ErrorMessage.NO_LINE_EXIST));
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new NoLineExistException(ErrorMessage.NO_LINE_EXIST));
    }
}
