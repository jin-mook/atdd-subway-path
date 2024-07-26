package nextstep.subway.path.domain;

public interface PathRepository {

    Path findShortestPath(Long sourceStationId, Long targetStationId);
}
