package chess;

import javax.swing.*;
import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public boolean rookMove(int row, int column, Collection<ChessMove> collection, ChessBoard board, ChessGame.TeamColor myColor, ChessPosition startPosition) {
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

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> collection = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        switch (type){
            case KING:
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
                    boolean flag = rookMove(row, column, collection, board, myColor,myPosition);
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
                    boolean flag = rookMove(row, column, collection,board, myColor,myPosition);
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
                    boolean flag = rookMove(row, column, collection,board, myColor,myPosition);
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
                    boolean flag = rookMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    column += 1;
                    row -= 1;
                }
                column = myPosition.getColumn();
                break;
            case KNIGHT:
                oneMove(row+2, column+1, collection,board, myColor,myPosition);
                oneMove(row+2, column-1, collection, board, myColor, myPosition);
                oneMove(row-2, column+1, collection, board, myColor, myPosition);
                oneMove(row-2, column-1, collection, board, myColor, myPosition);
                oneMove(row+1, column+2, collection, board, myColor, myPosition);
                oneMove(row+1, column-2, collection, board, myColor, myPosition);
                oneMove(row-1, column+2, collection, board, myColor, myPosition);
                oneMove(row-1, column-2, collection, board, myColor, myPosition);
                break;
            case ROOK:
                row += 1;
                while (inbounds(row, column)) {
                    boolean flag = rookMove(row, column, collection, board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    row += 1;
                }

                row = myPosition.getRow();
                row -=1;
                while (inbounds(row, column)) {
                    boolean flag = rookMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    row -= 1;
                }
                row = myPosition.getRow();

                column -=1;
                while (inbounds(row, column)) {
                    boolean flag = rookMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    column -= 1;
                }
                column = myPosition.getColumn();
                column +=1;

                while (inbounds(row, column)) {
                    boolean flag = rookMove(row, column, collection,board, myColor,myPosition);
                    if (!flag){
                        break;
                    }
                    column += 1;
                }
                column = myPosition.getColumn();
                break;
            case PAWN:
                if( pieceColor == ChessGame.TeamColor.BLACK){

                    if (row == 7){
                        if (board.getPiece(new ChessPosition(row-1, column)) == null){
                            collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column ), null ));
                            if (board.getPiece(new ChessPosition(row-2, column)) == null){
                                collection.add(new ChessMove(myPosition,new ChessPosition(row-2, column ), null ));
                            }
                        } else if (board.getPiece(new ChessPosition(row-1, column -1)) != null) {
                            if (board.getPiece(new ChessPosition(row-1, column -1)).pieceColor != ChessGame.TeamColor.BLACK){
                                collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column -1), null ));
                            }
                        } else if (board.getPiece(new ChessPosition(row-1, column +1)) != null) {
                            if (board.getPiece(new ChessPosition(row-1, column -1)).pieceColor != ChessGame.TeamColor.BLACK){
                                collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column -1), null ));
                            }

                        }
                    } else if (row == 2) {
                        if (board.getPiece(new ChessPosition(row-1, column)) == null){
                            for (PieceType kind : PieceType.values()) {
                                if (kind != PieceType.KING && kind != PieceType.PAWN){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column ), kind ));
                                }
                            }
                        } ChessPiece test = board.getPiece(new ChessPosition(row-1, column-1));
                        if (board.getPiece(new ChessPosition(row-1, column -1)) != null && board.getPiece(new ChessPosition(row-1, column -1)).pieceColor != ChessGame.TeamColor.BLACK) {
                            for (PieceType kind : PieceType.values()) {
                                if (kind != PieceType.KING && kind != PieceType.PAWN){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column-1 ), kind ));
                                }
                            }
                        } if (board.getPiece(new ChessPosition(row-1, column +1)) != null && board.getPiece(new ChessPosition(row-1, column +1)).pieceColor != ChessGame.TeamColor.BLACK) {
                            for (PieceType kind : PieceType.values()) {
                                if (kind != PieceType.KING && kind != PieceType.PAWN){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column+1 ), kind ));
                                }
                            }
                        }
                    }else{
                        if (board.getPiece(new ChessPosition(row-1, column)) == null){
                            if (inbounds(row-1,column)) {
                                collection.add(new ChessMove(myPosition, new ChessPosition(row - 1, column), null));
                            }}
                        if (board.getPiece(new ChessPosition(row-1, column -1)) != null) {
                                if (inbounds(row-1,column-1)){
                                    if (board.getPiece(new ChessPosition(row-1, column -1)).pieceColor != ChessGame.TeamColor.BLACK){
                                        collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column -1), null ));
                                    }}

                            } if (board.getPiece(new ChessPosition(row-1, column +1)) != null) {
                                if (inbounds(row-1, column+1)){
                                    if (board.getPiece(new ChessPosition(row-1, column +1)).pieceColor != ChessGame.TeamColor.BLACK){
                                        collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column +1), null ));
                                    }
                                }
                        }



                }}
                else{
                    if (row == 2){
                        oneMove()

                    } else if (row == 7) {

                    }else {

            }

                    break;
        }


    }
    return collection;
}
}
