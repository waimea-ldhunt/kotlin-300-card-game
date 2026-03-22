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

    val village = Location("Village", "village.png")

    val location = village

    val cards = listOf( // (Name,image,effect,value)
        listOf("Axe", "axe.png", "DMG", 7),
        listOf("Shield", "shield.png", "DEF", 10),
        listOf("Health Potion", "red_potion.png", "HEAL", 50),
        listOf("Speed Potion", "blue_potion.png", "SPD", 5)
    )
}


/**
 * Main UI window, handles user clicks, etc.
 *
 * @param Game the game state object
 */
class MainWindow(val game: Game) {
    val frame = JFrame("WINDOW TITLE")
    private val pane = JLayeredPane().apply { layout = null }

    private val locationLabel = JLabel()
    private val locationIcon = ImageIcon(game.location.icon)

    private val healthBar = JProgressBar(0, 100)
    private val healthBarLabel = JLabel("Health")

    private val enemyBar = JProgressBar(0, 100)
    private val enemyBarLabel = JLabel("Enemy Health")

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        pane.preferredSize = java.awt.Dimension(1600, 420)

        locationLabel.setBounds(500, 10, 600, 400)
        healthBar.setBounds(10, 10, 480, 50)
        healthBarLabel.setBounds(10, 10, 480, 50)

        enemyBar.setBounds(1110, 10, 480, 50)
        enemyBarLabel.setBounds(1110, 10, 480, 50)

        pane.add(locationLabel)
        pane.add(healthBar)
        pane.add(healthBarLabel, JLayeredPane.DEFAULT_LAYER + 1)
        pane.add(enemyBar)
        pane.add(enemyBarLabel, JLayeredPane.DEFAULT_LAYER + 1)

    }

    private fun setupStyles() {
        locationLabel.icon = locationIcon

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

    }


    fun updateUI() {

    }

    fun show() {
        frame.isVisible = true
    }
}


/**
 * Info UI window is a child dialog and shows how the
 * app state can be shown / updated from multiple places
 *
 * @param owner the parent frame, used to position and layer the dialog correctly
 * @param game the app state object
 */

class Card(owner: MainWindow, game: Game) {

    init {
        val name = game.cards[Random.nextInt(game.cards.size)][0]
    }
}

class Location(val name: String, val image: String) {
    val icon = ClassLoader.getSystemResource("images/$image")
}