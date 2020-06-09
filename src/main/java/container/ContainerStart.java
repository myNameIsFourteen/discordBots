package container;

import net.dv8tion.jda.api.entities.Member;

import java.util.*;

/**
 * Created by micha on 4/21/2020.
 */
public class ContainerStart {
    public List<ContainerPlayer> startGame(List<Member> playerNames) {
        int count = playerNames.size();
        List<ContainerPlayer> ret = new ArrayList<ContainerPlayer>();
        LinkedList<Integer> scores = new LinkedList<Integer>(ContainerConstants.GOODS);
        LinkedList<Integer> goods = new LinkedList<Integer>(ContainerConstants.GOODS);
        Collections.shuffle(goods);
        Collections.shuffle(scores);
        for (Member player : playerNames) {
            ret.add(new ContainerPlayer(player, goods.remove(), scores.remove()));
        }
        Collections.shuffle(ret);
        return ret;
    }
}
