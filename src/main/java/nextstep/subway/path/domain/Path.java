package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;

import java.util.Collections;
import java.util.List;

public class Path {

    private final List<Section> sections;

    public Path(List<Section> sections) {
        this.sections = sections;
    }

    public long calculateDistance() {
        return sections.stream()
                .mapToLong(Section::getDistance)
                .sum();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(this.sections);
    }
}
