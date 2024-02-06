package a3.project.spaceinvaders.views

import a3.project.spaceinvaders.models.Model
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.media.AudioClip
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


class GameView (private val model: Model) : BorderPane(), IView {

    private var grid = GridPane()
    val pane = Pane()
    var player = ImageView()
    private var scoreLabel = Label()
    private var livesLabel = Label()
    private var levelLabel = Label()
    private var lifeLost = false

    init {
        model.addView(this)

        setupTopBar()
        setupAliens()
        this.background = Background(BackgroundFill(Color.BLACK, CornerRadii(0.0), Insets(0.0,0.0,0.0,0.0)))

        grid.hgap = 10.0
        grid.vgap = 5.0
        grid.layoutX = 300.0
        grid.layoutY = 25.0
        grid.columnConstraints.addAll(ColumnConstraints(30.0))
        grid.rowConstraints.addAll(RowConstraints(30.0))

        player = ImageView(Image("player.png", 30.0, 30.0, false, false))
        player.layoutX = 475.0
        player.layoutY = 590.0
        model.setPlayer(player)

        pane.children.addAll(grid, player)

        this.center = pane
    }

    override fun update() {
        redrawAliens()
        checkEnemyHit()
        checkPlayerHit()
        enemyFire()
        drawMissiles()
        checkWinLoseStatus()
    }

    private fun checkEnemyHit() {
        val missiles = model.getMissiles()
        val aliens = model.getAliens()
        val missilesToRemove = mutableListOf<ImageView>()
        val aliensToRemove = mutableListOf<ImageView>()

        // Player missile hits
        for (m in missiles) {

            // Current position of the missile
            val currX = m.layoutX
            val currY = m.layoutY

            for (a in aliens) {

                // Current position of the alien
                val posX = a.key.boundsInParent.minX + grid.layoutX
                val posY = a.key.boundsInParent.minY + grid.layoutY

                if ((currX >= posX) && (currX <= (posX + 30.0)) &&
                    (currY >= posY) && (currY <= (posY + 30.0))) {

                    // Play sound
                    val sound = javaClass.classLoader.getResource("invaderkilled.wav").toString()
                    val clip = AudioClip(sound)
                    clip.play()

                    // Remove the alien and missile
                    aliensToRemove.add(a.key)
                    missilesToRemove.add(m)
                    pane.children.remove(m)

                    // Update Score
                    model.incrementSpeed()
                    model.incrementScore()
                    scoreLabel.text = "Score: " + model.getScore()

                    // Missile removed so cannot hit another shop
                    break
                }
            }
         }

        model.removeMissile(missilesToRemove)
        model.removeAliens(aliensToRemove)

    }

    private fun checkPlayerHit() {
        val aliens = model.getAliens()
        val missiles = model.getEnemyMissiles()
        val missilesToRemove = mutableListOf<ImageView>()

        for (m in missiles) {

            // Current position of the missile
            val currX = m.layoutX
            val currY = m.layoutY

            if ((currX >= player.layoutX) && (currX <= (player.layoutX + 30.0)) &&
                (currY >= player.layoutY) && (currY <= (player.layoutY + 30.0))) {
                missilesToRemove.add(m)
                pane.children.remove(m)

                val sound = javaClass.classLoader.getResource("explosion.wav").toString()
                val clip = AudioClip(sound)
                clip.play()

                model.lostLife()
                model.stopTimer()
                livesLabel.text = "Lives: " + model.getLives()
                lifeLost = true
                break
            }
        }
        model.removeEnemyMissile(missilesToRemove)

        for (a in aliens) {
            val alienX = a.key.boundsInParent.minX + grid.layoutX
            val alienY = a.key.boundsInParent.minY + grid.layoutY
            val playerX = player.layoutX
            val playerY = player.layoutY

            if ((((alienX >= playerX) && (alienX <= (playerX + 30.0)) &&
                        (alienY >= playerY) && (alienY <= (playerY + 30.0)))) ||
                (((alienX + 30.0 >= playerX) && (alienX + 30.0 <= (playerX + 30.0)) &&
                        (alienY >= playerY) && (alienY <= (playerY + 30.0))))
                || (((alienX + 30.0 >= playerX) && (alienX + 30.0 <= (playerX + 30.0)) &&
                        (alienY + 30.0 >= playerY) && (alienY + 30.0 <= (playerY + 30.0))))
                || (((alienX >= playerX) && (alienX <= (playerX + 30.0)) &&
                        (alienY + 30.0 >= playerY) && (alienY + 30.0 <= (playerY + 30.0))))) {
                val sound = javaClass.classLoader.getResource("explosion.wav").toString()
                val clip = AudioClip(sound)
                clip.play()

                model.lostLife()
                model.stopTimer()
                livesLabel.text = "Lives: " + model.getLives()
                lifeLost = true
                break
            }
        }
    }

    private fun enemyFire() {

        val randomInt = (0..2000).random()

        if (randomInt <= model.getBulletProbability()) {
            val aliens = model.getAliens()
            val selectedIndex = Random.nextInt(0, aliens.size)
            val aliensList = aliens.toList()
            val selectedAlien = aliensList[selectedIndex]

            val x = selectedAlien.first.boundsInParent.minX + grid.layoutX
            val y = selectedAlien.first.boundsInParent.minY + grid.layoutY
            val row = selectedAlien.second.second
            val bullet: ImageView

            if (row == 0) {
                bullet = ImageView(Image("g_missile.png", 10.0, 15.0, false, false))
            } else if (row == 1 || row == 2) {
                bullet = ImageView(Image("b_missile.png", 10.0, 15.0, false, false))
            } else {
                bullet = ImageView(Image("p_missile.png", 10.0, 15.0, false, false))
            }

            bullet.layoutX = x + 12.0
            bullet.layoutY = y + 20.0

            model.addEnemyBullet(bullet)
            pane.children.add(bullet)

            val sound = javaClass.classLoader.getResource("shoot.wav")?.toString()
            val clip = AudioClip(sound)
            clip.play()
        }
    }

    private fun checkWinLoseStatus() {
        val aliens = model.getAliens()

        // If no aliens, then level is won
        if (aliens.isEmpty()) {
            wonGame()
            return
        }

        if (model.getLives() == 0) {
            lostGame()
            return
        }

        if (lifeLost) {
            respawnPlayer()
            lifeLost = false
            model.startTimer()
        }
    }

    private fun respawnPlayer() {
        val x = Random.nextDouble(0.0,1000.0-30.0)
        player.layoutX = x
        player.layoutY = 590.0
        model.startTimer()
    }

    private fun drawMissiles() {
        val missiles = model.getMissiles()
        val enemyMissiles = model.getEnemyMissiles()
        val toRemove = mutableListOf<ImageView>()
        val enemyMissilesToRemove = mutableListOf<ImageView>()

        for (m in missiles) {
            if (m.layoutY <= 0.0) {
                toRemove.add(m)
                pane.children.remove(m)
            } else {
                m.layoutY -= model.getBulletSpeed()
            }
        }

        for (e in enemyMissiles) {
            if (e.layoutY >= 700.0) {
                enemyMissilesToRemove.add(e)
                pane.children.remove(e)
            } else {
                e.layoutY += model.getBulletSpeed()
            }
        }

        model.removeMissile(toRemove)
    }

    private fun setupTopBar() {
        this.top = null

        scoreLabel = Label("Score: " + model.getScore()).apply {
            font = Font("Arial", 20.0)
            textFill = Color.WHITE
            textAlignment = TextAlignment.LEFT
            alignment = Pos.TOP_LEFT
        }
        scoreLabel.padding = Insets(10.0, 600.0, 10.0, 20.0)

        livesLabel = Label("Lives: " + model.getLives()).apply {
            font = Font("Arial", 20.0)
            textFill = Color.WHITE
            textAlignment = TextAlignment.RIGHT
            alignment = Pos.TOP_RIGHT
        }
        livesLabel.padding = Insets(10.0, 50.0, 10.0, 0.0)

        levelLabel = Label("Level: " + model.getLevel()).apply {
            font = Font("Arial", 20.0)
            textFill = Color.WHITE
            textAlignment = TextAlignment.RIGHT
            alignment = Pos.TOP_RIGHT
        }
        levelLabel.padding = Insets(10.0, 20.0, 10.0, 0.0)

        val topBox = HBox()
        topBox.children.addAll(scoreLabel, livesLabel, levelLabel)
        this.top = topBox
    }

    private fun redrawAliens() {
        grid.children.removeAll()
        pane.children.remove(grid)

        val aliens = model.getAliens()
        val newGrid = GridPane()
        newGrid.hgap = 10.0
        newGrid.vgap = 5.0
        newGrid.layoutX = grid.layoutX
        newGrid.layoutY = grid.layoutY

        var rightmostCol = 0
        var leftmostCol = 10

        for (i in 0 until grid.columnCount) {
            newGrid.columnConstraints.add(ColumnConstraints(30.0))
        }

        for (i in 0 until grid.rowCount) {
            newGrid.rowConstraints.add(RowConstraints(30.0))
        }

        for (a in aliens) {
            newGrid.add(a.key, a.value.first, a.value.second)
            leftmostCol = min(leftmostCol, a.value.first)
            rightmostCol = max(rightmostCol, a.value.first)
        }
        grid = newGrid

        val leftmostBound = 0.0 - (leftmostCol * 30) - ((leftmostCol) * 10)
        val rightmostBound = 1000.0 - ((rightmostCol + 1) * 30) - ((rightmostCol) * 10)

        //Currently moving right so +
        val speed = model.getEnemySpeed()
        if (model.moveRight()) {
            if (grid.layoutX == rightmostBound) {
                model.setMoveRight(false)
                grid.layoutY += model.getEnemyVerticalSpeed()
            } else if (grid.layoutX + speed > rightmostBound) {
                grid.layoutX = rightmostBound
            } else {
                grid.layoutX += speed
            }
        }
        // Currently moving left so -
        else {
            if (grid.layoutX == leftmostBound) {
                model.setMoveRight(true)
                grid.layoutY += model.getEnemyVerticalSpeed()
            } else if (grid.layoutX - speed < leftmostBound) {
                grid.layoutX = leftmostBound
            } else {
                grid.layoutX -= speed
            }
        }

        pane.children.add(grid)
    }

    private fun setupAliens() {
        var alien = ImageView()

        for (row in (0..4)) {
            for (col in (0..9)) {
                if (row == 0) {
                    alien = ImageView(Image("g_alien.png", 30.0, 30.0, false, false))
                } else if (row == 1 || row == 2) {
                    alien = ImageView(Image("b_alien.png", 30.0, 30.0, false, false))
                } else {
                    //pink alien
                    alien = ImageView(Image("p_alien.png", 30.0, 30.0, false, false))
                }
                model.addAlien(alien, col, row)
                grid.add(alien, col, row)
            }
        }

        grid.children.remove(alien)

    }
    
    private fun wonGame() {
        model.stopTimer()
        val currLevel = model.getLevel()
        if (currLevel == 3) {
           winLoseDialog("YOU WON")
        } else {
            model.setLevel(currLevel+1)
            model.setupLevel()
            reinitScene()
        }
    }

    private fun reinitScene() {
        pane.children.clear()
        this.children.clear()

        setupTopBar()
        setupAliens()
        this.background = Background(BackgroundFill(Color.BLACK, CornerRadii(0.0), Insets(0.0,0.0,0.0,0.0)))

        grid.hgap = 10.0
        grid.vgap = 5.0
        grid.layoutX = 300.0
        grid.layoutY = 25.0
        grid.columnConstraints.addAll(ColumnConstraints(30.0))
        grid.rowConstraints.addAll(RowConstraints(30.0))

        player = ImageView(Image("player.png", 30.0, 30.0, false, false))
        player.layoutX = 475.0
        player.layoutY = 590.0
        model.setPlayer(player)

        pane.children.addAll(grid, player)

        this.center = pane
        model.startTimer()
    }

    private fun lostGame() {
       winLoseDialog("GAME OVER")
    }

    private fun winLoseDialog(result : String) {
        model.setGameLost()
        pane.children.clear()

        val infoBox = VBox()
        val title = Label(result).apply {
            font = Font("Arial Bold", 30.0)
        }
        title.padding = Insets(0.0, 0.0, 10.0, 0.0)
        val score = Label("Final Score: " + model.getScore()).apply {
            font = Font("Arial", 20.0)
        }
        score.padding = Insets(0.0, 0.0, 20.0, 0.0)
        val info = Label("ENTER - Start new game \nI - Back to instructions \nQ - Quit game \n1, 2, 3 - Start new game at specific level").apply {
            font = Font("Arial", 15.0)
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
        }
        infoBox.children.addAll(title, score, info)
        infoBox.layoutX = 300.0
        infoBox.layoutY = 150.0
        infoBox.background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
        infoBox.minWidth = 400.0
        infoBox. minHeight = 250.0
        infoBox.alignment = Pos.CENTER
        this.pane.children.add(infoBox)
    }

}