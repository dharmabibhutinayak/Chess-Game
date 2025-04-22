import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class ChessGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Java Chess");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ChessBoard board = new ChessBoard();
            frame.setContentPane(board);
            frame.setVisible(true);
        });
    }
}

class ChessBoard extends JPanel {
    private JButton[][] buttons = new JButton[8][8];
    private Piece[][] board = new Piece[8][8];
    private int selectedRow = -1, selectedCol = -1;
    private String currentPlayer = "White";
    private AIPlayer aiPlayer;

    public ChessBoard() {
        aiPlayer = new AIPlayer(this, board, "Black");
        setLayout(new GridLayout(8, 8));
        initializeBoard();
        drawBoard();
    }

    private void initializeBoard() {
        String[] order = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        for (int i = 0; i < 8; i++) {
            board[0][i] = new Piece(order[i], "Black");
            board[1][i] = new Piece("P", "Black");
            board[6][i] = new Piece("P", "White");
            board[7][i] = new Piece(order[i], "White");
        }
    }

    private void drawBoard() {
        removeAll();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
                btn.setFocusPainted(false);
                btn.setBackground((row + col) % 2 == 0 ? new Color(240, 217, 181) : new Color(181, 136, 99));

                Piece piece = board[row][col];
                if (piece != null) btn.setText(piece.getUnicode());

                int r = row, c = col;
                btn.addActionListener(e -> handleClick(r, c));
                buttons[row][col] = btn;
                add(btn);
            }
        }
        revalidate();
        repaint();
    }

    private void handleClick(int row, int col) {
        if (selectedRow == -1) {
            if (board[row][col] != null && board[row][col].color.equals(currentPlayer)) {
                selectedRow = row;
                selectedCol = col;
                buttons[row][col].setBackground(Color.YELLOW);
            }
        } else {
            Piece selectedPiece = board[selectedRow][selectedCol];
            if (isValidMove(selectedPiece, selectedRow, selectedCol, row, col)) {
                board[row][col] = selectedPiece;
                board[selectedRow][selectedCol] = null;
                selectedRow = selectedCol = -1;
                currentPlayer = "Black";
                drawBoard();
                
                if (isCheck("White")) showMessage("White is in check!");
                if (isCheckmate("White")) showMessage("Checkmate! Black wins!");

                Timer timer = new Timer(500, e -> {
                    aiPlayer.makeSmartMove();
                    currentPlayer = "White";
                    drawBoard();
                    if (isCheck("Black")) showMessage("Black is in check!");
                    if (isCheckmate("Black")) showMessage("Checkmate! White wins!");
                });
                timer.setRepeats(false);
                timer.start();
                return;
            }
            selectedRow = selectedCol = -1;
            drawBoard();
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public boolean isValidMove(Piece piece, int fromRow, int fromCol, int toRow, int toCol) {
        if (piece == null) return false;
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) return false;
        if (fromRow == toRow && fromCol == toCol) return false;
        Piece target = board[toRow][toCol];
        if (target != null && target.color.equals(piece.color)) return false;

        int dr = toRow - fromRow;
        int dc = toCol - fromCol;

        switch (piece.name) {
            case "P":
                int dir = piece.color.equals("White") ? -1 : 1;
                if (dc == 0 && target == null) {
                    if (dr == dir) return true;
                    if ((piece.color.equals("White") && fromRow == 6 || piece.color.equals("Black") && fromRow == 1)
                            && dr == 2 * dir && board[fromRow + dir][fromCol] == null)
                        return true;
                } else if (Math.abs(dc) == 1 && dr == dir && target != null && !target.color.equals(piece.color)) {
                    return true;
                }
                break;
            case "R":
                if (dr == 0 || dc == 0) return isPathClear(fromRow, fromCol, toRow, toCol);
                break;
            case "N":
                if ((Math.abs(dr) == 2 && Math.abs(dc) == 1) || (Math.abs(dr) == 1 && Math.abs(dc) == 2)) return true;
                break;
            case "B":
                if (Math.abs(dr) == Math.abs(dc)) return isPathClear(fromRow, fromCol, toRow, toCol);
                break;
            case "Q":
                if (dr == 0 || dc == 0 || Math.abs(dr) == Math.abs(dc)) return isPathClear(fromRow, fromCol, toRow, toCol);
                break;
            case "K":
                if (Math.abs(dr) <= 1 && Math.abs(dc) <= 1) return true;
                break;
        }
        return false;
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Integer.compare(toRow, fromRow);
        int dc = Integer.compare(toCol, fromCol);
        int r = fromRow + dr, c = fromCol + dc;
        while (r != toRow || c != toCol) {
            if (board[r][c] != null) return false;
            r += dr;
            c += dc;
        }
        return true;
    }

    public boolean isCheck(String color) {
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.name.equals("K") && p.color.equals(color)) {
                    kingRow = r;
                    kingCol = c;
                }
            }
        }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && !p.color.equals(color)) {
                    if (isValidMove(p, r, c, kingRow, kingCol)) return true;
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(String color) {
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece p = board[fromRow][fromCol];
                if (p != null && p.color.equals(color)) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            Piece temp = board[toRow][toCol];
                            if (isValidMove(p, fromRow, fromCol, toRow, toCol)) {
                                board[toRow][toCol] = p;
                                board[fromRow][fromCol] = null;
                                boolean stillCheck = isCheck(color);
                                board[fromRow][fromCol] = p;
                                board[toRow][toCol] = temp;
                                if (!stillCheck) return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public Piece[][] getBoard() {
        return board;
    }
}

class Piece {
    String name;
    String color;

    public Piece(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getUnicode() {
        return switch (name) {
            case "P" -> color.equals("White") ? "♙" : "♟";
            case "R" -> color.equals("White") ? "♖" : "♜";
            case "N" -> color.equals("White") ? "♘" : "♞";
            case "B" -> color.equals("White") ? "♗" : "♝";
            case "Q" -> color.equals("White") ? "♕" : "♛";
            case "K" -> color.equals("White") ? "♔" : "♚";
            default -> "?";
        };
    }
}

class AIPlayer {
    private Piece[][] board;
    private String color;
    private ChessBoard chessBoard;

    public AIPlayer(ChessBoard chessBoard, Piece[][] board, String color) {
        this.board = board;
        this.color = color;
        this.chessBoard = chessBoard;
    }

    public void makeSmartMove() {
        List<int[]> captureMoves = new ArrayList<>();
        List<int[]> allMoves = new ArrayList<>();

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece p = board[fromRow][fromCol];
                if (p != null && p.color.equals(color)) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            if (chessBoard.isValidMove(p, fromRow, fromCol, toRow, toCol)) {
                                int[] move = {fromRow, fromCol, toRow, toCol};
                                if (board[toRow][toCol] != null && !board[toRow][toCol].color.equals(color)) {
                                    captureMoves.add(move);
                                } else {
                                    allMoves.add(move);
                                }
                            }
                        }
                    }
                }
            }
        }
        Random rand = new Random();
        int[] move = !captureMoves.isEmpty() ? captureMoves.get(rand.nextInt(captureMoves.size()))
                                             : !allMoves.isEmpty() ? allMoves.get(rand.nextInt(allMoves.size())) : null;
        if (move != null) {
            board[move[2]][move[3]] = board[move[0]][move[1]];
            board[move[0]][move[1]] = null;
        }
    }
}

