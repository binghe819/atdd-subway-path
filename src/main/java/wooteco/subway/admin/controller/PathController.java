package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.PathSearchType;
import wooteco.subway.admin.dto.PathResponse;
import wooteco.subway.admin.dto.error.ErrorResponse;
import wooteco.subway.admin.exception.LineNotConnectedException;
import wooteco.subway.admin.exception.OverlappedStationException;
import wooteco.subway.admin.service.PathService;

import javax.validation.constraints.NotBlank;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@Validated
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findPath(@RequestParam @NotBlank(message = "시작역이 없습니다!") String source,
                                                 @RequestParam @NotBlank(message = "끝역이 없습니다!") String target,
                                                 @RequestParam @NotBlank(message = "검색 기준을 선택해주세요") String type) {
        return ResponseEntity.ok()
                .body(pathService.calculatePath(source, target, PathSearchType.of(type)));
    }

    @ExceptionHandler({OverlappedStationException.class, LineNotConnectedException.class})
    public ResponseEntity<ErrorResponse> overlappedStationExceptionHandler(Exception exception) {
        final ErrorResponse response = ErrorResponse.of(BAD_REQUEST.value(), exception.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(response);
    }
}
