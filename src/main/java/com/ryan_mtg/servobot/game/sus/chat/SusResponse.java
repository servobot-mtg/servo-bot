package com.ryan_mtg.servobot.game.sus.chat;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SusResponse {
    @Getter
    private int id;

    private final List<WeightedResponse> responses = new ArrayList<>();

    public static SusResponse create(final String... responses) {
        SusResponse susResponse = new SusResponse();
        for (String response : responses) {
            susResponse.add(new WeightedResponse(1, response));
        }
        return susResponse;
    }

    public static SusResponse create(final int firstWeight, final String... responses) {
        SusResponse susResponse = new SusResponse();
        int weight = firstWeight;
        for (String response : responses) {
            susResponse.add(new WeightedResponse(weight, response));
            weight = 1;
        }
        return susResponse;
    }


    public static SusResponse create(final int weight1, final String response1, final int weight2,
                                     final String response2) {
        SusResponse susResponse = new SusResponse();
        susResponse.add(new WeightedResponse(weight1, response1));
        susResponse.add(new WeightedResponse(weight2, response2));
        return susResponse;
    }

    public void add(final WeightedResponse response) {
        responses.add(response);
    }

    public String respond(final String name, final String sender, final Random random)  {
        int total = getTotalWeight();
        String template = selectTemplate(random.nextInt(total));
        return evaluateTemplate(template, name, sender);
    }

    private int getTotalWeight() {
        int total = 0;
        for (WeightedResponse response : responses) {
            total += response.getWeight();
        }
        return total;
    }

    private String selectTemplate(final int choice) {
        int total = 0;
        for (WeightedResponse response : responses) {
            total += response.getWeight();
            if (choice < total) {
                return response.getResponse();
            }
        }
        throw new IllegalStateException("unexpected total: " + total);
    }

    private String evaluateTemplate(final String template, final String name, final String sender)  {
        String result = template.replace("<name>", name);
        return sender == null ? result : result.replace("<sender>", sender);
    }
}