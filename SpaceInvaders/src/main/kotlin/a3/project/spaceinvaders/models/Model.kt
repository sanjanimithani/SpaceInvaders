package a3.project.spaceinvaders.models

import a3.project.spaceinvaders.views.GameView
import a3.project.spaceinvaders.views.IView
import javafx.animation.AnimationTimer
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.media.AudioClip


class Model {
    private var score = 0
    private var lives = 3
    private var level = 1
    private var aliensToRight = true
    private var PLAYER_SPEED = 15.0
    private var PLAYER_BULLET_SPEED = 6.0
    private var ENEMY_SPEED = 1.0
    private var ENEMY_VERTICAL_SPEED = 30.0
    private var BULLET_PROBABILITY = 10
    private var aliens = mutableMapOf<ImageView, Pair<Int, Int>>()
    private var playerMissiles = mutableListOf<ImageView>()
    private var enemyMissiles = mutableListOf<ImageView>()
    private var player = ImageView()
    private var game: IView? = null
    private var playerCanFire = true
    private var lastShot : Long = 0
    private var lost = false

    fun addView(view: IView) {
       game = view
    }

    // Code from the Animation Demo
    private val timer: AnimationTimer = object : AnimationTimer() {
        override fun handle(now: Long) {
            game?.update()        // redraw updated scene
            updateCanFire(now)
        }
    }

    fun updateCanFire(now: Long) {
        val elapsedTime = now - lastShot
        playerCanFire = if (elapsedTime >= 500_000_000) {
            true
        } else {
            false
        }
    }

    fun getBulletProbability() : Int {
        return BULLET_PROBABILITY
    }

    fun setLastBulletTime(time: Long) {
        lastShot = time
    }

    fun setGameLost() {
        lost = true
    }

    fun gameLost() : Boolean {
        return lost
    }

    fun fired() {
        playerCanFire = false
    }

    fun playerCanFire() : Boolean {
        return playerCanFire
    }

    fun setupLevel () {
        aliens.clear()
        playerMissiles.clear()
        aliensToRight = true
        lost = false

        if (level == 1) {
            PLAYER_SPEED = 15.0
            PLAYER_BULLET_SPEED = 6.0
            ENEMY_SPEED = 1.0
            ENEMY_VERTICAL_SPEED = 20.0
            BULLET_PROBABILITY = 10
        } else if (level == 2) {
            PLAYER_SPEED = 20.0
            PLAYER_BULLET_SPEED = 10.0
            ENEMY_SPEED = 1.5
            ENEMY_VERTICAL_SPEED = 23.0
            BULLET_PROBABILITY = 15
        } else if (level == 3) {
            PLAYER_SPEED = 25.0
            PLAYER_BULLET_SPEED = 15.0
            ENEMY_SPEED = 2.0
            ENEMY_VERTICAL_SPEED = 25.0
            BULLET_PROBABILITY = 20
        }
    }
    fun getBulletSpeed() : Double {
        return PLAYER_BULLET_SPEED
    }

    fun incrementSpeed() {
        if (level == 1) {
            ENEMY_SPEED += (ENEMY_SPEED * 0.05)
        } else if (level == 2) {
            ENEMY_SPEED += (ENEMY_SPEED * 0.065)
        } else if (level == 3) {
            ENEMY_SPEED += (ENEMY_SPEED * 0.08)
        }
    }

    fun lostLife() {
        lives -= 1
    }

    fun removeEnemyMissile(missiles: MutableList<ImageView>) {
        for (m in missiles) {
            enemyMissiles.remove(m)
        }
    }

    fun removeMissile(missiles: MutableList<ImageView>) {
        for (m in missiles) {
            playerMissiles.remove(m)
        }
    }

    fun removeAliens(list: MutableList<ImageView>) {
        for (a in list) {
            aliens.remove(a)
        }
    }

    fun incrementScore() {
        score += 10
    }

    fun setMoveRight(value: Boolean) {
        aliensToRight = value
    }

    fun moveRight() : Boolean {
        return aliensToRight
    }

    fun getLevel() : Int {
        return level
    }

    fun getEnemySpeed(): Double {
        return ENEMY_SPEED
    }

    fun getEnemyVerticalSpeed(): Double {
        return ENEMY_VERTICAL_SPEED
    }

    fun setLevel(numLevel : Int) {
        level = numLevel
        setupLevel()
    }

    fun getScore() : Int {
        return score
    }

    fun getLives() : Int {
        return lives
    }

    fun addAlien(alien: ImageView, col: Int, row: Int) {
        aliens[alien] = Pair(col, row)
    }

    fun getAliens() : MutableMap<ImageView, Pair<Int, Int>> {
        return aliens
    }


    fun getMissiles(): MutableList<ImageView> {
        return playerMissiles
    }

    fun setPlayer(p: ImageView) {
        player = p
    }

    fun movePlayerLeft(game: GameView) {
        if (game.player.layoutX - PLAYER_SPEED < 0.0) {
            game.player.layoutX = 0.0
        } else {
            game.player.layoutX -= PLAYER_SPEED
        }
    }

    fun movePlayerRight(game: GameView) {
        if (game.player.layoutX + PLAYER_SPEED > 970.0) {
            game.player.layoutX = 970.0
        } else {
            game.player.layoutX += PLAYER_SPEED
        }
    }

    fun fireMissile(game: GameView) {
        if (!lost) {
        val missile = ImageView(Image("r_missile.png", 5.0, 15.0, false, false))
        missile.layoutX = game.player.layoutX + 12.0
        missile.layoutY = game.player.layoutY - 20.0
        playerMissiles.add(missile)
        game.pane.children.add(missile)

        val sound = javaClass.classLoader.getResource("shoot.wav")?.toString()
        val clip = AudioClip(sound)
        clip.play()
        }
    }

    fun addEnemyBullet(bullet: ImageView) {
        enemyMissiles.add(bullet)
    }

    fun getEnemyMissiles() : MutableList<ImageView> {
        return enemyMissiles
    }

    fun stopTimer() {
        timer.stop()
    }

    fun startTimer() {
        timer.start()
    }

    fun restartGame() {
        lives = 3
        level = 1
        score = 0
        lost = false
        aliens.clear()
        playerMissiles.clear()
        aliensToRight = true
        PLAYER_SPEED = 15.0
        PLAYER_BULLET_SPEED = 6.0
        ENEMY_SPEED = 1.0
        ENEMY_VERTICAL_SPEED = 30.0
        BULLET_PROBABILITY = 10
    }
}