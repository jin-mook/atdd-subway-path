package nextstep.subway.path.infrastructure;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.exception.NotConnectedStationException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JgraphtPathRepository implements PathRepository {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Override
    public Path findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new NoStationException(ErrorMessage.NO_STATION_EXIST));

        Station targetStation = stationRepository.findById(targetStationId)
                .orElseThrow(() -> new NoStationException(ErrorMessage.NO_STATION_EXIST));

        List<Section> sections = lineRepository.findAllWithSectionsAndStations()
                .stream().flatMap(line -> line.getSections().stream())
                .distinct()
                .collect(Collectors.toList());

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = initializeDijkstra(sections);

        try {
            List<Station> shortestStations = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
            return new Path(findTargetSections(sections, shortestStations));
        } catch (NullPointerException e) {
            throw new NotConnectedStationException(ErrorMessage.NOT_CONNECTED_STATION);
        }

    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> initializeDijkstra(List<Section> allSection) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allSection.forEach(section -> addSectionToGraph(graph, section));
        return new DijkstraShortestPath<>(graph);
    }

    private void addSectionToGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        graph.addVertex(section.getUpStation());
        graph.addVertex(section.getDownStation());
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    private List<Section> findTargetSections(List<Section> sections, List<Station> shortestStation) {
        List<Section> shortestSection = new ArrayList<>();

        for (int i = 0; i < shortestStation.size() - 1; i++) {
            Station upStation = shortestStation.get(i);
            Station downStation = shortestStation.get(i + 1);

            Section targetSection = sections.stream().filter(section -> section.isRightSection(upStation, downStation))
                    .findFirst().get();

            shortestSection.add(targetSection);
        }

        return shortestSection;
    }
}
