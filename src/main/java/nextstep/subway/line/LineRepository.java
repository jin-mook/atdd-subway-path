package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l join fetch l.sections.sections s " +
            "join fetch s.upStation " +
            "join fetch s.downStation " +
            "order by l.id, s.id")
    List<Line> findAllWithSectionsAndStations();

    @Query("select l from Line l join fetch l.sections.sections s " +
            "join fetch s.upStation " +
            "join fetch s.downStation " +
            "where l.id = :id order by l.id, s.id")
    Optional<Line> findByIdWithSectionsAndStations(@Param("id") Long id);
}
