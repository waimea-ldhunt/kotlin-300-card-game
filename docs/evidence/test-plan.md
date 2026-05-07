# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Map and Deck setup - GAMEPLAY

Testing that the map is generated correctly and the Card Decks are organised and generated correctly.

### Test Data To Use

Printed output of map after generation,
Printed output of normalDeck after generation,
Printed output of legendaryDeck after generation,

### Expected Test Result

The Map should contain 7 entries each containing two identical or different locations.
The normalDeck should correctly contain every normal card.
The legendaryDeck should correctly contain every legendary card.

---

## Tutorial Scroll - VALID, BOUNDARY, INVALID

Testing to make sure the tutorial works correctly and the buttons function as they should during the tutorial.

### Test Data To Use

Valid/Boundary test, testing using the tutorial as expected.

Invalid test, attempting to move out of bounds of the tutorial text list.

### Expected Test Result

The player should be able to move back and forth through the tutorial lines in between the first and last, they should
be blocked from going back on the first line, and the tutorial should automatically end after the last line.

---

## Travelling - GAMEPLAY

Testing that when the player is given the option to travel, they are transported to the new location correctly.

### Test Data Used

Gameplay of travelling x2

### Expected Test Result

When the player chooses a direction they will be transported to the next location without errors and the next enemy is
set up.

---

## Fight Option - VALID, INVALID

Testing that the fight button works correctly.

### Test Data To Use

Testing of the fight button before (Valid) and during (Invalid) a fight

### Expected Test Result

The player should be able to use the fight button to start the fight, and it should work as intended however the player
should not be able to use the button during the fight.

---

## Flee Option - GAMEPLAY, VALID, INVALID

Testing of the flee option and ensuring it works correctly.

### Test Data To Use

Testing of fleeing before and during a fight.
Also testing of flee success and flee failure.

### Expected Test Result

The player should be able to try to flee before a fight and if they do not escape, the fight will begin automatically,
they should also be able to flee during combat and if they fail, the enemy will start their turn. If they succeed in
they should also be able to flee during combat and if they fail, the enemy will start their turn. If they succeed in
fleeing in any of these scenarios, the battle should end immediately with no rewards.

---

## Card-Main Window Interaction - UI, GAMEPLAY, VALID, BOUNDARY, INVALID

Testing card placement and detection.

### Test Data To Use

Playing cards during combat,
Attempting to play cards outside of combat.

### Expected Test Result

Card should be detected when brought within a specified distance (10px) of the card area, it should not be detected when
not in combat.

---

## Card Placed - Gameplay

Testing that when the Card placement is detected the cards effect is run correctly.

### Test Data To Use

Playing a card during combat.

### Expected Test Result

When the card is placed, the specific card effect should occur and the card should be relocated below the main window.

---

## Card Window UI Updates - UI, GAMEPLAY

Testing that the Card Window UI and function is Updated whenever the card is dropped or redrawn.

### Test Data To Use

The Card Window UI before the card is played,
The Card Window UI after the card is played.

### Expected Test Result

When the Card Window is played, the card is randomised becoming a different card, all the UI should update to the new
card and work like the new card.

---

## Enemy Turn - Gameplay

Testing that the enemies turn plays out as intended.

### Test Data To Use

Multiple rounds of combat gameplay.

### Expected Test Result

The enemy should have a short delay, and then they will attempt tp use their regular attack,
if they hit, the player should take damage, else no damage occurs.
The enemy should then use their special ability if they have one (might need to try hit.).

---

## BattleEnd/Lose/Win Detection - GAMEPLAY

Detection for the battle end, win and lose states.

### Test Data To Use

Defeating an enemy during gameplay.
Reaching the win and lose conditions during gameplay.

### Expected Test Result

If the player reduces the enemy to or below 0 health, the battle should end and the travel phase begin.
If the player defeats the final enemy in the final location, the ending sequence should play.
If the players health is reduced to 0 or beyond, the defeat sequence should play.

---

## Health Limits - VALID, BOUNDARY, INVALID

Testing Player and Enemy health limits.

### Test Data To Use

Player using a healing card that would increase health beyond maximum.
Enemy healing ability that would increase its health beyond maximum.

### Expected Test Result

Both Player and Enemy should have health capped by their maximum health and cannot heal beyond that.

(Miracle Potion increases player max health)

---