package com.connectfour.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connectfour.services.ConnectFourService;
import com.connectfour.models.MoveRequest;
import com.connectfour.models.PlayerRequest;


@RestController
@RequestMapping("/api/connectfour")
public class ConnectFourController {
    private final ConnectFourService connectfourService = new ConnectFourService();

    // Get the current game state
    @GetMapping("/position")
    public String[][] getGamePosition() {
        return connectfourService.getGameArrayPosition();
    }

    @GetMapping("/winner")
    public String getWinner() {
        return connectfourService.checkWinner();
    }

    // Make a move for a player
    @PostMapping("/move")
    public String[][] makeMove(@RequestBody MoveRequest moveRequest) {
        //System.out.println("Received move: " + moveRequest);
        return connectfourService.playerMove(moveRequest.getColumn(), moveRequest.getPlayer());
    }
    

    @PostMapping("/reset")
    public void reset() {
        connectfourService.reset();
    }

    @PostMapping("/compute")
    public int computeBestMove(@RequestBody PlayerRequest playerRequest) {
        return connectfourService.computerBestMove(playerRequest.getPlayer());
    }
}