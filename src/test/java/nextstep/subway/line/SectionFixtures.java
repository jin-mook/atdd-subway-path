package nextstep.subway.line;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.StationFixtures;

public class SectionFixtures {

    public static final Section FIRST_SECTION = new Section(StationFixtures.FIRST_UP_STATION, StationFixtures.FIRST_DOWN_STATION, 10L);
    public static final Section ADD_FIRST_SECTION = new Section(StationFixtures.FIRST_DOWN_STATION, StationFixtures.SECOND_UP_STATION, 20L);
    public static final Section SECOND_SECTION = new Section(StationFixtures.SECOND_UP_STATION, StationFixtures.SECOND_DOWN_STATION, 30L);
}
