package nextstep.subway.line;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.StationFixtures;

public class SectionFixtures {

    public static final Section FIRST_SECTION = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
    public static final Section ADD_FIRST_SECTION = new Section(StationFixtures.DOWN_STATION, StationFixtures.NEW_UP_STATION, 20L);
    public static final Section SECOND_SECTION = new Section(StationFixtures.NEW_UP_STATION, StationFixtures.NEW_DOWN_STATION, 30L);
}
