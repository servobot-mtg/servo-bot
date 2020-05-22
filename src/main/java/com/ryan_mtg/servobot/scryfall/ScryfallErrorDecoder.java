package com.ryan_mtg.servobot.scryfall;

import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class ScryfallErrorDecoder implements ErrorDecoder {
    private Decoder decoder;

    public ScryfallErrorDecoder(final Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Exception decode(final String methodKey, final Response response) {
        try {
            ErrorResponse errorResponse = (ErrorResponse) decoder.decode(response, ErrorResponse.class);
            return new ScryfallQueryException(errorResponse.getDetails());
        } catch (IOException e) {
            return e;
        }
    }
}
