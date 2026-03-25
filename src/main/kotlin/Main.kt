import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Font
import java.awt.Image
import javax.swing.*
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
}


/**
 * Manage app state
 *
 * @property name the user's name
 * @property score the points earned
 */
class Game {
    var name = "Test"
    var distanceTravelled = 0
    var health = 100
    var phase = 'I' //[I]ntro, [T]ravel, [B]attle

    val map = setupMap(setupEnemies())
    var location = map[0][0]
    var lastLocation = 0

    val deck = setupDeck()
    val hand = mutableListOf<Card>()

    fun setupMap(enemies: List<Enemy>): List<List<Location>> {
        val map = mutableListOf<List<Location>>()


        val village = Location("The Village", "village.png","", listOf())
        map.add(listOf(village))

        //Temperate biomes (2 out of 3 are visible)
        val meadow = Location("Sunlit Meadow", "meadow.png","", listOf(enemies[0]))
        val forest = Location("Deep Forest", "forest.png","", listOf())
        val river = Location("Rushing River", "river.png","", listOf(enemies[0], enemies[1]))
        map.add(listOf(meadow,forest,river).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Cold biomes (2 out of 3 are visible)
        val mountains = Location("Winter Hills", "mountains.png","", listOf())
        val snowForest = Location("Snowy Forest", "snow-forest.png","", listOf())
        val summit = Location("Frosted Peak", "summit.png","", listOf())
        map.add(listOf(snowForest,mountains,summit).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //Maybe add another temperate section here

        //Warm biomes (2 out of 3 are visible)
        val beach = Location("Sunset Beach", "beach.png","", listOf())
        val desert = Location("Burning Desert", "desert.png","", listOf())
        val canyon = Location("Scorched Gorge", "canyon.png","", listOf(enemies[0],enemies[1]))
        map.add(listOf(beach, desert, canyon).shuffled()) //randomised list of each area, only index 0 and 1 are used

        //The second of the mysterious lands
        val foggySea = Location("Foggy Sea", "sea.png","", listOf())
        map.add(listOf(foggySea,foggySea)) //player can only travel to this location

        //The second of the mysterious lands
        val strangeLand = Location("Strange Land", "land.png","", listOf())
        map.add(listOf(strangeLand,strangeLand)) //player can only travel to this location

        return map
    }

    fun setupDeck(): List<Card> {
        val deck = mutableListOf<Card>()

        val axe = Card("Axe", "axe.png", "DMG", 7)

        deck.add(axe)

        return deck
    }

    fun setupEnemies(): List<Enemy> {
        val enemies = mutableListOf<Enemy>()

        val leo = Enemy("Leo","leo.png",100,10,5)                      //0
        val redOx = Enemy("Red Ox","redox.png",160, 4, 4)              //1

        enemies.add(leo)
        enemies.add(redOx)

        return enemies
    }

    fun travel(choice: Char) {
        when (choice) {
            'A' -> location = map[(distanceTravelled/100)+1][0]
            'B' -> location = map[(distanceTravelled/100)+1][1]
        }
        distanceTravelled += 100
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



    private val healthBar = JProgressBar(0, 100)
    private val healthBarLabel = JLabel("Health")

    private val enemyBar = JProgressBar(0, 100)
    private val enemyBarLabel = JLabel("Enemy Health")

    private val buttonA = JButton("Tutorial")
    private val buttonB = JButton("Skip")

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
        healthBar.setBounds(10, 10, 480, 50)
        healthBarLabel.setBounds(10, 10, 480, 50)

        enemyBar.setBounds(1110, 10, 480, 50)
        enemyBarLabel.setBounds(1110, 10, 480, 50)

        buttonA.setBounds(10, 200, 230, 50)
        buttonB.setBounds(250, 200, 230, 50)

        pane.add(locationLabel)
        pane.add(locationImageLabel)
        pane.add(healthBar)
        pane.add(healthBarLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(enemyBar)
        pane.add(enemyBarLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(buttonA)
        pane.add(buttonB)

    }

    private fun setupStyles() {
        locationLabel.horizontalAlignment = SwingConstants.CENTER
        locationLabel.font = Font("SANS_SERIF", Font.BOLD, 20)

        locationImageLabel.icon = locationIcon

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
    }

    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = pane                           // Define the main content
        frame.pack()
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    private fun setupActions() {
        buttonA.addActionListener { handleA() }
        buttonB.addActionListener { handleB() }
    }


    private fun updateUI() {
        locationLabel.text = game.location.name
        locationIcon = ImageIcon(game.location.icon)
        locationImageLabel.icon = locationIcon

        when (game.phase) {
            'T' -> {
                buttonA.text = "NW"
                buttonB.text = "NE"
            }
            'B' -> {
                buttonA.text = "Fight"
                buttonB.text = "Flee"
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

class CardWindow(owner: MainWindow, game: Game, card: Card) {}



class Location(val name: String, val image: String, val description: String, val possibleEnemies: List<Enemy>) {
    val icon = ClassLoader.getSystemResource("images/locations/$image")
}

class Card(val name: String, val image: String, val effect: String, val intensity: Int) {
    val icon = ClassLoader.getSystemResource("images/cards/$image")
}

class Enemy(val name: String, val image: String, val health: Int, val attack: Int, val speed: Int) {
    fun attack() {}
}