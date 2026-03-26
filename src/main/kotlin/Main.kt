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

    var enemy = location.possibleEnemies[0]

    val deck = setupDeck()
    val hand = mutableListOf<Card>()

    fun setupMap(enemies: List<Enemy>): List<List<Location>> {
        val map = mutableListOf<List<Location>>()


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

        val stranger = Enemy("Stranger","stranger.png",999,999,999)    //0
        val leo = Enemy("Leo","leo.png",100,10,5)                      //1
        val redOx = Enemy("Red Ox","redox.png",160, 4, 4)              //2

        val giantSquid = Enemy("Giant Squid", "squid.png",150,8,2)
        val boss = Enemy("Memory Thief","thief.png",365, 10, 10)

        enemies.add(stranger)
        enemies.add(leo)

        return enemies
    }

    fun travel(choice: Char) {
        when (choice) {
            'A' -> location = map[(distanceTravelled/100)+1][0]
            'B' -> location = map[(distanceTravelled/100)+1][1]
        }

        phase = 'B'

        distanceTravelled += 100
        enemy = location.possibleEnemies[(0..<location.possibleEnemies.size).random()]
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

        pane.add(locationLabel)
        pane.add(enemyImageLabel, JLayeredPane.DEFAULT_LAYER + 1)
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

                enemyBar.isVisible = false
                enemyBarLabel.isVisible = false
            }
            'B' -> {
                buttonA.text = "Fight"
                buttonB.text = "Flee"

                enemyBar.isVisible = true
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

class CardWindow(owner: MainWindow, game: Game, card: Card) {}



class Location(val name: String, val image: String, val description: String, val possibleEnemies: MutableList<Enemy>) {
    val icon = ClassLoader.getSystemResource("images/locations/$image")
}

class Card(val name: String, val image: String, val effect: String, val intensity: Int) {
    val icon = ClassLoader.getSystemResource("images/cards/$image")
}

class Enemy(val name: String, val image: String, val health: Int, val attack: Int, val speed: Int) {
    val icon = ClassLoader.getSystemResource("images/enemies/$image")
    fun attack() {}
}