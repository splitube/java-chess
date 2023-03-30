package controller;

import domain.ChessGame;
import java.util.Arrays;
import java.util.List;
import view.GameCommand;
import view.InputView;
import view.OutputView;

public final class ChessController {

    private static final int MAIN_COMMAND_INDEX = 0;
    private static final int GAME_COMMAND_INDEX = 0;
    private static final String COMMANDS_DELIMITER = " ";
    private final ChessGame chessGame;
    private final OutputView outputView = new OutputView();
    private final InputView inputView = new InputView();

    public ChessController(final ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public void ready() {
        execute(this::getGameStartCommand);
    }

    private void execute(final Runnable runningFunction) {
        try {
            runningFunction.run();
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
            execute(runningFunction);
        }
    }

    private void getGameStartCommand() {
        final GameCommand command = GameCommand.find(inputView.getStartCommand().strip());
        if (command == GameCommand.MOVE || command == GameCommand.STATUS) {
            throw new IllegalArgumentException("start, end 중에 입력해주세요.");
        }
        if (command == GameCommand.START) {
            chessGame.ready();
            chessGame.makeBoard();
            printBoard();
        }
    }

    public void play() {
        while (chessGame.isReady()) {
            execute(() -> reactCommand(getPlayCommands()));
        }
        outputView.printEndMessage();
    }

    private List<String> getPlayCommands() {
        final List<String> inputCommands = Arrays.asList(inputView.getGameCommand().split(COMMANDS_DELIMITER, -1));
        if (GameCommand.notExist(inputCommands.get(MAIN_COMMAND_INDEX))) {
            throw new IllegalArgumentException("잘못된 커맨드 입력입니다.");
        }
        return inputCommands;
    }

    private void reactCommand(final List<String> commandInput) {
        final GameCommand gameCommand = GameCommand.find(commandInput.get(GAME_COMMAND_INDEX));
        if (gameCommand == GameCommand.END || gameCommand == GameCommand.START) {
            reactControlCommand(gameCommand);
            return;
        }
        reactPlayCommand(commandInput, gameCommand);
    }

    private void reactControlCommand(final GameCommand gameCommand) {
        if (gameCommand == GameCommand.END) {
            chessGame.end();
            return;
        }
        chessGame.makeBoard();
        printBoard();
    }

    private void reactPlayCommand(final List<String> commandInputs, final GameCommand gameCommand) {
        if (gameCommand == GameCommand.STATUS) {
            printScore();
            return;
        }
        chessGame.move(MoveCommand.of(commandInputs));
        printBoard();
    }

    public void printBoard() {
        outputView.printBoard(chessGame.getBoard());
    }

    public void printScore() {
        outputView.printScores(chessGame.getBlackScore(), chessGame.getWhiteScore());
    }
}
