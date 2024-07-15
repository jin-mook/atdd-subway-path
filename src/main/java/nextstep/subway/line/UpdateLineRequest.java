package nextstep.subway.line;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateLineRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    public UpdateLineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
