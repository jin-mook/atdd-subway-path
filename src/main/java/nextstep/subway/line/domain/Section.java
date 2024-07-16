package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.ErrorMessage;
import nextstep.subway.exception.IllegalDistanceValueException;
import nextstep.subway.exception.NotSameUpAndDownStationException;
import nextstep.subway.station.Station;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Long distance;

    public Section(Station upStation, Station downStation, Long distance) {
        if (upStation.equals(downStation)) {
            throw new NotSameUpAndDownStationException(ErrorMessage.NOT_SAME_UP_AND_DOWN_STATION);
        }
        if (distance <= 0) {
            throw new IllegalDistanceValueException(ErrorMessage.ILLEGAL_DISTANCE_VALUE);
        }

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isDownStationSameWithNewUpStation(Section section) {
        return this.downStation.equals(section.getUpStation());
    }

    public boolean containStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

//    public Station getUpStation() {
//        return upStation;
//    }

//    public Station getDownStation() {
//        return downStation;
//    }

    public void setMappingWithLine(Line line) {
        this.line = line;
    }

//    public Long getDistance() {
//        return this.distance;
//    }

    public void decreaseDistance(Long distance) {
        if (this.distance <= distance) {
            throw new IllegalDistanceValueException(ErrorMessage.LARGE_DISTANCE_THAN_CURRENT_SECTION);
        }

        this.distance -= distance;
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }
}
