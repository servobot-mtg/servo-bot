package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.repositories.SusResponseRepository;
import com.ryan_mtg.servobot.game.sus.chat.SusResponder;
import org.springframework.stereotype.Component;

@Component
public class SusResponseSerializer {
    public SusResponseSerializer(final SusResponseRepository susResponseRepository) {
    }

    public SusResponder createResponder() {
        SusResponder susResponder = new SusResponder();
        susResponder.initalize();
        return susResponder;
    }
}
