package controller.minesweeper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class MinesweeperController implements Initializable {

    @FXML
    private GridPane gridPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateBoard();
    }

    public void generateBoard(){
        for (int i = 0; i < gridPane.getRowCount(); i++) {
            for (int j = 0; j < gridPane.getColumnCount(); j++) {
                Rectangle rectangle = new Rectangle(50, 50, Color.BLUE);
                if((j+i)%2 == 0){
                    rectangle.setUserData(Color.GRAY);
                } else {
                    rectangle.setUserData(Color.WHITE);
                }
                Color color = (Color) rectangle.getUserData();
                rectangle.setFill(color);
                gridPane.add(rectangle ,j, i);
            }
        }
    }
}
