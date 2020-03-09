package com.payware.business.controller;

import io.javalin.http.Context;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AppController {

    public void health(Context ctx) {
        Map<String, String> health = new HashMap<>();
        health.put("status", "healthy");
        ctx.json(health);
    }
}
