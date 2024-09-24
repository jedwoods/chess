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
        currentBoard.get(8-position.getRow()).set(position.getColumn()-1, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        var tempRow = currentBoard.get(8-position.getRow());
        var piece = tempRow.get(position.getColumn()-1);
        return piece;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */

    public List<ChessPiece> getRow(ChessGame.TeamColor color, ChessPiece.PieceType piece){
        List<ChessPiece> currentRow = new ArrayList<>(8);
        if (piece == ChessPiece.PieceType.PAWN){
            while (currentRow.size() < 8){
                currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.PAWN));
            }
        }else{
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.ROOK));
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.QUEEN));
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.KING));
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            currentRow.addLast(new ChessPiece(color, ChessPiece.PieceType.ROOK));
        }
        return currentRow;
    }

    public List<List<ChessPiece>> emptyBoard(){
        List<List<ChessPiece>> newBoard = new ArrayList<>(8);
        for (int i = 0; i < 8; i++){
            List<ChessPiece> currentRow = new ArrayList<>(8);
            for (int j = 0; j< 8; j++) {
                currentRow.addLast( null);
            }
            newBoard.addLast(currentRow);
        }
        return newBoard;
    }

    public void resetBoard() {
        currentBoard = emptyBoard();
        for (int i = 0; i < 8; i++){
            List<ChessPiece> currentRow = currentBoard.get(i);
            if (i == 0){
                currentBoard.set(i, getRow(ChessGame.TeamColor.BLACK, null));
            }else if (i ==1){
                currentBoard.set(i, getRow(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            }else if (i ==6){
                currentBoard.set(i, getRow(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

            }else if (i == 7){
                currentBoard.set(i, getRow(ChessGame.TeamColor.WHITE, null));
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

    @Override
    public String toString() {
        return "ChessBoard{" +
                "currentBoard=" + currentBoard +
                '}';
    }
}