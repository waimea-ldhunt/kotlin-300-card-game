import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import kotlin.math.sin
import kotlin.system.exitProcess

enum class GamePhase { //Enum containing each different possible game phase
    INTRO, TUTORIAL, TRAVEL, BATTLE_START, PLAYER_TURN, ENEMY_TURN, DEFEAT, ENDING
}

enum class CardEffect { //Enum containing each different possible card effect
    DAMAGE, HEAL, ACCELERATE, MIRACLE, DODGE, POISON;
}

enum class EnemySpecial { //Enum containing each different possible enemy special ability
    HEAL, ACCELERATE, POWER_UP, DESTROY_PLANET, FREEZE, DEVOUR, POWER_STEAL
}

/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()                        // Initialise the LAF

    val game = Game()                             // Get an app state object
    val window = MainWindow(game)                 // Spawn the UI, passing in the app state

    SwingUtilities.invokeLater { window.show() }
}


/**
 * Manage app state
 */
class Game {
    private var distanceTravelled = 0
    var maxHealth = 100
    var health = maxHealth
    var speed = 5
    var dodgeChance = 20

    val tutorialText =
        mutableListOf(
            "As you explore the world,\nyou are likely to run into dangerous enemies on the road,\nyou must fight them to continue your journey.",
            "When you encounter an enemy you can either Fight or Flee,\nif you choose to flee but the enemy is faster than you\nthey will catch up and you will be forced to fight.",
            "In a Fight you will need to use Cards to defend yourself,\nto use a Card's effect, place it in the designated area.",
            "If you defeat an enemy you will be rewarded with cards,\nif you are lucky you might even get a Legendary card."
        )

    val gameLog = mutableListOf<String>()

    var phase = GamePhase.INTRO

    val hand = mutableListOf<CardWindow>()
    var legendaryCount = 0

    lateinit var location: Location
    lateinit var enemy: Enemy

    private val normalDeck = mutableListOf<Card>()
    private val legendaryDeck = mutableListOf<Card>()
    val map = mutableListOf<List<Location>>()

    init {
        setupGame()
    }

    private fun setupGame() {
        /*
        * SETTING UP CARDS
        * */

        val stick = Card("Big Stick", "stick.png", CardEffect.DAMAGE, 5, false)
        val shovel = Card("Shovel", "shovel.png", CardEffect.DAMAGE, 7, false)
        val axe = Card("Axe", "axe.png", CardEffect.DAMAGE, 8, false)
        val sword = Card("Sword", "sword.png", CardEffect.DAMAGE, 10, false)
        val revolver = Card("Revolver", "revolver.png", CardEffect.DAMAGE, 15, false)
        val bomb = Card("Bomb", "bomb.png", CardEffect.DAMAGE, 20, false)
        val wand = Card("Wand", "wand.png", CardEffect.DAMAGE, 30, false)

        val healingPotion = Card("Healing Potion", "healingPotion.png", CardEffect.HEAL, 30, false)

        val speedPotion = Card("Speed Potion", "speedPotion.png", CardEffect.ACCELERATE, 3, false)

        val miraclePotion = Card("Miracle Potion", "placeholder.png", CardEffect.MIRACLE, 100, true)
        val dodgeBook = Card("The art of Dodging", "placeholder.png", CardEffect.DODGE, 40, true)
        val nuke = Card("Nuclear Bomb", "placeholder.png", CardEffect.DAMAGE, 123, true)
        val bioweapon = Card("Chemical 42", "placeholder.png", CardEffect.POISON, 10, true)

        normalDeck.add(stick)
        normalDeck.add(shovel)
        normalDeck.add(axe)
        normalDeck.add(sword)
        normalDeck.add(revolver)
        normalDeck.add(bomb)
        normalDeck.add(wand)
        normalDeck.add(healingPotion)
        normalDeck.add(healingPotion)
        normalDeck.add(speedPotion)

        legendaryDeck.add(miraclePotion)
        legendaryDeck.add(dodgeBook)
        legendaryDeck.add(nuke)
        legendaryDeck.add(bioweapon)

        /*
        * SETTING UP ENEMIES
        */

        val stranger = Enemy(
            "Stranger",
            "stranger.png",
            365,
            10..10,
            999,
            "Donate",
            null
        )

        val ant = Enemy(
            "Resilient Ant",
            "ant.png",
            21,
            2..4,
            10,
            "Bite",
            null
        )

        val flyingSnake = Enemy(
            "Flying Snake",
            "flyingSnake.png",
            26,
            1..5,
            10,
            "Snakebite",
            null
        )

        val flower = Enemy(
            "Violent Sunflower",
            "flower.png",
            19,
            1..7,
            0,
            "Pollen Burst",
            null
        )

        val micheal = Enemy(
            "Michael, Destroyer of Worlds",
            "michael.png",
            60,
            3..5,
            8,
            "Cuteness Overdrive",
            SpecialAbility("Obliterate Planet", EnemySpecial.DESTROY_PLANET)
        )

        val walkingFish =
            Enemy(
                "Walking Fish",
                "walkingFish.png",
                75,
                6..12,
                5,
                "Fish Slap",
                SpecialAbility("Slippery", EnemySpecial.ACCELERATE)
            )

        val giraffe =
            Enemy(
                "Tall Horse",
                "giraffe.png",
                73,
                5..11,
                7,
                "Hoof Kick",
                SpecialAbility("Wierd Neigh", EnemySpecial.POWER_UP)
            )

        val cloud =
            Enemy(
                "Malevolent Cloud",
                "cloud.png",
                46,
                7..13,
                5,
                "Thunderclap",
                SpecialAbility("Lightning Bolt", EnemySpecial.FREEZE)
            )

        val crab =
            Enemy(
                "Crab: The Ultimate Lifeform",
                "crab.png",
                86,
                3..6,
                5,
                "Claw Pinch",
                SpecialAbility("Evolve", EnemySpecial.HEAL)
            )

        val leo = Enemy(
            "Leo",
            "leo.png",
            45,
            5..10,
            5,
            "Pounce",
            SpecialAbility("Roar", EnemySpecial.POWER_UP)
        )

        val redOx =
            Enemy(
                "Red Ox",
                "redOx.png",
                60,
                8..12,
                4,
                "Reduce",
                SpecialAbility("Oxidise", EnemySpecial.HEAL)
            )

        val kraken =
            Enemy(
                "Giant Squid",
                "kraken.png",
                130,
                1..1,
                2,
                "Tentacle Rush",
                SpecialAbility("Devour", EnemySpecial.DEVOUR)
            )
        val boss =
            Enemy(
                "Memory Thief",
                "thief.gif",
                365,
                3..7,
                999,
                "Distort",
                SpecialAbility("Power Steal", EnemySpecial.POWER_STEAL)
            )

        enemy = stranger

        /*
        * SETTING UP LOCATIONS
        * */

        val village = Location("The Village", "village.png", mutableListOf(stranger), false)
        map.add(listOf(village))

        //Temperate biomes (2 out of 3 are visible)
        val meadow = Location("Sunlit Meadow", "meadow.png", mutableListOf(ant, flyingSnake, flower), false)
        val forest = Location("Deep Forest", "forest.png", mutableListOf(ant, flyingSnake, flower), false)
        val river = Location("Rushing River", "river.png", mutableListOf(ant, flyingSnake, flower), false)
        map.add(listOf(meadow, forest, river).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Cold biomes (2 out of 3 are visible)
        val mountains = Location("Autumn Hills", "mountains.png", mutableListOf(micheal), false)
        val cave = Location("Mysterious Cave", "cave.png", mutableListOf(micheal), false)
        val summit = Location("Frosted Peak", "summit.png", mutableListOf(micheal), false)
        map.add(listOf(cave, mountains, summit).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Wet biomes (2 out of 3 are visible)
        val jungle = Location("Humid Jungle", "jungle.png", mutableListOf(walkingFish, giraffe), true)
        val wetland = Location("Muddy Wetland", "wetland.png", mutableListOf(walkingFish, giraffe), true)
        val lake = Location("Serene Lake", "lake.png", mutableListOf(walkingFish, giraffe), true)
        map.add(listOf(jungle, wetland, lake).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Warm biomes (2 out of 3 are visible)
        val beach = Location("Sunset Beach", "beach.png", mutableListOf(cloud, crab), false)
        val desert = Location("Burning Desert", "desert.png", mutableListOf(cloud, crab), false)
        val canyon = Location("Scorched Gorge", "canyon.png", mutableListOf(cloud, crab), false)
        map.add(listOf(beach, desert, canyon).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //The first of the mysterious lands, with a boss fight
        val foggySea = Location("Foggy Sea", "sea.png", mutableListOf(kraken), true)
        map.add(listOf(foggySea, foggySea)) //player can only travel to this location

        //The second of the mysterious lands
        val strangeLand = Location("Strange Land", "land.png", mutableListOf(leo, redOx), false)
        map.add(listOf(strangeLand, strangeLand)) //player can only travel to this location

        //The final of the mysterious lands, holding the final boss fight
        val lostRealm = Location("The Lost Realm", "lost-realm.png", mutableListOf(boss), false)
        map.add(listOf(lostRealm, lostRealm)) //player can only travel to this location

        location = village

        //Initial game dialouge
        log("Howdy Stranger!\nI see you are wanting to leave the town,\nwould you like any advice from a seasoned traveller?")
    }

    fun travel(choice: Char) { // Sets the location based on choice
        when (choice) {
            'A' -> location = map[(distanceTravelled / 100) + 1][0]
            'B' -> location = map[(distanceTravelled / 100) + 1][1]
        }
        distanceTravelled += 100

        //gets enemy from the location
        enemy = location.possibleEnemies[location.possibleEnemies.indices.random()]
    }

    fun log(str: String) { //adds a line of text to the game log
        gameLog.add(str)
    }

    fun drawRandomNormalCard(): Card { //gets random non-legendary card from deck
        return normalDeck[normalDeck.indices.random()]
    }

    fun drawRandomLegendaryCard(): Card { //gets random legendary card from deck
        return legendaryDeck[legendaryDeck.indices.random()]
    }

    fun getRandomNormalCardFromHand(): CardWindow { //gets random non-legendary card from player hand
        while (true) {
            val window = hand[hand.indices.random()]
            if (window.card.legendary) continue
            else return window
        }
    }

    fun enemyTurn(game: Game): Boolean { //runs through enemy attack and special ability, returns true if loss detected
        log("----------")
        log("${enemy.name} uses Attack: ${enemy.attackName}")

        if (tryHit(false)) {
            val damage = enemy.attack.random()
            health -= damage
            log("You took $damage Damage")
            if (!checkLoss()) {
                enemy.specialAbility?.doSpecialAttack(game)
            }
        } else {
            log("${enemy.name} missed!")
        }

        //if not yet lost, handle poison
        if (!checkLoss()) {
            if (enemy.poison > 0) {
                log("----------")
                log("${enemy.name} is being slowly eaten by Poison")
                log("${enemy.name} took ${enemy.poison} Damage")
                enemy.health -= enemy.poison
            }
            return false
        } else {
            return true
        }

    }

    fun tryFlee(): Boolean { //speed contest between player and enemy, if enemy speed > player speed, 0% chance of escape
        return ((1..100).random() > ((enemy.speed / speed) * 100))
    }

    private fun checkLoss(): Boolean { //checks if player has lost
        return (health <= 0)
    }

    fun checkWin(): Boolean { //checks if enemy has lost
        return (enemy.health <= 0)
    }

    fun resetStats() { //resets player values
        maxHealth = 100
        if (health > maxHealth) health = maxHealth
        speed = 5
        dodgeChance = 20
        log("#--------------------")
        log("You feel the effects of battle fading")

        //unfreeze cards
        for (cardWindow in hand) {
            cardWindow.frame.isEnabled = true
        }
    }

    fun tryHit(withDisadvantage: Boolean): Boolean { //try to hit player, if "has disadvantage" only hits if passed twice
        return if (withDisadvantage) {
            ((1..100).random() > dodgeChance && (1..100).random() > dodgeChance)
        } else {
            ((1..100).random() > dodgeChance)
        }
    }
}


/**
 * Main UI window, handles user clicks, UI updates, ect...
 */
class MainWindow(private val game: Game) {
    val frame = JFrame("WINDOW TITLE")
    private val pane = JLayeredPane().apply { layout = null }

    private val quitButton = JButton("Quit")

    //Location Elements
    private val locationLabel = JLabel(game.location.name)
    private val locationImageLabel = JLabel()
    private var locationIcon = ImageIcon(game.location.icon)

    //Enemy Image Elements
    private val enemyImageLabel = JLabel()
    private var enemyIcon = ImageIcon(game.enemy.icon)

    //Player Health Elements
    private val healthBar = JProgressBar(0, 100)
    private val healthBarLabel = JLabel("Health")

    //Player Stat Elements
    private val playerStatsLabel = JLabel("Your Stats:")
    private val healthLabel = JLabel("Health: ")
    private val healthValueLabel = JLabel("${game.health}/${game.maxHealth} ")
    private val speedLabel = JLabel("Speed: ")
    private val speedValueLabel = JLabel("${game.speed}")
    private val dodgeLabel = JLabel("Dodge Chance: ")
    private val dodgeValueLabel = JLabel("${game.dodgeChance}%")
    private val cardsLabel = JLabel("<html>Cards in your Hand:<html>")
    private val cardsCountLabel = JLabel("${game.hand.size}")

    //Enemy Health Elements
    private val enemyBar = JProgressBar(0, 100)
    private val enemyBarLabel = JLabel("Mysterious Stranger")

    //Game Button Elements
    private val buttonA = JButton("Yes please (Tutorial)")
    private val buttonB = JButton("I can handle myself")

    //Game Log Elements
    private val gameLogText = JTextArea()
    private val gameLogArea = JScrollPane(gameLogText)

    private val cardArea = JLabel("PLACE")

    private var tutorialPosition = -1

    var windowLocation: Point = frame.location

    val placeArea = Point(1090, 80) //Area where cards are placed

    //Timers
    private val enemyDelayTimer = Timer(1000, null)
    private val battleEndTimer = Timer(1000, null)

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() { //sets locations and areas of each element and then adds them to the pane
        pane.preferredSize = Dimension(1300, 420)

        quitButton.setBounds(960, 380, 120, 30)

        locationLabel.setBounds(500, 380, 300, 35)

        locationImageLabel.setBounds(350, 10, 600, 400)

        enemyImageLabel.setBounds(350, 10, 600, 400)

        healthBar.setBounds(10, 10, 330, 50)
        healthBarLabel.setBounds(10, 10, 330, 50)

        playerStatsLabel.setBounds(960, 70, 120, 50)

        healthLabel.setBounds(960, 120, 50, 30)
        healthValueLabel.setBounds(1000, 120, 80, 30)
        speedLabel.setBounds(960, 160, 50, 30)
        speedValueLabel.setBounds(1000, 160, 80, 30)
        dodgeLabel.setBounds(960, 200, 90, 30)
        dodgeValueLabel.setBounds(1000, 200, 80, 30)
        cardsLabel.setBounds(960, 240, 120, 50)
        cardsCountLabel.setBounds(960, 300, 120, 70)

        enemyBar.setBounds(960, 10, 330, 50)
        enemyBarLabel.setBounds(960, 10, 330, 50)

        buttonA.setBounds(10, 70, 160, 50)
        buttonB.setBounds(180, 70, 160, 50)

        gameLogArea.setBounds(10, 130, 330, 280)
        gameLogText.setBounds(0, 0, 280, 280)

        cardArea.setBounds(placeArea.x, placeArea.y, 200, 330)

        //Adding elements to window pane

        pane.add(quitButton)

        pane.add(locationLabel, JLayeredPane.DEFAULT_LAYER + 2)

        pane.add(enemyImageLabel, JLayeredPane.DEFAULT_LAYER + 1)

        pane.add(locationImageLabel)

        pane.add(healthBar)
        pane.add(healthBarLabel, JLayeredPane.DEFAULT_LAYER + 1)

        pane.add(playerStatsLabel)
        pane.add(healthLabel)
        pane.add(healthValueLabel)
        pane.add(speedLabel)
        pane.add(speedValueLabel)
        pane.add(dodgeLabel)
        pane.add(dodgeValueLabel)
        pane.add(cardsLabel)
        pane.add(cardsCountLabel)

        pane.add(enemyBar)
        pane.add(enemyBarLabel, JLayeredPane.DEFAULT_LAYER + 1)

        pane.add(buttonA)
        pane.add(buttonB)

        pane.add(gameLogArea)

        pane.add(cardArea)
    }

    private fun setupStyles() { //styles elements for aesthetic purposes
        locationLabel.horizontalAlignment = SwingConstants.CENTER
        locationLabel.font = Font("SANS_SERIF", Font.BOLD, 20)
        locationLabel.setOpaque(true)
        locationLabel.background = UIManager.getColor("Panel.background")

        locationImageLabel.icon = locationIcon

        enemyImageLabel.horizontalAlignment = SwingConstants.CENTER
        enemyImageLabel.verticalAlignment = SwingConstants.CENTER
        enemyImageLabel.icon = enemyIcon

        healthBar.value = 100
        healthBar.foreground = Color.RED

        healthBarLabel.horizontalAlignment = SwingConstants.CENTER
        healthBarLabel.font = Font("SANS_SERIF", Font.BOLD, 20)

        playerStatsLabel.font = Font("SANS_SERIF", Font.BOLD, 20)
        playerStatsLabel.horizontalAlignment = SwingConstants.CENTER

        healthValueLabel.font = Font("SANS_SERIF", Font.BOLD, 16)
        healthValueLabel.horizontalAlignment = SwingConstants.RIGHT

        dodgeValueLabel.font = Font("SANS_SERIF", Font.BOLD, 16)
        dodgeValueLabel.horizontalAlignment = SwingConstants.RIGHT

        speedValueLabel.font = Font("SANS_SERIF", Font.BOLD, 16)
        speedValueLabel.horizontalAlignment = SwingConstants.RIGHT

        cardsLabel.font = Font("SANS_SERIF", Font.BOLD, 20)
        cardsLabel.horizontalAlignment = SwingConstants.CENTER

        cardsCountLabel.font = Font("SANS_SERIF", Font.BOLD, 50)
        cardsCountLabel.horizontalAlignment = SwingConstants.CENTER

        enemyBar.foreground = Color.RED
        enemyBar.isVisible = false

        enemyBarLabel.horizontalAlignment = SwingConstants.CENTER
        enemyBarLabel.font = Font("SANS_SERIF", Font.BOLD, 20)

        gameLogText.border = BorderFactory.createLineBorder(Color.DARK_GRAY, 2)
        gameLogText.font = Font("SANS_SERIF", Font.PLAIN, 10)
        gameLogText.isEditable = false
        gameLogText.caretPosition = gameLogText.document.length

        cardArea.horizontalAlignment = SwingConstants.CENTER
        cardArea.border = BorderFactory.createLineBorder(Color.RED, 5)
        cardArea.font = Font("SANS_SERIF", Font.BOLD, 40)
    }

    private fun setupWindow() { //setup of window properties
        frame.isResizable = false                           // Can't resize
        frame.contentPane = pane                            // Define the main content
        frame.isUndecorated = true
        frame.isAlwaysOnTop = true
        frame.pack()
        frame.setLocation(
            ((Toolkit.getDefaultToolkit().screenSize.width - frame.width) / 2),
            0
        )              // Centre on the screen
        windowLocation = frame.location
    }

    private fun setupActions() { //setup of action listeners
        quitButton.addActionListener { //quit option, gets confirm dialog
            if (JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to Quit?",
                    "Quit",
                    JOptionPane.YES_NO_OPTION
                ) == 0
            ) {
                exitProcess(0)
            }
        }

        buttonA.addActionListener { handleA() } //left (A) game button
        buttonB.addActionListener { handleB() } //right (B) game button

        //Timer Listeners
        enemyDelayTimer.addActionListener { handleEnemyTurn() }
        battleEndTimer.addActionListener { handleBattleEnd() }

        frame.addComponentListener(object : ComponentAdapter() { //Updates Window Location When Moved
            override fun componentMoved(e: ComponentEvent?) {
                windowLocation = frame.location
            }
        })
    }

    private fun updateUI() { //updates UI elements
        /**
         * Update Consistent UI elements
         */

        locationLabel.text = game.location.name
        locationIcon = ImageIcon(game.location.icon)
        locationImageLabel.icon = locationIcon

        cardArea.isVisible = (game.phase == GamePhase.PLAYER_TURN)

        gameLogText.text = null
        for (line in game.gameLog) {
            gameLogText.append("$line\n")
        }
        gameLogText.text = gameLogText.text
        gameLogText.caretPosition = gameLogText.text.length

        healthBar.maximum = game.maxHealth
        healthBar.value = game.health

        healthValueLabel.text = "${game.health}/${game.maxHealth}"
        speedValueLabel.text = "${game.speed}"
        dodgeValueLabel.text = "${game.dodgeChance}%"
        cardsCountLabel.text = "${game.hand.size}"

        enemyBar.maximum = game.enemy.maxHealth
        enemyBar.value = game.enemy.health

        /**
         * Update Variable UI elements based on game phase
         */

        when (game.phase) {

            GamePhase.TUTORIAL -> {
                buttonA.text = "Back"
                buttonA.isEnabled = (tutorialPosition > 0)
                buttonB.text = "Next"
                buttonB.isEnabled = true
            }

            GamePhase.TRAVEL -> {
                buttonA.text = "NW"
                buttonA.isEnabled = true
                buttonB.text = "NE"
                buttonB.isEnabled = true

                enemyBar.isVisible = false
                enemyBarLabel.isVisible = false

                enemyImageLabel.icon = null
            }

            GamePhase.BATTLE_START -> {
                buttonA.text = "Fight"
                buttonB.text = "Flee"

                buttonA.isEnabled = true
                buttonB.isEnabled = true

                enemyBarLabel.text = game.enemy.name
                enemyBarLabel.isVisible = true

                enemyBar.isVisible = true

                enemyIcon = ImageIcon(game.enemy.icon)
                enemyImageLabel.icon = enemyIcon
            }

            GamePhase.PLAYER_TURN -> {
                buttonA.isEnabled = false
                buttonB.isEnabled = true

                enemyIcon = ImageIcon(game.enemy.icon)
                enemyImageLabel.icon = enemyIcon

                cardArea.isVisible = true
            }

            GamePhase.ENEMY_TURN -> {
                buttonA.isEnabled = false
                buttonB.isEnabled = false

                enemyIcon = ImageIcon(game.enemy.icon)
                enemyImageLabel.icon = enemyIcon

                cardArea.isVisible = false
            }

            GamePhase.ENDING -> {
                buttonA.isEnabled = false
                buttonB.isEnabled = false

                enemyBar.isVisible = false

                enemyIcon = ImageIcon(game.enemy.icon)
                enemyImageLabel.icon = enemyIcon
            }

            else -> {}
        }
    }

    fun show() { //shows the window
        frame.isVisible = true
    }

    private fun handleA() { //handles the interaction of the left (A) game button, based on game state
        when (game.phase) {
            GamePhase.INTRO -> { //Tutorial
                game.log("----------\n> Yes please")
                game.phase = GamePhase.TUTORIAL
                stepTutorial()
                updateUI()
            }

            GamePhase.TUTORIAL -> { //Back
                stepDownTutorial()
                updateUI()
            }

            GamePhase.TRAVEL -> { //NW
                game.travel('A')
                game.phase = GamePhase.BATTLE_START
                updateUI()
            }

            GamePhase.BATTLE_START -> { //Fight
                updateUI()
            }

            else -> {}
        }
    }

    private fun handleB() { //handles the interaction of the right (B) game button, based on game state
        when (game.phase) {
            GamePhase.INTRO -> { //Skip Tutorial
                game.log("----------\n> I can handle myself\n----------")
                game.log("Very well then, take these cards as a gift,\n use them to defend yourself if you are in danger")
                game.hand.add(CardWindow(this, game, game.drawRandomNormalCard(), 0))
                game.hand.add(CardWindow(this, game, game.drawRandomNormalCard(), 30))
                game.phase = GamePhase.TRAVEL
                updateUI()
            }

            GamePhase.TUTORIAL -> { //Next
                stepTutorial()
                updateUI()
            }

            GamePhase.TRAVEL -> { //NE
                game.travel('B')
                game.phase = GamePhase.BATTLE_START
                updateUI()
            }

            GamePhase.PLAYER_TURN, GamePhase.BATTLE_START -> { //Flee
                game.log("You Tried To Run Away")
                if (game.tryFlee()) {
                    game.log("You Escaped!")

                    game.phase = GamePhase.TRAVEL
                    updateUI()
                } else {
                    game.log("${game.enemy.name} Caught Up With You!")
                    updateUI()
                    enemyDelayTimer.restart()
                }
            }

            else -> {}

        }
    }

    private fun stepTutorial() { //shows the next section of the tutorial
        tutorialPosition += 1
        if (tutorialPosition < 4) {
            game.log("----------")
            game.log(game.tutorialText[tutorialPosition])
        } else { //if the next tutorial section is the end of the list start game
            game.log("----------")
            game.log("That is all the advice I have for your journey,\ntake these cards, it is dangerous to go without any.\nGood Luck!")
            game.log("----------")
            game.hand.add(CardWindow(this, game, game.drawRandomNormalCard(), 0))
            game.hand.add(CardWindow(this, game, game.drawRandomNormalCard(), 30))
            game.phase = GamePhase.TRAVEL
            updateUI()
        }

    }

    private fun stepDownTutorial() { //shows the previous section of the tutorial
        tutorialPosition -= 1
        game.log("----------")
        game.log(game.tutorialText[tutorialPosition])
    }

    fun handleCardPlaced(wasDamaged: Boolean) { //handle window events after card is placed
        if (wasDamaged) shakeEnemy() //shake enemy if damaged
        if (game.checkWin()) { //check if enemy was defeated, if not, move to enemy turn
            game.log("#--------------------")
            game.log("  You defeated ${game.enemy.name}")
        } else {
            game.phase = GamePhase.ENEMY_TURN
            updateUI()
            enemyDelayTimer.restart()
        }
    }

    private fun handleEnemyTurn() { //runs through enemy turn
        enemyDelayTimer.stop()

        if (game.enemyTurn(game)) { //runs enemy turn, returns true if loss
            game.log("#--------------------")
            game.log("  You were defeated")
            game.log("#--------------------")
        } else if (game.checkWin()) { //check if enemy defeated
            game.log("#--------------------")
            game.log("  You defeated ${game.enemy.name}")
            battleEndTimer.restart()
        } else { //move to player turn
            game.phase = GamePhase.PLAYER_TURN
        }
        updateUI()
    }

    private fun handleBattleEnd() { //checks for ending and then rewards cards for defeating the enemy
        battleEndTimer.stop()
        if (game.location.name == "The Lost Realm") handleEnding() //if win on last area, begin ending
        else {
            game.phase = GamePhase.TRAVEL
            if (game.location.dropsLegendayCards) { //card rewards
                game.hand.add(CardWindow(this, game, game.drawRandomLegendaryCard(), 0))
            } else {
                game.hand.add(CardWindow(this, game, game.drawRandomNormalCard(), 0))
                game.hand.add(CardWindow(this, game, game.drawRandomNormalCard(), 30))
            }
            game.resetStats()
            updateUI()
        }
    }

    private fun handleEnding() { //TODO
        game.phase = GamePhase.ENDING
        game.enemy = game.map[0][0].possibleEnemies[0]
        for (cardWindow in game.hand) {
            cardWindow.discard()
        }
        updateUI()
    }

    private fun shakeEnemy() { //shakes the enemy image
        val originalLocation = enemyImageLabel.location
        val startTime = System.currentTimeMillis()
        val shakeDistance = 25
        val shakeDuration = 500
        val shakeCycle = 50

        val timer = Timer(shakeCycle) {
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed > shakeDuration) {
                enemyImageLabel.location = originalLocation
                (it.source as Timer).stop()
            } else {
                val offset = (sin(elapsed.toDouble() * 0.1) * shakeDistance).toInt()
                enemyImageLabel.location = Point(originalLocation.x + offset, originalLocation.y)
            }
        }
        timer.start()
    }
}

/**
 * Card UI window, handles movement, UI updates, ect...
 */
class CardWindow(val owner: MainWindow, val game: Game, var card: Card, private var initialOffsetY: Int) {
    val frame = JFrame(card.name)
    private val panel = JPanel().apply { layout = null }

    private val effectText = mapOf("DAMAGE" to "Damage", "HEAL" to "Health", "ACCELERATE" to "Speed")

    private val cardImageLabel = JLabel()
    private val intensityLabel = JLabel("+${card.intensity}")
    private val effectLabel = JLabel()


    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
        frame.isVisible = true
    }

    private fun setupLayout() { //sets locations and areas of each element and then adds them to the pane
        panel.preferredSize = Dimension(200, 300) // the window header is an extra 30px tall

        cardImageLabel.setBounds(10, 10, 180, 180)
        intensityLabel.setBounds(10, 200, 180, 40)
        effectLabel.setBounds(10, 250, 180, 50)

        panel.add(cardImageLabel)
        panel.add(intensityLabel)
        panel.add(effectLabel)
    }

    private fun setupStyles() { //styles elements for aesthetic purposes
        cardImageLabel.icon = ImageIcon(card.icon)

        intensityLabel.horizontalAlignment = SwingConstants.CENTER
        intensityLabel.font = Font("SANS_SERIF", Font.BOLD, 20)

        effectLabel.horizontalAlignment = SwingConstants.CENTER
        effectLabel.font = Font("SANS_SERIF", Font.BOLD, 30)
    }

    private fun setupActions() { //setup of action listeners
        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(e: ComponentEvent?) {
                val location = frame.location
                //Check if card is in place area
                if (game.phase == GamePhase.PLAYER_TURN || game.phase == GamePhase.BATTLE_START) {
                    val placeLocation =
                        Point(owner.windowLocation.x + owner.placeArea.x, owner.windowLocation.y + owner.placeArea.y)
                    if (location.x in (placeLocation.x - 10..placeLocation.x + 10) &&
                        location.y in (placeLocation.y - 10..placeLocation.y + 10)
                    ) {
                        frame.isEnabled = false
                        frame.isEnabled = true
                        play()
                    }
                }
            }
        })
    }

    private fun setupWindow() { //setup of window properties
        frame.isEnabled = true
        frame.isResizable = false                           // Can't resize
        frame.contentPane = panel                           // Define the main content
        frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        frame.isAlwaysOnTop = true
        frame.pack()
        frame.setLocation((owner.frame.x + 10), (owner.frame.y + owner.frame.height + 10 + initialOffsetY))
    }

    private fun updateUI() {
        frame.title = card.name
        cardImageLabel.icon = ImageIcon(card.icon)

        if (card.legendary) {
            intensityLabel.isVisible = false

            effectLabel.text = "???"
            effectLabel.foreground = Color.YELLOW
        } else {
            intensityLabel.text = "+${card.intensity}"
            intensityLabel.isVisible = true
            
            effectLabel.text = effectText[card.effect.name]
        }
    }

    fun play() {
        //Card is placed
        if (card.playCard(game, true)) discard()
        else redraw(0)
        owner.handleCardPlaced((card.effect == CardEffect.DAMAGE))

    }

    fun discard() {
        frame.dispose()
    }

    fun redraw(offsetY: Int) {
        card = game.drawRandomNormalCard()
        updateUI()
        frame.setLocation(
            (owner.frame.location.x + owner.frame.width - 220),
            (owner.frame.location.y + owner.frame.height + 10 + offsetY)
        )
    }
}


class Location(
    val name: String,
    image: String,
    val possibleEnemies: MutableList<Enemy>,
    val dropsLegendayCards: Boolean
) {
    val icon = ClassLoader.getSystemResource("images/locations/$image")!!
}

class Card(
    val name: String,
    image: String,
    val effect: CardEffect,
    var intensity: Int,
    val legendary: Boolean
) {
    val icon = ClassLoader.getSystemResource("images/cards/$image")!!

    fun playCard(game: Game, isPlayer: Boolean): Boolean {
        game.log("----------")
        game.log("You played card: $name")
        when (effect) {
            CardEffect.DAMAGE -> {
                if (isPlayer) {
                    game.enemy.health -= intensity
                    game.log("${game.enemy.name} took $intensity Damage")
                } else {
                    game.health -= intensity
                    game.log("You took $intensity Damage")
                }
            }

            CardEffect.HEAL -> {
                if (isPlayer) {
                    game.health += intensity
                    if (game.health > game.maxHealth) game.health = game.maxHealth
                    game.log("You healed $intensity Health")
                } else {
                    game.enemy.health += intensity
                    if (game.enemy.health > game.enemy.maxHealth) game.enemy.health = game.enemy.maxHealth
                    game.log("${game.enemy.name} healed $intensity Health")
                }
            }

            CardEffect.ACCELERATE -> {
                if (isPlayer) {
                    game.speed += intensity
                    game.log("You got faster")
                } else {
                    game.enemy.speed += intensity
                    game.log("${game.enemy.name} got faster")
                }
            }

            //Unique Legendary Effects
            CardEffect.MIRACLE -> {
                game.maxHealth = intensity
                game.health = intensity
                game.speed += 5
                game.dodgeChance += 15

                game.log("Your Maximum Health increased")
                game.log("You healed $intensity Health")
                game.log("Your Speed increased")
                game.log("Your Dodge skill increased")
            }

            CardEffect.DODGE -> {
                game.dodgeChance += intensity

                game.log("Your Dodge Skill increased")
            }

            CardEffect.POISON -> {
                game.enemy.poison += 1

                game.log("${game.enemy.name} was Poisoned")
            }
        }
        if (legendary) game.legendaryCount--
        return legendary
    }

}

class Enemy(
    val name: String,
    image: String,
    var maxHealth: Int,
    var attack: IntRange,
    var speed: Int,
    val attackName: String,
    val specialAbility: SpecialAbility?
) {
    val icon = ClassLoader.getSystemResource("images/enemies/$image")!!
    var health = maxHealth
    var poison = 0
}

class SpecialAbility(private val name: String, private val effect: EnemySpecial) {
    fun doSpecialAttack(game: Game) {
        game.log("----------")
        game.log("${game.enemy.name} used special ability: $name")
        when (effect) {
            EnemySpecial.HEAL -> {
                val intensity = (3..8).random()
                game.enemy.health += intensity
                game.log("${game.enemy.name} healed $intensity health!")
            }

            EnemySpecial.ACCELERATE -> {
                game.enemy.speed++
                game.log("${game.enemy.name} got faster!")
            }

            EnemySpecial.POWER_UP -> {
                game.enemy.attack = game.enemy.attack.min() + 3..game.enemy.attack.max() + 3
                game.log("${game.enemy.name} got stronger!")
            }

            EnemySpecial.DESTROY_PLANET -> {
                game.log("${game.enemy.name} destroyed a distant planet!")
                game.log("There is now one less light in the night sky")
            }

            EnemySpecial.FREEZE -> {
                if (game.tryHit(true)) {
                    val cardWindow = game.getRandomNormalCardFromHand()
                    cardWindow.frame.isEnabled = false
                    game.log("${game.enemy.name} stunned card: ${cardWindow.card.name}")
                } else {
                    game.log("${game.enemy.name} missed!")
                }
            }

            EnemySpecial.DEVOUR -> {
                if (game.tryHit(true)) {
                    val cardWindow = game.getRandomNormalCardFromHand()
                    game.log("${game.enemy.name} devoured card: ${cardWindow.card.name}")
                    cardWindow.discard()
                    game.log("${game.enemy.name} grew ${game.enemy.attack.min()} more tentacles")
                    game.enemy.attack = game.enemy.attack.min() * 2..game.enemy.attack.max() * 2
                } else {
                    game.log("${game.enemy.name} missed!")
                }
            }

            EnemySpecial.POWER_STEAL -> {
                if (game.tryHit(false)) {
                    val cardWindow = game.getRandomNormalCardFromHand()
                    game.log("${game.enemy.name} used your card: ${cardWindow.card.name}")
                    cardWindow.card.playCard(game, false)
                    cardWindow.redraw(30)
                } else {
                    game.log("${game.enemy.name} missed!")
                }
            }
        }
    }
}