package nextstep.subway.path.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathRepository;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PathService {

    private final PathRepository pathRepository;

    public PathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
        Path path = pathRepository.findShortestPath(sourceStationId, targetStationId);

        long distance = path.calculateDistance();
        List<Section> sections = path.getSections();

        return PathResponse.from(sections, distance);
    }
}
