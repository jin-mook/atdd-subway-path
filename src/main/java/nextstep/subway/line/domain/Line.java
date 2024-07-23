package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;

        this.addSection(section);
    }

    public void addSection(Section section) {
        sections.addSection(section);
        section.setMappingWithLine(this);
    }

    public Section findDeleteTargetSection(Long stationId) {
        return sections.getDeleteTargetSection(stationId);
    }

    public void deleteSection(Section section) {
        sections.deleteSection(section);
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateColor(String newColor) {
        this.color = newColor;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }
}
