package nextstep.subway.line;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/lines")
@RestController
@RequiredArgsConstructor
public class LineController {

    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Validated @RequestBody LineRequest lineRequest) {
        LineResponse data = lineService.saveLine(lineRequest);

        return SuccessResponse.created(data, () -> "/lines/" + data.getId());
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> data = lineService.findLines();
        return SuccessResponse.ok(data);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        LineResponse data = lineService.findLineResponse(lineId);
        return SuccessResponse.ok(data);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable Long lineId,
                                           @Validated @RequestBody UpdateLineRequest updateLineRequest) {
        lineService.updateLine(lineId, updateLineRequest);
        return SuccessResponse.ok();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLine(lineId);
        return SuccessResponse.noContent();
    }
}
