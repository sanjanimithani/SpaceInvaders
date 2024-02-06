package a3.project.spaceinvaders.views

import a3.project.spaceinvaders.models.Model
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment


class StartupView (private val model: Model) : BorderPane()  {

    init {
        setTitle()
        setupInstructions()
        this.background = Background(BackgroundImage(Image("space.jpg", 1300.0, 1000.0, true, false),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
            ))

    }
    fun setTitle() {
        val image = Image("si_logo.png")
        val titleImage = ImageView(image)
        val box = HBox()
        box.children.add(titleImage)
        box.alignment = Pos.CENTER
        box.padding = Insets(100.0, 0.0, 0.0, 0.0)
        this.top = box
    }
    fun setupInstructions() {

        val instructionsTitle = Label("INSTRUCTIONS").apply {
            textFill = Color.WHITE
            minWidth = 10.0
            font = Font("Arial", 24.0)
            padding = Insets(0.0, 0.0, 15.0, 0.0)
        }
        val commands = Label("ENTER - Start Game \n A or <, D or > - Move ship left or right " +
                "\n SPACE - Fire Missile \n Q - Quit Game \n 1, 2, or 3 - Start Game at Specific Level").apply {
            textFill = Color.WHITE
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
        }

        val instructionsVbox = VBox()
        instructionsVbox.children.addAll(instructionsTitle, commands)
        instructionsVbox.alignment = Pos.CENTER
        this.center = instructionsVbox
    }

}