package com.connectfour.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.connectfour.services.ConnectFourService;
import com.connectfour.models.MoveRequest;

@RestController
@RequestMapping("/api/connectfour")
public class ConnectFourController {
    private final ConnectFourService connect4Service = new ConnectFourService();

    // Get the current game state
    @GetMapping("/position")
    public String[][] getGamePosition() {
        return connect4Service.getGameArrayPosition();
    }

    // Make a move for a player
    @PostMapping("/move")
    public String[][] makeMove(@RequestBody MoveRequest moveRequest) {
        System.out.println("Received move: " + moveRequest);
        return connect4Service.playerMove(moveRequest.getColumn(), moveRequest.getPlayer());
    }
}
