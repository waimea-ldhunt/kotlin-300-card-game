import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.sun.java.accessibility.util.AWTEventMonitor.addComponentListener
import com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener
import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.BorderFactory.createLineBorder
import kotlin.math.max
import kotlin.random.Random

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

    val card = CardWindow(window,game,game.deck[0])
}


/**
 * Manage app state
 *
 * @property name the user's name
 * @property score the points earned
 */
class Game {
    var distanceTravelled = 0
    var health = 100
    var dodgeChance = 20

    val gameLog = mutableListOf<String>()

    var phase = 'I' //[I]ntro, [T]ravel, [B]attle
    var isPlayerTurn = false

    val hand = mutableListOf<Card>()
    var legendaryCount = 0

    lateinit var location: Location
    lateinit var enemy: Enemy

    val deck = mutableListOf<Card>()
    val map = mutableListOf<List<Location>>()
    val enemies = mutableListOf<Enemy>()

    init {
        setupGame()
    }

    fun setupGame() {
        /*
        * SETTING UP CARDS
        * */

        val axe = Card("Axe", "axe.png", "Damage", 7, false)
        val dodgeBook = Card("The art of Dodging", "axe.png", "Dodge", 70, true)

        deck.add(axe)

        /*
        * SETTING UP ENEMIES
        * */

        val stranger = Enemy("Stranger","kraken.png",999,999,999, "", null)                               //0
        val leo = Enemy("Leo","leo.png",100,10,5, "Pounce", SpecialAbility("Roar", "Heal"))                //1
        val redOx = Enemy("Red Ox","redox.png",160, 4, 4, "Reduce", SpecialAbility("Oxidixse","Strong Heal"))                                      //2

        val giantSquid = Enemy("Giant Squid", "kraken.png",150,2, 2, "Tentacle Rush", SpecialAbility("Devour","Devour"))
        val boss = Enemy("Memory Thief","thief.gif",365, 10,10, "Distort", SpecialAbility("Power Steal","Power Steal"))

        enemies.add(stranger)
        enemies.add(leo)

        enemy = stranger

        /*
        * SETTING UP LOCATIONS
        * */

        val village = Location("The Village", "village.png","", mutableListOf(enemies[0]))
        map.add(listOf(village))

        //Temperate biomes (2 out of 3 are visible)
        val meadow = Location("Sunlit Meadow", "meadow.png","", mutableListOf(enemies[1]))
        val forest = Location("Deep Forest", "forest.png","", mutableListOf(enemies[1]))
        val river = Location("Rushing River", "river.png","", mutableListOf(enemies[1]))
        map.add(listOf(meadow,forest,river).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Cold biomes (2 out of 3 are visible)
        val mountains = Location("Winter Hills", "mountains.png","", mutableListOf())
        val snowForest = Location("Snowy Forest", "snow-forest.png","", mutableListOf())
        val summit = Location("Frosted Peak", "summit.png","", mutableListOf())
        map.add(listOf(snowForest,mountains,summit).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Wet biomes (2 out of 3 are visible)
        val jungle = Location("Humid Jungle", "jungle.png","", mutableListOf())
        val wetland = Location("Muddy Wetland", "wetland.png","", mutableListOf())
        val lake = Location("Serene Lake", "lake.png","", mutableListOf())
        map.add(listOf(jungle, wetland, lake).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Warm biomes (2 out of 3 are visible)
        val beach = Location("Sunset Beach", "beach.png","", mutableListOf())
        val desert = Location("Burning Desert", "desert.png","", mutableListOf())
        val canyon = Location("Scorched Gorge", "canyon.png","", mutableListOf(enemies[1]))
        map.add(listOf(beach, desert, canyon).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //The second of the mysterious lands
        val foggySea = Location("Foggy Sea", "sea.png","", mutableListOf())
        map.add(listOf(foggySea,foggySea)) //player can only travel to this location

        //The second of the mysterious lands
        val strangeLand = Location("Strange Land", "land.png","", mutableListOf())
        map.add(listOf(strangeLand,strangeLand)) //player can only travel to this location

        //The final of the mysterious lands
        val lostRealm = Location("The Lost Realm", "lost-realm.png","", mutableListOf())
        map.add(listOf(lostRealm,lostRealm)) //player can only travel to this location

        location = village
    }

    fun travel(choice: Char) {
        when (choice) {
            'A' -> location = map[(distanceTravelled/100)+1][0]
            'B' -> location = map[(distanceTravelled/100)+1][1]
        }

        phase = 'B'

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

    fun enemyTurn() {
        log("$enemy uses attack ${enemy.attackName}")
    }
}


/**
 * Main UI window, handles user clicks, etc.
 *
 * @param Game the game state object
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

    private val cardArea = JLabel("PLACE")

    var windowLocation = frame.location
    val placeArea = Point(1390, 100)

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        pane.preferredSize = java.awt.Dimension(1600, 420)

        locationLabel.setBounds(1110, 200, 480, 50)

        locationImageLabel.setBounds(500, 10, 600, 400)

        enemyImageLabel.setBounds(500, 10, 600, 400)

        healthBar.setBounds(10, 10, 480, 50)
        healthBarLabel.setBounds(10, 10, 480, 50)

        enemyBar.setBounds(1110, 10, 480, 50)
        enemyBarLabel.setBounds(1110, 10, 480, 50)

        buttonA.setBounds(10, 200, 230, 50)
        buttonB.setBounds(250, 200, 230, 50)

        cardArea.setBounds(1390, 70, 200, 330)

        pane.add(locationLabel)
        pane.add(enemyImageLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(locationImageLabel)
        pane.add(healthBar)
        pane.add(healthBarLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(enemyBar)
        pane.add(enemyBarLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(buttonA)
        pane.add(buttonB)
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

        cardArea.border = BorderFactory.createLineBorder(Color.RED, 5)
        cardArea.font = Font("SANS_SERIF", Font.BOLD, 40)
    }

    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = pane                           // Define the main content
        frame.pack()
        frame.setLocation(((Toolkit.getDefaultToolkit().screenSize.width - frame.width) / 2),0)              // Centre on the screen
        windowLocation = frame.location
    }

    private fun setupActions() {
        buttonA.addActionListener { handleA() }
        buttonB.addActionListener { handleB() }

        frame.addComponentListener(object: ComponentAdapter(){
            override fun componentMoved(e: ComponentEvent?) {
                windowLocation = frame.location
                super.componentMoved(e)
            }
        })

    }


    private fun updateUI() {
        locationLabel.text = game.location.name
        locationIcon = ImageIcon(game.location.icon)
        locationImageLabel.icon = locationIcon

        cardArea.isVisible = game.isPlayerTurn

        when (game.phase) {
            'T' -> {
                buttonA.text = "NW"
                buttonB.text = "NE"

                enemyBar.isVisible = false
                enemyBarLabel.isVisible = false
            }
            'B' -> {
                buttonA.text = "Fight"
                buttonB.text = "Flee"

                enemyBar.isVisible = true
                enemyBar.maximum = game.enemy.maxHealth
                enemyBar.value = game.enemy.health

                enemyBarLabel.isVisible = true
                enemyBarLabel.text = game.enemy.name

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

            //'B' -> Fight

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

            //'B' -> Flee

        }
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
        panel.preferredSize = java.awt.Dimension(200, 300) // the window header is an extra 30px tall
    }

    fun setupStyles(){

    }

    fun setupActions(){
        frame.addComponentListener(object: ComponentAdapter(){
            override fun componentMoved(e: ComponentEvent?) {
                val location = frame.location
                super.componentMoved(e)
                //Check if card is in place area
                if (game.isPlayerTurn) {
                    val placeLocation = Point(owner.windowLocation.x + owner.placeArea.x,owner.windowLocation.y + owner.placeArea.y)
                    if (location.x in (placeLocation.x-5..placeLocation.x+5) &&
                        location.y in (placeLocation.y-5..placeLocation.y+5)) {
                        //play()
                    }
                }
            }
        })
    }

    fun setupWindow(){
        frame.isResizable = false                           // Can't resize
        frame.contentPane = panel                           // Define the main content
        frame.pack()
        frame.setLocation(((Toolkit.getDefaultToolkit().screenSize.width - frame.width) / 2),(owner.frame.getLocation().y + 460))
    }

    fun updateUI(){
    }

    fun play(){
        //Card is placed
        println("Placed")

        if (card.playCard(game)) frame.dispose()
        else {
          card = game.getRandomNLCard(game.deck)
          updateUI()
          frame.setLocation((owner.frame.getLocation().x + 1400),(owner.frame.getLocation().y + 460))
        }
    }
}



class Location(val name: String, val image: String, val description: String, val possibleEnemies: MutableList<Enemy>) {
    val icon = ClassLoader.getSystemResource("images/locations/$image")
}

class Card(val name: String, val image: String, val effect: String, val intensity: Int, val legendary: Boolean) {
    val icon = ClassLoader.getSystemResource("images/cards/$image")

    fun playCard(game: Game): Boolean {
        game.log("You played card: $name")
        when (effect) {
            "Damage" -> game.enemy.health -= intensity
            "Dodge" -> game.dodgeChance += intensity
        }
        if (legendary) game.legendaryCount --
        return legendary
    }
}

class Enemy(val name: String, val image: String, val maxHealth: Int, var attack: Int, var speed: Int, val attackName: String, val specialAbility: SpecialAbility?) {
    val icon = ClassLoader.getSystemResource("images/enemies/$image")
    var health = maxHealth
}

class SpecialAbility(val name: String, val effect: String){
    fun doSpecialAttack(game: Game) {
        game.log("${game.enemy.name} used special ability: $name")
        when (effect){
            "Heal" -> {
                val effect = (3..8).random()
                game.enemy.health += effect
                game.log("${game.enemy.name} healed $effect health!")

            }
            "Strong Heal" -> {
                val effect = (10..16).random()
                game.enemy.health += effect
                game.log("${game.enemy.name} healed $effect health!")
            }
            "Accelerate" -> {
                game.enemy.speed += 3
                game.log("${game.enemy.name} got faster!")
            }
            "Power Up" -> {
                game.enemy.attack += 3
                game.log("${game.enemy.name} got stronger!")
            }
            "Devour" -> {

            }
            "Power Steal" -> {

            }
        }
    }
}