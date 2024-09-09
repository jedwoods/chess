package chess;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    List <List<ChessPiece>> currentBoard = new ArrayList<>(8);

    public ChessBoard() {
        for (int i = 0; i < 8; i++){
            List<ChessPiece> newList = new ArrayList<ChessPiece>(8);
            for (int j = 0; j < 8; j++) {
                newList.add(null);  // Fill with null values
            }
            currentBoard.add(newList);
        }

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        currentBoard.get(position.getRow()-1).set(position.getColumn()-1, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return currentBoard.get(position.getRow()-1).get(position.getColumn()-1);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        currentBoard = new ArrayList<>(8);
        for (int i = 0; i < 8; i++){
            List<ChessPiece> newList = new ArrayList<ChessPiece>(8);
            for (int j = 0; j < 8; j++) {
                newList.add(null);  // Fill with null values
            }
            currentBoard.add(newList);
        }
        for (int i = 0; i < 8; i++){
            if (i == 7){
                List<ChessPiece> row = currentBoard.get(i);
                row.set(0, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                row.set(1, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                row.set(2, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                row.set(3, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
                row.set(4, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
                row.set(5, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                row.set(6, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                row.set(7, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
              }
            else if (i == 6){
                List<ChessPiece> row = currentBoard.get(i);
                row.replaceAll(ignore -> new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));

            }
            else if (i == 1){
                List<ChessPiece> row = currentBoard.get(i);
                row.replaceAll(ignore -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            }
            else if (i == 0){
                List<ChessPiece> row = currentBoard.get(i);
                row.set(0, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                row.set(1, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                row.set(2, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                row.set(3, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                row.set(4, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                row.set(5, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                row.set(6, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                row.set(7, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;
        return Objects.equals(currentBoard, that.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currentBoard);
    }
}