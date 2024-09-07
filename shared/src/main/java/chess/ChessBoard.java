package chess;
import java.util.ArrayList;
import java.util.List;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    List <List<ChessPiece>> currentBoard = new ArrayList<>(8);

    public ChessBoard() {
        this.resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        currentBoard.get(position.getRow()).set(position.getColumn(), piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return currentBoard.get(position.getRow()).get(position.getColumn());
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        currentBoard.replaceAll(ignored -> (new ArrayList<>(8)));
        for (int i = 0; i < 8; i++){
            if (i == 0){
                List<ChessPiece> row = currentBoard.getFirst();
//              for (int j = 0; j < 8; j++){
//                  row.set();
//
//              }
            }
        }
    }
}
