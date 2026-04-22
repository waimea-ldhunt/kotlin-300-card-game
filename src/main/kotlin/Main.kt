import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.*
import java.awt.event.*
import javax.swing.*
import kotlin.math.sin

fun ImageIcon.scaled(width: Int, height: Int): ImageIcon =
    ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH))

/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()          // Initialise the LAF

    val game = Game()                 // Get an app state object
    val window = MainWindow(game)    // Spawn the UI, passing in the app state

    SwingUtilities.invokeLater { window.show() }

    val card1 = CardWindow(window,game,game.deck[0])
    val card2 = CardWindow(window,game,game.deck[1])

    game.hand.add(card1.card)
    game.hand.add(card2.card)
}


/**
 * Manage app state
 *
 * @property distanceTravelled the user's name
 * @property health the points earned
 */
class Game {
    private var distanceTravelled = 0
    var maxHealth = 100
    var health = maxHealth
    var speed = 5
    var dodgeChance = 20

    val gameLog = mutableListOf<String>()

    var phase = 'I' //[I]ntro, [T]ravel, [P]layer Turn [E]nemy Turn [O]utro
    var isPlayerTurn = false

    val hand = mutableListOf<Card>()
    var legendaryCount = 0

    lateinit var location: Location
    lateinit var enemy: Enemy

    val deck = mutableListOf<Card>()
    private val map = mutableListOf<List<Location>>()

    init {
        setupGame()
    }

    private fun setupGame() {
        /*
        * SETTING UP CARDS
        * */

        val axe = Card("Axe", "axe.png", "Damage", 7, false)
        val sword = Card("Sword", "placeholder.png", "Damage", 10, false)
        val bomb = Card("Bomb", "placeholder.png","Damage", 25, false)

        val weakHealingPotion = Card("Lesser Potion of Healing","placeholder.png","Heal", 30, false)
        val strongHealingPotion = Card("Greater Potion of Healing","placeholder.png","Heal", 50, false)

        val miraclePotion = Card("Lesser Potion of Miracles", "placeholder.png", "Miracle", 100, true)
        val dodgeBook = Card("The art of Dodging", "placeholder.png", "Dodge", 70, true)
        val nuke = Card("Nuclear Bomb", "placeholder.png", "Damage", 100, true)

        deck.add(axe)
        deck.add(sword)
        deck.add(bomb)
        deck.add(weakHealingPotion)

        /*
        * SETTING UP ENEMIES
        * */

        val stranger = Enemy("Stranger","stranger.png",365,10..10,999, "Donate", null)

        val ant = Enemy("Resilient Ant","ant.png",10,2..4,10,"Bite", null)

        val walkingFish = Enemy("Walking Fish","walkingFish.png",70,6..9,5,"Fish Slap",SpecialAbility("Slippery","Accelerate"))



        val leo = Enemy("Leo","leo.png",30,4..8,5, "Pounce", SpecialAbility("Roar", "Heal+Power Up"))
        val redOx = Enemy("Red Ox","redox.png",45, 8..12, 4, "Reduce", SpecialAbility("Oxidise","Strong Heal"))

        val kraken = Enemy("Giant Squid", "kraken.png",130,2..2, 2, "Tentacle Rush", SpecialAbility("Devour","Devour"))
        val boss = Enemy("Memory Thief","thief.gif",365, 9..11,999, "Distort", SpecialAbility("Power Steal","Power Steal"))

        enemy = stranger

        /*
        * SETTING UP LOCATIONS
        * */

        val village = Location("The Village", "village.png","", mutableListOf(stranger))
        map.add(listOf(village))

        //Temperate biomes (2 out of 3 are visible)
        val meadow = Location("Sunlit Meadow", "meadow.png","", mutableListOf(ant))
        val forest = Location("Deep Forest", "forest.png","", mutableListOf(ant))
        val river = Location("Rushing River", "river.png","", mutableListOf(ant))
        map.add(listOf(meadow,forest,river).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Cold biomes (2 out of 3 are visible)
        val mountains = Location("Winter Hills", "mountains.png","", mutableListOf())
        val snowForest = Location("Snowy Forest", "snow-forest.png","", mutableListOf())
        val summit = Location("Frosted Peak", "summit.png","", mutableListOf())
        map.add(listOf(snowForest,mountains,summit).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Wet biomes (2 out of 3 are visible)
        val jungle = Location("Humid Jungle", "jungle.png","", mutableListOf())
        val wetland = Location("Muddy Wetland", "wetland.png","", mutableListOf(walkingFish))
        val lake = Location("Serene Lake", "lake.png","", mutableListOf(walkingFish))
        map.add(listOf(jungle, wetland, lake).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Warm biomes (2 out of 3 are visible)
        val beach = Location("Sunset Beach", "beach.png","", mutableListOf())
        val desert = Location("Burning Desert", "desert.png","", mutableListOf())
        val canyon = Location("Scorched Gorge", "canyon.png","", mutableListOf())
        map.add(listOf(beach, desert, canyon).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //The first of the mysterious lands, with a boss fight
        val foggySea = Location("Foggy Sea", "sea.png","", mutableListOf(kraken))
        map.add(listOf(foggySea,foggySea)) //player can only travel to this location

        //The second of the mysterious lands
        val strangeLand = Location("Strange Land", "land.png","", mutableListOf(leo,redOx))
        map.add(listOf(strangeLand,strangeLand)) //player can only travel to this location

        //The final of the mysterious lands, holding the final boss fight
        val lostRealm = Location("The Lost Realm", "lost-realm.png","", mutableListOf(boss))
        map.add(listOf(lostRealm,lostRealm)) //player can only travel to this location

        location = village

        log("Welcome to THE GAME")
    }

    fun travel(choice: Char) {
        when (choice) {
            'A' -> location = map[(distanceTravelled/100)+1][0]
            'B' -> location = map[(distanceTravelled/100)+1][1]
        }

        phase = 'S'
        distanceTravelled += 100
        enemy = location.possibleEnemies[location.possibleEnemies.indices.random()]
    }

    fun log(str: String) {
        gameLog.add(str)
    }

    fun getRandomNLCard(cards: List<Card>): Card{
        while (true) {
            val card = cards[cards.indices.random()]
            if (card.legendary) continue
            else return card
        }
    }

    fun enemyTurn(game: Game) {
        log("${enemy.name} uses attack ${enemy.attackName}")
        val damage = enemy.attack.random()
        health -= damage
        log("You Took $damage Damage")
        checkLoss()
        enemy.specialAbility?.doSpecialAttack(game)
        checkLoss()
        phase = 'P'
        isPlayerTurn = true
    }

    fun tryFlee(): Boolean {
        return ((1..100).random() > ((enemy.speed/speed) * 100))
    }

    fun checkLoss() {
        if (health <= 0) {}
    }

    fun checkWin() {
        if (enemy.health <= 0) {}
    }
}


/**
 * Main UI window, handles user clicks, etc.
 *
 * @param game the game state object
 */
class MainWindow(val game: Game) {
    val frame = JFrame("WINDOW TITLE")
    private val pane = JLayeredPane().apply { layout = null }

    private val locationLabel = JLabel(game.location.name)
    private val locationImageLabel = JLabel()
    private var locationIcon = ImageIcon(game.location.icon)

    private val enemyImageLabel = JLabel()
    private var enemyIcon = ImageIcon(game.enemy.icon)

    private val healthBar = JProgressBar(0, 100)
    private val healthBarLabel = JLabel("Health")

    private val enemyBar = JProgressBar(0, 100)
    private val enemyBarLabel = JLabel("Enemy Health")

    private val buttonA = JButton("Tutorial")
    private val buttonB = JButton("Skip")

    private val gameLogText = JTextArea()

    private val cardArea = JLabel("PLACE")

    var windowLocation = frame.location

    val placeArea = Point(1390, 80)

    private val turnTimer = Timer(1000, null)

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        pane.preferredSize = Dimension(1600, 420)

        locationLabel.setBounds(1110, 70, 280, 30)

        locationImageLabel.setBounds(500, 10, 600, 400)

        enemyImageLabel.setBounds(500, 10, 600, 400)

        healthBar.setBounds(10, 10, 480, 50)
        healthBarLabel.setBounds(10, 10, 480, 50)

        enemyBar.setBounds(1110, 10, 480, 50)
        enemyBarLabel.setBounds(1110, 10, 480, 50)

        buttonA.setBounds(10, 200, 230, 50)
        buttonB.setBounds(250, 200, 230, 50)

        gameLogText.setBounds(1110, 120, 280, 290)

        cardArea.setBounds(1390, 80, 200, 330)

        pane.add(locationLabel)
        pane.add(enemyImageLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(locationImageLabel)
        pane.add(healthBar)
        pane.add(healthBarLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(enemyBar)
        pane.add(enemyBarLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(buttonA)
        pane.add(buttonB)
        pane.add(gameLogText)
        pane.add(cardArea)
    }

    private fun setupStyles() {
        locationLabel.horizontalAlignment = SwingConstants.CENTER
        locationLabel.font = Font("SANS_SERIF", Font.BOLD, 20)

        locationImageLabel.icon = locationIcon

        enemyImageLabel.horizontalAlignment = SwingConstants.CENTER
        enemyImageLabel.verticalAlignment = SwingConstants.CENTER
        enemyImageLabel.icon = enemyIcon

        healthBar.value = 100
        healthBar.foreground = Color.RED

        healthBarLabel.horizontalAlignment = SwingConstants.CENTER
        healthBarLabel.font = Font("SANS_SERIF", Font.BOLD, 20)

        enemyBar.value = 100
        enemyBar.foreground = Color.RED
        enemyBar.isVisible = false

        enemyBarLabel.horizontalAlignment = SwingConstants.CENTER
        enemyBarLabel.font = Font("SANS_SERIF", Font.BOLD, 20)
        enemyBarLabel.isVisible = false

        gameLogText.border = BorderFactory.createLineBorder(Color.DARK_GRAY, 2)
        gameLogText.font = Font("SANS_SERIF", Font.PLAIN, 10)
        gameLogText.isEditable = false
        gameLogText.caretPosition = gameLogText.document.length

        cardArea.horizontalAlignment = SwingConstants.CENTER
        cardArea.border = BorderFactory.createLineBorder(Color.RED, 5)
        cardArea.font = Font("SANS_SERIF", Font.BOLD, 40)
    }

    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.contentPane = pane                            // Define the main content
        frame.isUndecorated = true
        frame.pack()
        frame.setLocation(((Toolkit.getDefaultToolkit().screenSize.width - frame.width) / 2),0)              // Centre on the screen
        windowLocation = frame.location
    }

    private fun setupActions() {
        buttonA.addActionListener { handleA() }
        buttonB.addActionListener { handleB() }

        turnTimer.addActionListener { handleEnemyTurn() }

        //Updates Window Location When Moved
        frame.addComponentListener(object: ComponentAdapter(){
            override fun componentMoved(e: ComponentEvent?) {
                windowLocation = frame.location
            }
        })
    }


    private fun updateUI() {
        locationLabel.text = game.location.name
        locationIcon = ImageIcon(game.location.icon)
        locationImageLabel.icon = locationIcon

        cardArea.isVisible = game.isPlayerTurn

        gameLogText.text = null
        for (line in game.gameLog) {
            gameLogText.append("$line\n")
        }

        healthBar.maximum = game.maxHealth
        healthBar.value = game.health

        enemyBar.maximum = game.enemy.maxHealth
        enemyBar.value = game.enemy.health

        when (game.phase) {
            'T' -> {
                buttonA.text = "NW"
                buttonA.isEnabled = true
                buttonB.text = "NE"
                buttonB.isEnabled = true

                enemyBar.isVisible = false
                enemyBarLabel.isVisible = false

                enemyImageLabel.icon = null
            }
            'S' -> {
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
            'P' -> {
                buttonA.isEnabled = false
                buttonB.isEnabled = true

                enemyIcon = ImageIcon(game.enemy.icon)
                enemyImageLabel.icon = enemyIcon

                cardArea.isVisible = true
            }
            'E' -> {
                buttonA.isEnabled = false
                buttonB.isEnabled = false

                enemyIcon = ImageIcon(game.enemy.icon)
                enemyImageLabel.icon = enemyIcon
            }
        }
    }

    fun show() {
        frame.isVisible = true
    }

    private fun handleA() {
        when (game.phase) {
            'I' -> {
                game.phase = 'T'
                updateUI()
            }

            'T' -> {
                game.travel('A')
                updateUI()
            }

            'S' -> { //Fight
                game.isPlayerTurn = true
                updateUI()
            }

        }
    }

    private fun handleB() {
        when (game.phase) {
            'I' -> {
                game.phase = 'T'
                updateUI()
            }

            'T' -> {
                game.travel('B')
                updateUI()
            }

            'P','S' -> { //Flee
                if (game.tryFlee()) {
                    updateUI()
                    game.phase = 'T'
                    updateUI()
                } else {
                    turnTimer.restart()
                }
            }

        }
    }

    fun handleCardPlaced(wasDamaged: Boolean) {
        if (wasDamaged) shakeEnemy()
        game.isPlayerTurn = false
        game.phase = 'E'
        updateUI()
        turnTimer.restart()
    }

    private fun handleEnemyTurn() {
        turnTimer.stop()
        game.enemyTurn(game)
        updateUI()
    }

    private fun shakeEnemy() {
        val originalLocation = enemyImageLabel.location
        val startTime = System.currentTimeMillis()
        val shakeDistance = 10
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
 * Info UI window is a child dialog and shows how the
 * app state can be shown / updated from multiple places
 *
 * @param owner the parent frame, used to position and layer the dialog correctly
 * @param game the app state object
 */

class CardWindow(val owner: MainWindow, val game: Game, var card: Card) {
    val frame = JFrame(card.name)
    val panel = JPanel().apply { layout = null }

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
        frame.isVisible = true
    }

    fun setupLayout(){
        panel.preferredSize = Dimension(200, 300) // the window header is an extra 30px tall
    }

    fun setupStyles(){

    }

    fun setupActions(){
        frame.addComponentListener(object: ComponentAdapter(){
            override fun componentMoved(e: ComponentEvent?) {
                val location = frame.location
                //Check if card is in place area
                if (game.isPlayerTurn) {
                    val placeLocation = Point(owner.windowLocation.x + owner.placeArea.x,owner.windowLocation.y + owner.placeArea.y)
                    if (location.x in (placeLocation.x-5..placeLocation.x+5) &&
                        location.y in (placeLocation.y-5..placeLocation.y+5)) {
                        frame.isEnabled = false
                        frame.setLocation((owner.frame.location.x + 1400),(owner.frame.location.y + 470))
                        frame.isEnabled = true
                        play()
                    }
                }



            }
        })
    }

    fun setupWindow(){
        frame.isEnabled = true
        frame.isResizable = false                           // Can't resize
        frame.contentPane = panel                           // Define the main content
        frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        frame.pack()
        frame.setLocation(((Toolkit.getDefaultToolkit().screenSize.width - frame.width) / 2),(owner.frame.getLocation().y + 460))
    }

    fun updateUI(){
        frame.title = card.name
        for (card in game.hand) {
            print(card.name)
        }
    }

    fun play(){
        //Card is placed
        game.hand.remove(card)
        if (card.playCard(game)) frame.dispose() else {
            card = game.getRandomNLCard(game.deck)
        }
        game.checkWin()
        game.hand.add(card)
        updateUI()
        val isAttack = when (card.effect) {
            "Damage" -> true
            else -> false
        }
        owner.handleCardPlaced(isAttack)
    }
}



class Location(val name: String, private val image: String, val description: String, val possibleEnemies: MutableList<Enemy>) {
    val icon = ClassLoader.getSystemResource("images/locations/$image")
}

class Card(val name: String, private val image: String, val effect: String, private var intensity: Int, val legendary: Boolean) {
    val icon = ClassLoader.getSystemResource("images/cards/$image")

    fun playCard(game: Game): Boolean {
        game.log("You played card: $name")
        when (effect) {
            "Damage" -> {
                game.enemy.health -= intensity

                game.log("${game.enemy.name} Took $intensity Damage")
            }
            "Heal" -> {
                game.health += intensity
                if (game.health > game.maxHealth) game.health = game.maxHealth

                game.log("You Healed $intensity Health")
            }

            //Unique Legendary Effects
            "Miracle" -> {
                game.health += intensity
                game.maxHealth += intensity/2
                if (game.health > game.maxHealth) game.health = game.maxHealth
                game.speed += 5

                game.log("You Healed $intensity Health")
                game.log("Your Maximum Health Increased")
                game.log("Your Speed Increased")
            }

            "Dodge" -> {
                game.dodgeChance += intensity

                game.log("Your Dodge Skill Increased")
            }
        }
        if (legendary) game.legendaryCount --
        return legendary
    }

}

class Enemy(val name: String, private val image: String, var maxHealth: Int, var attack: IntRange, var speed: Int, val attackName: String, val specialAbility: SpecialAbility?) {
    val icon = ClassLoader.getSystemResource("images/enemies/$image")
    var health = maxHealth
}

class SpecialAbility(private val name: String, private val effects: String){
    fun doSpecialAttack(game: Game) {
        game.log("${game.enemy.name} used special ability: $name")
        val effectsList = effects.split("+")
        for (effect in effectsList){
            when (effect) {
                "Heal" -> {
                    val intensity = (3..8).random()
                    game.enemy.health += intensity
                    game.log("${game.enemy.name} healed $effect health!")

                }

                "Strong Heal" -> {
                    val intensity = (8..14).random()
                    game.enemy.health += intensity
                    game.log("${game.enemy.name} healed $effect health!")
                }

                "Accelerate" -> {
                    game.enemy.speed += 3
                    game.log("${game.enemy.name} got faster!")
                }

                "Power Up" -> {
                    game.enemy.attack = game.enemy.attack.min()+3..game.enemy.attack.max()+3
                    game.log("${game.enemy.name} got stronger!")
                }

                "Devour" -> {

                }

                "Power Steal" -> {

                }
            }
        }
    }
}