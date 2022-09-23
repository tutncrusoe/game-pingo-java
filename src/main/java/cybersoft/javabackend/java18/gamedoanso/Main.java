package cybersoft.javabackend.java18.gamedoanso;

import cybersoft.javabackend.java18.gamedoanso.model.GameSession;
import cybersoft.javabackend.java18.gamedoanso.model.Player;
import cybersoft.javabackend.java18.gamedoanso.repository.GameSessionRepository;
import cybersoft.javabackend.java18.gamedoanso.repository.PlayerRepository;
import cybersoft.javabackend.java18.gamedoanso.service.GameService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        GameSessionRepository gameSessionRepository = new GameSessionRepository();
        LinkedHashMap<String, Integer> listPlayersRanking = gameSessionRepository.listAllPlayersRanking();
        System.out.println("=========================");
        System.out.println("List All Players Ranking:" + listPlayersRanking);

        // Create List 1 that store keys
        List<String> list1
                = new ArrayList<String>(listPlayersRanking.keySet());

        // display List 1
        System.out.println("List 1 - " + list1);

        // Create List 2 that store values
        List<Integer> list2
                = new ArrayList<Integer>(listPlayersRanking.values());

        // display List 1
        System.out.println("List 2 - " + list2);
    }

}
