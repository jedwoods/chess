package chess;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor currentTeam;
    ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
        currentTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece((startPosition));
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (piece == null){
            return null;
        }
        for (var move : piece.pieceMoves(board, startPosition)) {
            ChessPosition endPosition = move.endPosition;
            var tempPiece = board.getPiece((endPosition));
            board.addPiece(endPosition, piece);
            board.addPiece(startPosition, null);
            if (! isInCheck(piece.getTeamColor())){
                validMoves.add(move);
            }
            board.addPiece(endPosition, tempPiece);
            board.addPiece(startPosition, piece);
        }
    return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var moves = validMoves(move.startPosition);
        if (moves == null){
            throw new InvalidMoveException("Invalid Move Blech");
        }
        for (var mv : moves){
            if (mv.endPosition.getRow() == move.endPosition.getRow() && mv.endPosition.getColumn() == move.endPosition.getColumn() && mv.promotionPiece == null){
                board.addPiece(move.endPosition, board.getPiece(move.startPosition));
                board.addPiece(move.startPosition, null);
                return;
            }
            if (mv.endPosition.getRow() == move.endPosition.getRow() && mv.endPosition.getColumn() == move.endPosition.getColumn() && mv.promotionPiece == move.promotionPiece){
                var piece = board.getPiece(move.startPosition);
                var color = piece.getTeamColor();
                var promotion = mv.getPromotionPiece();
                board.addPiece(move.endPosition, new ChessPiece(color, promotion));
                board.addPiece(move.startPosition, null);
                return;
            }


        }
        throw new InvalidMoveException("Invalid Move");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var currentColor = teamColor;
        ChessPosition kingPosition = new ChessPosition(-1,-1);
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (int i=1; i <= 8; i++) {
            for (int j=1; j <= 8; j++) {
                ChessPosition position=new ChessPosition(i, j);
                ChessPiece pos = board.getPiece(position);
                if (pos != null && pos.getTeamColor() == teamColor && pos.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition=new ChessPosition(i, j);
                }
                else if (pos != null &&  pos.getTeamColor() != currentColor) {
                    Collection<ChessMove> tempMoves = pos.pieceMoves(board, position);
                    possibleMoves.addAll(pos.pieceMoves(board, position));
                }
            }
        }

        for (var position : possibleMoves){
            if (position.endPosition.row == kingPosition.row && position.endPosition.col == kingPosition.col){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        return possibleMoves(teamColor).isEmpty() && isInCheck(teamColor);
    }


    public Collection<ChessMove> possibleMoves(TeamColor teamColor){
        Collection<ChessMove> allMoves = new HashSet<>();
        for (int i=1; i <= 8; i++) {
            for (int j=1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i,j);
                var currentPiece = board.getPiece(currentPosition);
                if (currentPiece!= null && currentPiece.getTeamColor() == teamColor){
                    Collection<ChessMove> vMoves = validMoves(currentPosition);
                    allMoves.addAll(vMoves);

                }

            }
        }
        return allMoves;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return possibleMoves(teamColor).isEmpty() && ! isInCheck(teamColor);
    }





    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;

    }


    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
