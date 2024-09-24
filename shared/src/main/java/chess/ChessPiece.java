package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
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


    public boolean oneMove(int row, int col, ChessPosition currentPosition, ChessBoard board, Collection<ChessMove> collection){
        if( ! inbounds(row, col)){return false;}

        ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col));
        ChessPiece myPiece =board.getPiece(currentPosition);
        if (currentPiece == null){
            collection.add(new ChessMove(currentPosition, new ChessPosition(row, col), null));
            return true;
        }
        if (currentPiece.getTeamColor() == myPiece.getTeamColor()){
            return false;
        }else if (currentPiece.getTeamColor() != myPiece.getTeamColor()){
            collection.add(new ChessMove(currentPosition, new ChessPosition(row, col), null));
            return false;
        }
        return false;
    }



    public void promotion(int row, int col,ChessPosition startPosition, Collection<ChessMove> collection){
        collection.add(new ChessMove(startPosition, new ChessPosition(row,col), PieceType.QUEEN));
        collection.add(new ChessMove(startPosition, new ChessPosition(row,col), PieceType.ROOK));
        collection.add(new ChessMove(startPosition, new ChessPosition(row,col), PieceType.BISHOP));
        collection.add(new ChessMove(startPosition, new ChessPosition(row,col), PieceType.KNIGHT));

    }


    public boolean pawnMove(ChessPosition currentPosition, ChessBoard board, Collection<ChessMove> collection){
        int row = currentPosition.getRow();
        int col = currentPosition.getColumn();
        int move = -1;
        var currpiece = board.getPiece(currentPosition);
        var color = currpiece.getTeamColor();
        if (color == ChessGame.TeamColor.WHITE){
            move = 1;
            if(inbounds(row + move, col) && board.getPiece(new ChessPosition(row + move, col)) == null){
                if (row == 2){
                    collection.add(new ChessMove(currentPosition, new ChessPosition(row + move , col), null));
                    if (board.getPiece(new ChessPosition(row +2, col)) == null){
                        collection.add(new ChessMove(currentPosition, new ChessPosition(row +2 , col), null));

                    }
                }
                else if (row + move == 8){
                    promotion(8, col, currentPosition, collection);
                }else{
                    collection.add(new ChessMove(currentPosition, new ChessPosition(row + move , col), null));
                }
            }
        }else {
            if(inbounds(row + move, col) && board.getPiece(new ChessPosition(row + move, col)) == null){
                if (row == 7){
                    collection.add(new ChessMove(currentPosition, new ChessPosition(row + move , col), null));

                    if (board.getPiece(new ChessPosition(row - 2, col)) == null){
                        collection.add(new ChessMove(currentPosition, new ChessPosition(row - 2, col), null));
                    }
                }
                else if (row + move == 1){
                    promotion(1, col, currentPosition, collection);
                }else{
                    collection.add(new ChessMove(currentPosition, new ChessPosition(row + move , col), null));
                }

            }
        }
        return false;
    }

    public boolean pawnTake(int row, int col, ChessPosition currentPosition, ChessBoard board, Collection<ChessMove> collection){
        int move = 1;
        if (! inbounds(row,col)){return false;
        }
        var ourPiece = board.getPiece(currentPosition);
        var otherPiece = board.getPiece(new ChessPosition(row,col));
        var color = ourPiece.getTeamColor();
        if (otherPiece == null){
            return false;
        }
        if (ourPiece.getTeamColor() != otherPiece.getTeamColor()){
            if (color == ChessGame.TeamColor.BLACK && row == 1){
                promotion(row,col, currentPosition, collection);
            }else if (color == ChessGame.TeamColor.WHITE && row == 8){
                promotion(row,col, currentPosition, collection);
            }
            else{
                collection.add(new ChessMove(currentPosition, new ChessPosition(row,col), null));
            }
        }
        return true;
    }

    public boolean inbounds(int row, int col){
        return 0 < row && row <= 8 && 0 < col && col <= 8;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> collection = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece currPiece = board.getPiece(myPosition);

        if (currPiece == null){
            return collection;
        }else if (currPiece.getPieceType() == PieceType.ROOK){
            do {
                row += 1;
            }while (oneMove(row, col, myPosition, board, collection));

            row = myPosition.getRow();
            do {
                row -= 1;
            } while (oneMove(row, col, myPosition, board, collection));
            row = myPosition.getRow();
            do{
                col += 1;
            } while (oneMove(row, col, myPosition, board, collection));

            col =myPosition.getColumn();
            do{
                col -= 1;
            } while (oneMove(row, col, myPosition, board, collection));

            col = myPosition.getColumn();

        }else if (currPiece.getPieceType() == PieceType.BISHOP){
            do {
                row +=1;
                col += 1;
            }while (oneMove(row, col, myPosition, board, collection));
            col = myPosition.getColumn();
            row = myPosition.getRow();
            do {
                row +=1;
                col -= 1;
            }while (oneMove(row, col, myPosition, board, collection));
            col = myPosition.getColumn();
            row = myPosition.getRow();

            do {
                row -=1;
                col += 1;
            }while (oneMove(row, col, myPosition, board, collection));
            col = myPosition.getColumn();
            row = myPosition.getRow();

            do {
                row -=1;
                col -= 1;
            }while (oneMove(row, col, myPosition, board, collection));
            col = myPosition.getColumn();
            row = myPosition.getRow();

        }else if (currPiece.getPieceType() == PieceType.QUEEN){
            board.addPiece(myPosition, new ChessPiece(currPiece.getTeamColor(), PieceType.BISHOP));
            Collection<ChessMove> bishopMoves = pieceMoves(board, myPosition);
            board.addPiece(myPosition, new ChessPiece(currPiece.getTeamColor(), PieceType.ROOK));
            Collection<ChessMove> rookMoves = pieceMoves(board, myPosition);
            board.addPiece(myPosition, new ChessPiece(currPiece.getTeamColor(), PieceType.QUEEN));
            collection.addAll(bishopMoves);
            collection.addAll(rookMoves);

        }else if (currPiece.getPieceType() == PieceType.KNIGHT){
            oneMove(row + 2,col + 1, myPosition, board, collection);
            oneMove(row + 2,col - 1, myPosition, board, collection);
            oneMove(row - 2,col + 1, myPosition, board, collection);
            oneMove(row -2 ,col - 1, myPosition, board, collection);
            oneMove(row + 1,col + 2, myPosition, board, collection);
            oneMove(row + 1,col - 2, myPosition, board, collection);
            oneMove(row - 1,col + 2, myPosition, board, collection);
            oneMove(row - 1,col - 2, myPosition, board, collection);

        }else if (currPiece.getPieceType() == PieceType.KING){
            oneMove(row + 1,col + 1, myPosition, board, collection);
            oneMove(row + 1,col, myPosition, board, collection);
            oneMove(row + 1,col - 1, myPosition, board, collection);
            oneMove(row,col + 1, myPosition, board, collection);
            oneMove(row,col - 1, myPosition, board, collection);
            oneMove(row - 1,col + 1, myPosition, board, collection);
            oneMove(row - 1,col, myPosition, board, collection);
            oneMove(row - 1,col - 1, myPosition, board, collection);

        }else if (currPiece.getPieceType() == PieceType.PAWN){
            var color = currPiece.getTeamColor();
            if (color == ChessGame.TeamColor.BLACK){
                pawnMove(myPosition, board, collection);
                pawnTake(row - 1, col + 1, myPosition, board, collection);
                pawnTake(row - 1, col - 1, myPosition, board, collection);
            }
            else{
                pawnMove(myPosition, board, collection);
                pawnTake(row + 1, col + 1, myPosition, board, collection);
                pawnTake(row + 1, col - 1, myPosition, board, collection);
            }

        }



        return collection;
    }
}
