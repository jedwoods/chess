package dataaccess.GameDataBase;

import chess.ChessGame;

public record gameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
