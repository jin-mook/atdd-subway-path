package nextstep.subway.line.domain;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.*;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        List<Section> upSections = sections.stream().filter(section -> section.containStation(newSection.getUpStation()))
                .collect(Collectors.toList());

        List<Section> downSections = sections.stream().filter(section -> section.containStation(newSection.getDownStation()))
                .collect(Collectors.toList());

        if (upSections.isEmpty() && downSections.isEmpty()) {
            throw new NoStationException(ErrorMessage.CANNOT_ADD_STATION);
        }

        if (!upSections.isEmpty() && !downSections.isEmpty()) {
            throw new AlreadyHasUpAndDownStationException(ErrorMessage.CANNOT_ADD_STATION);
        }

        if (!upSections.isEmpty()) {
            for (int i = 0; i < sections.size(); i++) {
                Section section = sections.get(i);
                if (section.getUpStation().equals(newSection.getUpStation())) {
                    // 로직
                    sections.add(i, newSection);
                    section.decreaseDistance(newSection.getDistance());
                    section.changeUpStation(newSection.getDownStation());
                    return;
                }
            }

            sections.add(newSection);
        }

        if (!downSections.isEmpty()) {
            for (int i = 0; i < sections.size(); i++) {
                Section section = sections.get(i);
                if (section.getDownStation().equals(newSection.getDownStation())) {
                    sections.add(i+1, newSection);
                    section.decreaseDistance(newSection.getDistance());
                    section.changeDownStation(newSection.getUpStation());
                    return;
                }
            }
            sections.add(0, newSection);
        }
    }

    private boolean canConnectedWithNewSection(Section newSection) {
        Section lastSection = getLastSection();

        return lastSection.isDownStationSameWithNewUpStation(newSection)
                && !alreadyHasNewDownStation(newSection);
    }

    private boolean alreadyHasNewDownStation(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.containStation(newSection.getDownStation()));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Section getDeleteTargetSection(Long stationId) {
        if (sections.size() <= 1) {
            throw new CannotDeleteSectionException(ErrorMessage.CANNOT_DELETE_SECTION);
        }

        if (!isLastStation(stationId)) {
            throw new CannotDeleteSectionException(ErrorMessage.CANNOT_DELETE_SECTION);
        }

        return getLastSection();
    }

    public void deleteSection(Section section) {
        sections.remove(section);
    }

    private boolean isLastStation(Long stationId) {
        return getLastSection().getDownStation().getId().equals(stationId);
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }
}
