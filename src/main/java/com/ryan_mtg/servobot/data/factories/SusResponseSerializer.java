package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.repositories.SusResponseRepository;
import com.ryan_mtg.servobot.game.sus.chat.SusResponder;
import org.springframework.stereotype.Component;

@Component
public class SusResponseSerializer {
    private final SusResponseRepository susResponseRepository;

    public SusResponseSerializer(final SusResponseRepository susResponseRepository) {
        this.susResponseRepository = susResponseRepository;
    }

    public SusResponder createResponder() {
        SusResponder susResponder = new SusResponder();
        susResponder.initalize();
        return susResponder;
    }
}
