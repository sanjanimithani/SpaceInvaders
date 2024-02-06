package a3.project.spaceinvaders

import a3.project.spaceinvaders.models.Model
import a3.project.spaceinvaders.views.GameView
import a3.project.spaceinvaders.views.StartupView
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.stage.Stage

class SpaceInvaders : Application() {
    val model = Model()
    val startup = StartupView(model)
    val scene1 = Scene(startup)
    var startupScene = true

    fun switchScene(stage: Stage) {
        if (startupScene) {
            stage.scene = scene1
        } else {
            //model sets up game level
            var game = GameView(model)
            var count = 1
            stage.scene = Scene(game)
            model.startTimer()

            stage.scene.onKeyPressed = EventHandler {
                if (it.code.equals(KeyCode.A) || it.code.equals(KeyCode.LEFT)) {
                    println("A")
                    model.movePlayerLeft(game)
                } else if (it.code.equals(KeyCode.D) || it.code.equals(KeyCode.RIGHT)) {
                    println("D")
                    model.movePlayerRight(game)
                } else if (it.code.equals(KeyCode.SPACE)) {
                    if (model.playerCanFire()) {
                        model.setLastBulletTime(System.nanoTime())
                        model.fired()
                        model.fireMissile(game)
                    }
                } else if (it.code.equals(KeyCode.Q)) {
                    Platform.exit()
                } else if (it.code.equals(KeyCode.I)) {
                    if (model.gameLost()) {
                        startupScene = true
                        switchScene(stage)
                    }
                } else if (it.code.equals(KeyCode.ENTER)) {
                    model.restartGame()
                    model.setLevel(1)
                    switchScene(stage)
                }  else if (it.code.equals(KeyCode.DIGIT1)) {
                    model.setLevel(1)
                    startupScene = false
                    switchScene(stage)
                } else if (it.code.equals(KeyCode.DIGIT2)) {
                    println("LEVEL 2")
                    model.setLevel(2)
                    startupScene = false
                    switchScene(stage)
                } else if (it.code.equals(KeyCode.DIGIT3)) {
                    println("LEVEL 3")
                    model.setLevel(3)
                    startupScene = false
                    switchScene(stage)
                }
            }
        }
    }

    override fun start(stage: Stage) {

        scene1.onKeyPressed = EventHandler {
            println(it)
            if (it.code.equals(KeyCode.ENTER)) {
                model.setLevel(1)
                println("START GAME")
                startupScene = false
            } else if (it.code.equals(KeyCode.DIGIT1)) {
                model.setLevel(1)
                startupScene = false
            } else if (it.code.equals(KeyCode.DIGIT2)) {
                println("LEVEL 2")
                model.setLevel(2)
                startupScene = false
            } else if (it.code.equals(KeyCode.DIGIT3)) {
                println("LEVEL 3")
                model.setLevel(3)
                startupScene = false
            } else if (it.code.equals(KeyCode.Q)) {
                Platform.exit()
            }
            switchScene(stage)
        }

        stage.apply {
            title = "Space Invaders (c) Sanjani Mithani - 20782944"
            scene = scene1
            width = 1000.0
            height = 700.0
            /*maxWidth = 1600.0
            maxHeight = 1080.0*/
            isResizable = false
        }.show()



    }
}
