package chess;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;

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
        currentTeam = TeamColor.WHITE
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
            board.addPiece(endPosition, piece);
            board.addPiece(startPosition, null);
            if (isInCheck(piece.getTeamColor())){
                validMoves.add(move);
            }
            board.addPiece(endPosition, null);
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
        for (var mv : moves){
            if (mv.endPosition == move.endPosition){
                board.addPiece(move.endPosition, board.getPiece(move.startPosition));
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
        var currentColor=this.getTeamTurn();
        ChessPosition kingPosition = new ChessPosition(-1,-1);
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (int i=1; i <= 8; i++) {
            for (int j=1; j <= 8; j++) {
                ChessPosition position=new ChessPosition(i, j);
                ChessPiece pos=board.getPiece(position);
                if (pos != null && pos.getTeamColor() != getTeamTurn() && pos.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition=new ChessPosition(i, j);
                }

                if (pos != null &&  pos.getTeamColor() == getTeamTurn()) {
                    possibleMoves.addAll(pos.pieceMoves(board, position));
                }
            }
        }

        for (var position : possibleMoves){
            if (position.endPosition == kingPosition){
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }





    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();

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
