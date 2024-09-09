package chess;

import javax.swing.*;
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


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> collection = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        switch (type){
            case KING:

                break;
            case QUEEN:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case ROOK:
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
                        } if (board.getPiece(new ChessPosition(row-1, column -1)) != null) {
                            for (PieceType kind : PieceType.values()) {
                                if (kind != PieceType.KING && kind != PieceType.PAWN){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row-1, column-1 ), kind ));
                                }
                            }
                        } if (board.getPiece(new ChessPosition(row-1, column +1)) != null) {
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
                        if (board.getPiece(new ChessPosition(row+1, column)) == null){
                            collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column ), null ));
                            if (board.getPiece(new ChessPosition(row+2, column)) == null){
                                collection.add(new ChessMove(myPosition,new ChessPosition(row+2, column ), null ));
                            }
                        } if (board.getPiece(new ChessPosition(row+1, column -1)) != null) {
                            if (board.getPiece(new ChessPosition(row+1, column -1)).pieceColor != ChessGame.TeamColor.WHITE){
                                collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column -1), null ));
                            }
                        } if (board.getPiece(new ChessPosition(row+1, column +1)) != null) {
                            if (board.getPiece(new ChessPosition(row+1, column -1)).pieceColor != ChessGame.TeamColor.WHITE){
                                collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column -1), null ));
                            }

                        }
                    } else if (row == 7) {
                        if (board.getPiece(new ChessPosition(row+1, column)) == null){

                            for (PieceType kind : PieceType.values()) {
                                if (kind != PieceType.KING && kind != PieceType.PAWN){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column ), kind ));
                                }
                            }
                        } if (board.getPiece(new ChessPosition(row+1, column -1)) != null && board.getPiece(new ChessPosition(row+1, column -1)).getTeamColor() != ChessGame.TeamColor.WHITE) {
                            for (PieceType kind : PieceType.values()) {
                                if (kind != PieceType.KING && kind != PieceType.PAWN){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column-1 ), kind ));
                                }
                            }
                        } if (board.getPiece(new ChessPosition(row+1, column +1)) != null) {
                            if (inbounds(row+1,column+1)){
                            if (board.getPiece(new ChessPosition(row+1, column +1)).pieceColor != ChessGame.TeamColor.WHITE){
                                for (PieceType kind : PieceType.values()) {
                                    if (kind != PieceType.KING && kind != PieceType.PAWN){
                                        collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column+1 ), kind ));
                                    }
                                }                            }
                            }
                        }
                    }else {
                        if (inbounds(row+1,column)){
                        if (board.getPiece(new ChessPosition(row+1, column)) == null) {
                            collection.add(new ChessMove(myPosition, new ChessPosition(row + 1, column), null));
                        }
                        if (board.getPiece(new ChessPosition(row+1, column -1)) != null) {
                            if (inbounds(row+1,column-1)){
                                if (board.getPiece(new ChessPosition(row+1, column -1)).pieceColor != ChessGame.TeamColor.WHITE){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column -1), null ));
                                }}

                            } if (board.getPiece(new ChessPosition(row+1, column +1)) != null) {
                            if (inbounds(row+1, column+1)){
                                if (board.getPiece(new ChessPosition(row+1, column +1)).pieceColor != ChessGame.TeamColor.WHITE){
                                    collection.add(new ChessMove(myPosition,new ChessPosition(row+1, column +1), null ));
                                }
                            }
                    }
                }
            }

                    break;
        }


    }
    return collection;
}
}
