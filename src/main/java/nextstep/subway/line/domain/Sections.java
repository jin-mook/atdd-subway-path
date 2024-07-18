package nextstep.subway.line.domain;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.AlreadyHasUpAndDownStationException;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NoStationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        // 새로 추가하려는 구간의 역이 기존 노선에 존재하지 않는 경우
        if (upSections.isEmpty() && downSections.isEmpty()) {
            throw new NoStationException(ErrorMessage.CANNOT_ADD_STATION);
        }

        // 새로 추가하려는 구간의 상행역과 하행역이 기존 노선에 모두 존재하는 경우
        if (!upSections.isEmpty() && !downSections.isEmpty()) {
            throw new AlreadyHasUpAndDownStationException(ErrorMessage.CANNOT_ADD_STATION);
        }

        // 상행역을 기준으로 추가하는 경우
        if (!upSections.isEmpty()) {
            for (int i = 0; i < sections.size(); i++) {
                Section section = sections.get(i);
                if (isAddToUpStation(newSection, section)) {
                    // 로직
                    sections.add(i, newSection);
                    section.decreaseDistance(newSection.getDistance());
                    section.changeUpStation(newSection.getDownStation());
                    return;
                }
            }

            sections.add(newSection);
        }

        // 하행역을 기준으로 추가하는 경우
        if (!downSections.isEmpty()) {
            for (int i = 0; i < sections.size(); i++) {
                Section section = sections.get(i);
                if (isAddToDownStation(newSection, section)) {
                    sections.add(i+1, newSection);
                    section.decreaseDistance(newSection.getDistance());
                    section.changeDownStation(newSection.getUpStation());
                    return;
                }
            }
            sections.add(0, newSection);
        }
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
}
