package com.example.thechefbot.presentation.ChatBotFeat.dummy

import com.example.thechefbot.presentation.ChatBotFeat.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatSession


val dummyMessages: List<ChatMessage> = buildList {
    val now = System.currentTimeMillis()
    val email = "user@example.com"

    // Session 101
    add(ChatMessage(1, 101, email, "How do I make jollof rice?",
        "Start by parboiling rice, blend tomatoes/peppers, fry with onions, add stock, steam on low.", null, now - 60_000L))
    add(ChatMessage(2, 101, email, "What protein pairs best?",
        "Grilled chicken or fried plantain works great. Add coleslaw for freshness.", null, now - 55_000L))
    add(ChatMessage(3, 101, email, "Share a quick spice mix.",
        "Paprika, curry, thyme, bay leaf, garlic powder, stock cube, salt to taste.", null, now - 50_000L))
    add(ChatMessage(4, 101, email, "Any vegetarian option?",
        "Use veggie stock; add mushrooms and carrots. Finish with butter for sheen.", null, now - 45_000L))
    add(ChatMessage(0, 101, email, "Image inspo?",
        "Here’s a plating idea: dome rice, side salad, drizzle pepper sauce.", "file:///pictures/jollof_plate.jpg", now - 40_000L))

    // Session 202
    add(ChatMessage(6, 202, email, "Plan a 3-day workout split.",
        "Day1 Push, Day2 Pull, Day3 Legs. Rest, then repeat.", null, now - 30_000L))
    add(ChatMessage(7, 202, email, "Give me Push day details.",
        "Bench press, OHP, incline DB press, lateral raises, triceps dips.", null, now - 25_000L))
    add(ChatMessage(8, 202, email, "What about nutrition?",
        "Aim 1.6–2.2g protein/kg, balanced carbs/fats, hydrate and sleep 7–9h.", null, now - 20_000L))
    add(ChatMessage(9, 202, email, "Sample grocery list?",
        "Chicken breast, eggs, oatmeal, rice, beans, spinach, tomatoes, yogurt, nuts.", null, now - 15_000L))
    add(ChatMessage(10, 202, email, "Any rest day tips?",
        "Light walk, mobility work, foam roll. Keep steps ~8–10k.", null, now - 10_000L))
}

val dummySessions: List<ChatSession> = buildList {
    val now = System.currentTimeMillis()
    val email = "user@example.com"

    add(ChatSession(0, "Quick dinner ideas",             now -  1_000L, email))
    add(ChatSession(0, "Meal prep for the week",         now -  5_000L, email))
    add(ChatSession(0, "High-protein breakfast",         now - 10_000L, email))
    add(ChatSession(0, "Budget-friendly groceries",      now - 15_000L, email))
    add(ChatSession(0, "Spicy jollof tweaks",            now - 20_000L, email))
    add(ChatSession(0, "Vegetarian lunch boxes",         now - 25_000L, email))
    add(ChatSession(0, "Dinner party menu (6 ppl)",      now - 30_000L, email))
    add(ChatSession(0, "Tuna recipe remixes",            now - 45_000L, email))
    add(ChatSession(0, "Low-carb snacks on-the-go",      now - 60_000L, email))
    add(ChatSession(0, "Kid-friendly meals",             now - 75_000L, email))
    add(ChatSession(0, "Nigerian soups rotation",        now - 90_000L, email))
    add(ChatSession(0, "Smoothies & shakes planner",     now -105_000L, email))
}
