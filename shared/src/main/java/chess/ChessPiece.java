package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public boolean inbounds(int startPos, int endPos) {
      return startPos > 0 && startPos <= 8 && endPos > 0 && endPos <= 8;
    }

    public boolean oneMove(int row, int column, Collection<ChessMove> collection, ChessBoard board, ChessGame.TeamColor myColor, ChessPosition startPosition) {
        if (!inbounds(row, column)) {return false;};

        ChessPiece currPiece = board.getPiece(new ChessPosition(row, column));
        if (currPiece == null){
            collection.add(new ChessMove(startPosition, new ChessPosition(row, column), null));
            return true;
        } else if (currPiece.getTeamColor() != myColor) {
            collection.add(new ChessMove(startPosition, new ChessPosition(row, column), null));
            return false;
        }else{
            return false;
        }
    };

    public boolean pawnMove(int row, int column, Collection<ChessMove> collection, ChessGame.TeamColor myColor, ChessPosition startPosition, ChessBoard board) {

        if (inbounds(row, column) && board.getPiece(new ChessPosition(row, column)) == null) {
            if (row == 8 || row == 1){
                for (PieceType pieceType : PieceType.values()){
                    if (pieceType != PieceType.KING && pieceType != PieceType.PAWN){
                    collection.add(new ChessMove(startPosition, new ChessPosition(row, column), pieceType));
                    }
                }
            }else{
            collection.add(new ChessMove(startPosition, new ChessPosition(row, column), null));}
            return true;
        }
        return false;
    };

    public boolean pawnTake(int row, int column, Collection<ChessMove> collection, ChessGame.TeamColor myColor, ChessPosition startPosition, ChessBoard board){
        if (inbounds(row, column) && board.getPiece(new ChessPosition(row, column)) != null && board.getPiece(new ChessPosition(row, column)).getTeamColor() != myColor) {
            if (row == 8 || row == 1){
                for (PieceType pieceType : PieceType.values()){
                    if (pieceType != PieceType.KING && pieceType != PieceType.PAWN){
                        collection.add(new ChessMove(startPosition, new ChessPosition(row, column), pieceType));
                    }
                }
            }else{
                collection.add(new ChessMove(startPosition, new ChessPosition(row, column), null));}
            return true;
        }
        return false;
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> collection = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        switch (type){
            case KING:
                oneMove(row+1, column, collection,board, myColor,myPosition);
                oneMove(row-1, column, collection, board, myColor, myPosition);
                oneMove(row, column+1, collection, board, myColor, myPosition);
                oneMove(row, column-1, collection, board, myColor, myPosition);
                oneMove(row+1, column+1, collection, board, myColor, myPosition);
                oneMove(row-1, column+1, collection, board, myColor, myPosition);
                oneMove(row-1, column-1, collection, board, myColor, myPosition);
                oneMove(row+1, column-1, collection, board, myColor, myPosition);
                break;
            case QUEEN:
                ChessPiece bishop = new ChessPiece(myColor, ChessPiece.PieceType.BISHOP);
                ChessPiece rook = new ChessPiece(myColor, ChessPiece.PieceType.ROOK);

                Collection<ChessMove> bishopMoves = bishop.pieceMoves(board, myPosition);

                Collection<ChessMove> rookMoves = rook.pieceMoves(board, myPosition);

                collection.addAll(bishopMoves);
                collection.addAll(rookMoves);
                break;
            case BISHOP:
                row += 1;
                column += 1;
                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection, board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    row += 1;
                    column += 1;
                }

                row = myPosition.getRow();
                column = myPosition.getColumn();
                row -=1;
                column -=1;
                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    row -= 1;
                    column -= 1;
                }
                row = myPosition.getRow();
                column = myPosition.getColumn();

                column -=1;
                row += 1;
                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    column -= 1;
                    row += 1;
                }
                column = myPosition.getColumn();
                row = myPosition.getRow();
                column +=1;
                row -= 1;
                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    column += 1;
                    row -= 1;
                }
                column = myPosition.getColumn();
                break;
            case KNIGHT:
                break;
            case ROOK:
                row += 1;
                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection, board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    row += 1;
                }

                row = myPosition.getRow();
                row -=1;
                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    row -= 1;
                }
                row = myPosition.getRow();

                column -=1;
                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    column -= 1;
                }
                column = myPosition.getColumn();
                column +=1;

                while (inbounds(row, column)) {
                    boolean flag = oneMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    column += 1;
                }
                column = myPosition.getColumn();
                break;
            case PAWN:
                if (myColor == ChessGame.TeamColor.BLACK){
                    if (row == 7) {
                        var checkMove = pawnMove(row-1 , column, collection, myColor, myPosition, board);
                        if (checkMove){
                            pawnMove(row-2 , column, collection, myColor, myPosition, board);
                        }
                    }else{
                        pawnMove(row-1 , column, collection, myColor, myPosition, board);
                    }
                    pawnTake(row-1 , column-1, collection, myColor, myPosition, board);
                    pawnTake(row-1 , column+1, collection, myColor, myPosition, board);
                }
                else{
                    {
                        if (row == 2) {
                            var checkMove = pawnMove(row+1 , column, collection, myColor, myPosition, board);
                            if (checkMove){
                                pawnMove(row+2 , column, collection, myColor, myPosition, board);
                            }
                        }else{
                            pawnMove(row+1 , column, collection, myColor, myPosition, board);
                        }
                        pawnTake(row+1 , column-1, collection, myColor, myPosition, board);
                        pawnTake(row+1 , column+1, collection, myColor, myPosition, board);
                    }
                }
                break;
    }
    return collection;
}
}
