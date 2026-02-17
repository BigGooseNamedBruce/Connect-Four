package com.connectfour.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.connectfour.services.ConnectFourService;
import com.connectfour.game.Player;
import com.connectfour.dto.MoveRequest;
import com.connectfour.dto.PlayerRequest;
import com.connectfour.dto.ConnectFourResponse;


@RestController
@RequestMapping("/api/connectfour")
public class ConnectFourController {
    private final ConnectFourService connectfourService = new ConnectFourService();

    // Get the current game state
    @GetMapping("/position")
    public String[][] getGamePosition() {
        return connectfourService.getGameArrayPosition();
    }

    @GetMapping("/test")
    public Player test() {
        return Player.RED;
    }

    @GetMapping("/winner")
    public String getWinner() {
        return connectfourService.checkWinner();
    }

    @PostMapping("/move")
    @ResponseBody
    public ConnectFourResponse makeMove(@RequestBody MoveRequest moveRequest) {
        connectfourService.place(moveRequest.getColumn(), moveRequest.getPlayer());
        String[][] board = connectfourService.toArray();
        int col = moveRequest.getColumn();
        return new ConnectFourResponse(board, col);
    }

    @PostMapping("/reset")
    public void reset() {
        connectfourService.reset();
    }

    @PostMapping("/compute")
    public ConnectFourResponse computeBestMove(@RequestBody PlayerRequest playerRequest) {
        int bestMove = connectfourService.computerBestMove(playerRequest.getPlayer());
        connectfourService.place(bestMove, playerRequest.getPlayer());
        String[][] board = connectfourService.toArray();
        return new ConnectFourResponse(board, bestMove);
    }
}