package nextstep.subway.line.domain;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.AlreadyHasUpAndDownStationException;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            newSection.setFirstSectionOrder();
            sections.add(newSection);
            return;
        }

        Optional<Section> upSection = findExistUpStationSection(newSection);
        Optional<Section> downSection = findExistDownStationSection(newSection);

        if (upSection.isEmpty() && downSection.isEmpty()) {
            throw new NoStationException(ErrorMessage.CANNOT_ADD_STATION);
        }

        if (upSection.isPresent() && downSection.isPresent()) {
            throw new AlreadyHasUpAndDownStationException(ErrorMessage.CANNOT_ADD_STATION);
        }

        if (upSection.isPresent()) {
            Section section = upSection.get();
            if (isAddToUpStation(newSection, section)) {
                newSection.setOrderFrontSection(section);

                section.decreaseDistance(newSection.getDistance());
                section.changeUpStation(newSection.getDownStation());
            } else {
                Section lastSection = getLastSection();
                newSection.setOrderBehindSection(lastSection);
            }
            addNewSection(newSection);
        }

        if (downSection.isPresent()) {
            Section section = downSection.get();
            if (isAddToDownStation(newSection, section)) {
                newSection.setOrderBehindSection(section);

                section.decreaseDistance(newSection.getDistance());
                section.changeDownStation(newSection.getUpStation());
            } else {
                Section firstSection = getFirstSection();
                newSection.setOrderFrontSection(firstSection);
            }
            addNewSection(newSection);
        }
    }

    private Optional<Section> findExistUpStationSection(Section section) {
        return sections.stream()
                .filter(existSection -> existSection.containStation(section.getUpStation()))
                .reduce((first, second) -> second);
    }

    private Optional<Section> findExistDownStationSection(Section section) {
        return sections.stream()
                .filter(existSection -> existSection.containStation(section.getDownStation()))
                .reduce((first, second) -> first);
    }

    private void addNewSection(Section newSection) {
        sections.forEach(currentSection -> currentSection.addOneOrder(newSection));
        sections.add(newSection);
        sections.sort(Comparator.comparingInt(Section::getLineOrder));
    }

    private boolean isAddToUpStation(Section newSection, Section section) {
        return section.getUpStation().equals(newSection.getUpStation());
    }

    private boolean isAddToDownStation(Section newSection, Section section) {
        return section.getDownStation().equals(newSection.getDownStation());
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

    private Section getFirstSection() {
        return sections.get(0);
    }
}
