package nextstep.subway.section;

import nextstep.subway.station.StationFixtures;

public class SectionFixtures {

    public static final Section FIRST_SECTION = new Section(StationFixtures.UP_STATION, StationFixtures.DOWN_STATION, 10L);
    public static final Section SECOND_SECTION = new Section(StationFixtures.NEW_UP_STATION, StationFixtures.NEW_DOWN_STATION, 20L);
}
