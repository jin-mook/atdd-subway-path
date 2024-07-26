package nextstep.subway.path.domain;

import nextstep.subway.line.SectionFixtures;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class PathTest {

    @DisplayName("총 경로 거리를 계산합니다.")
    @Test
    void calculateDistance() {
        // given
        Path path = new Path(List.of(SectionFixtures.논현_고속, SectionFixtures.고속_교대, SectionFixtures.교대_양재));
        // when
        long distance = path.calculateDistance();
        // then
        Assertions.assertThat(distance).isEqualTo(6);
    }

}