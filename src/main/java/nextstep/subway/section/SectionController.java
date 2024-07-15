package nextstep.subway.section;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.SuccessResponse;
import nextstep.subway.line.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/lines")
@RestController
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId,
                                                   @Validated @RequestBody SectionRequest sectionRequest) {
        LineResponse data = sectionService.addSection(lineId, sectionRequest);
        return SuccessResponse.created(data);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId,
                                              @RequestParam("stationId") Long stationId) {

        sectionService.deleteSection(lineId, stationId);
        return SuccessResponse.noContent();
    }

}
