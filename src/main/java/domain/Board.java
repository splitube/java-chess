package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import view.PieceView;

public class Board {

    private static final String IMPOSSIBLE_MOVE_ERROR_MESSAGE = "가 이동할 수 없는 위치입니다.";
    private static final String WHITE_TURN_ERROR_MESSAGE = "흰 진영 차례입니다.";
    private static final String BLACK_TURN_ERROR_MESSAGE = "검은 진영 차례입니다.";
    private final PathValidator pathValidator;
    private List<Line> lines;

    public Board(PathValidator pathValidator) {
        this.pathValidator = pathValidator;
    }

    public void initialize() {
        final List<Line> lines = new ArrayList<>();
        lines.add(Line.whiteBack());
        lines.add(Line.whiteFront());
        IntStream.range(0, 4).mapToObj(count -> Line.empty()).forEach(lines::add);
        lines.add(Line.blackFront());
        lines.add(Line.blackBack());
        this.lines = lines;
    }

    public void moveWhite(final Location start, final Location end) {
        final Square square = findSquare(start);
        if (square.isBlack()) {
            throw new IllegalArgumentException(WHITE_TURN_ERROR_MESSAGE);
        }
        move(start, end);
    }

    public void moveBlack(final Location start, final Location end) {
        final Square square = findSquare(start);
        if (square.isWhite()) {
            throw new IllegalArgumentException(BLACK_TURN_ERROR_MESSAGE);
        }
        move(start, end);
    }

    private void move(final Location start, final Location end) {
        validatePath(start, end);
        findSquare(start).moveTo(findSquare(end));
    }

    private void validatePath(final Location startLocation, final Location endLocation) {
        final Square startSquare = findSquare(startLocation);
        final Square endSquare = findSquare(endLocation);
        final SpecialValidateDto start = SpecialValidateDto.of(startLocation, startSquare);
        final SpecialValidateDto end = SpecialValidateDto.of(endLocation, endSquare);
        final List<Square> squares = getSquaresInPath(startLocation, endLocation);
        if (isSpecialPath(start, end)) {
            return;
        }
        //폰일때 아무도 없는 곳으로 대각선을 가는 경우에 대한 처리가 필요 ( 방향을 알아야 함 )
        if (isNormalPath(startSquare, squares)) {
            return;
        }
        throw new IllegalArgumentException(PieceView.findSign(start.getPiece()) + IMPOSSIBLE_MOVE_ERROR_MESSAGE);
    }

    private boolean isNormalPath(final Square startSquare, final List<Square> squares) {
        return pathValidator.validateNormal(startSquare, squares);
    }

    private boolean isSpecialPath(final SpecialValidateDto start, final SpecialValidateDto end) {
        return pathValidator.validateSpecial(start, end);
    }

    private List<Square> getSquaresInPath(final Location start, final Location end) {
        final Square square = findSquare(start);
        final List<Location> paths = square.searchPath(start, end);
        return paths.stream().map(this::findSquare).collect(Collectors.toList());
    }

    public Square findSquare(final Location location) {
        return lines.get(location.getRow()).getByCol(location.getCol());
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
