package app.minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AppMinesweeper extends Application {

    public static final Color field1 = Color.GREEN;
    public static final Color field2 = Color.DARKGREEN;
    public static final Color flag = Color.RED;
    public static final Color revealed1 = Color.MOCCASIN;
    public static final Color revealed2 = Color.BURLYWOOD;
    public static int x = 16;
    public static int y = 16;
    public static final int mineCount = x*2;
    public static final GridPane gridPane = new GridPane();
    public static final List<Rectangle> mineFields = new ArrayList<>();

    @Override
    public void start(Stage stage){
        Scanner scanner = new Scanner(System.in);
        System.out.println("x: ");
        x = scanner.nextInt();
        System.out.println("y: ");
        y = scanner.nextInt();
        gridPane.setGridLinesVisible(false);

        Scene scene = new Scene(gridPane);
        stage.setScene(scene);
        stage.setTitle("Minesweeper");


        //sketchy changelistener stuff to keep width and height the same, doesnt really work
        stage.heightProperty().addListener((observableValue, oldValue, newValue) -> stage.setWidth((double)newValue));
        stage.widthProperty().addListener((observableValue, oldValue, newValue) -> stage.setHeight((double)newValue));

        //creating the rectangles in 2 colors
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                Rectangle rectangle = new Rectangle(100, 100, Color.BLUE);
                if((j+i)%2 == 0){
                    rectangle.setUserData(field1);
                } else {
                    rectangle.setUserData(field2);
                }
                Color color = (Color) rectangle.getUserData();
                rectangle.setFill(color);
                //rectangles bound to gridpane via fluent binding
                rectangle.widthProperty().bind(gridPane.widthProperty().divide(x));
                rectangle.heightProperty().bind(gridPane.heightProperty().divide(y));

                gridPane.add(rectangle ,j, i);
            }
        }

        //Mines placed at random spots
        Random random = new Random();

        while (mineFields.size() != mineCount) {
            int xCord = random.nextInt(x);
            int yCord = random.nextInt(y);

            Rectangle rectangle = (Rectangle) gridPane.getChildren().get(getIndex(xCord, yCord));
            if(mineFields.isEmpty() || !mineFields.contains(rectangle)){
//                rectangle.setFill(Color.PURPLE);
//                System.out.println("Mine at: " + xCord + ", " + yCord);
                mineFields.add(rectangle);
            }

        }

        //uncovering
        gridPane.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.SECONDARY){
                //putting and removing flags
                if(!mouseEvent.getTarget().equals(gridPane)){
                    Rectangle rectangle = (Rectangle) mouseEvent.getTarget();
                    flagEvent(rectangle);
                }
            } else if (mouseEvent.getButton() == MouseButton.PRIMARY){
                //uncovering a field
                if(!mouseEvent.getTarget().equals(gridPane)){
                    Rectangle rectangle = (Rectangle) mouseEvent.getTarget();
                    //stopping input after loss
                    if(!uncover(rectangle)){
                        scene.getRoot().setDisable(true);
                    }
                }
            }
        });

        //gridpane size bound to window size
        gridPane.prefHeightProperty().bind(stage.heightProperty());
        gridPane.prefWidthProperty().bind(gridPane.prefHeightProperty());
        stage.show();
    }

    //####################################### functions ####################################################

    /*
      input: rectangle in the playing field
      output: int amount of mines that are surrounding the rectangle
    */
    public int checkSurroundings(Rectangle rectangle){
        int surroundingMinesCount = 0;
        List<Rectangle> surrounding = getSurrounding(rectangle);
        for (Rectangle rectangle1 : surrounding) {
            if(mineFields.contains(rectangle1)){
                surroundingMinesCount++;
            }
        }
        return surroundingMinesCount;
    }

    /*
      input: rectangle in playing field
      output: false if a bomb was hit, else true
     */
    public boolean uncover(Rectangle rectangle){
        //checking if field is not a flag
        if(!rectangle.getFill().equals(flag) && !rectangle.getFill().equals(revealed1) && !rectangle.getFill().equals(revealed2)) {
            //hitting mine logic
            if (mineFields.contains(rectangle)) {

                for (Rectangle mineField : mineFields) {
                    mineField.setFill(Color.MAGENTA);
                }
                rectangle.setFill(Color.PURPLE);
                System.out.println("GAME OVER");
                return false;
            } else {
                //uncovering logic

                //just the color change
                Color color = (Color) rectangle.getUserData();
                if (color.equals(field1)) {
                    rectangle.setFill(revealed1);
                } else {
                    rectangle.setFill(revealed2);
                }

                int rectangleXCord = GridPane.getColumnIndex(rectangle);
                int rectangleYCord = GridPane.getRowIndex(rectangle);
                List<Rectangle> surrounding = getSurrounding(rectangle);

                int checked = checkSurroundings(rectangle);
                //if there are no bombs next to the rectangle, that means that all the surrounding rectangles are not bombs and thus can be uncovered as well
                if (checked == 0) {
                    for (Rectangle rectangle1 : surrounding) {
                        uncover(rectangle1);
                    }
                }
                //if there are bombs it adds the amount on the uncovered field
                else {
                    gridPane.add(new Label(checked + ""), rectangleXCord, rectangleYCord);
                }
//                System.out.println("Opened Field: " + GridPane.getColumnIndex(rectangle) + ", " + GridPane.getRowIndex(rectangle));
            }
        }
        return true;
    }

    /*
      just changes and resets the color of rectangle when flags are set
     */
    public void flagEvent(Rectangle rectangle){
        if(rectangle.getFill().equals(flag)){
            rectangle.setFill((Color) rectangle.getUserData());
        } else if (!rectangle.getFill().equals(revealed1) && !rectangle.getFill().equals(revealed2)) {
            rectangle.setFill(flag);
        }
    }

    /*
      input: rectangle if the playing field
      output: array of all the surrounding rectangles
     */
    public List<Rectangle> getSurrounding(Rectangle rectangle){
        List<Rectangle> surrounding = new ArrayList<>();

        int rectangleXCord = GridPane.getColumnIndex(rectangle);
        int rectangleYCord = GridPane.getRowIndex(rectangle);

        int index = getIndex(rectangleXCord, rectangleYCord);


        //right side
        if(!(rectangleXCord == x-1)){
            surrounding.add((Rectangle) gridPane.getChildren().get(index+1));
        }

        //above the rectangle
        if(!(rectangleYCord == 0)){
            //above rectangle
            surrounding.add((Rectangle) gridPane.getChildren().get(index-x));

            //right above rectangle
            if(!(rectangleXCord == x-1)){
                surrounding.add((Rectangle) gridPane.getChildren().get((index-x)+1));
            }

            //left above rectangle
            if(!(rectangleXCord == 0)){
                surrounding.add((Rectangle) gridPane.getChildren().get((index-x)-1));
            }
        }

        //left side
        if(!(rectangleXCord == 0)){
            //left next to rectangle
            surrounding.add((Rectangle) gridPane.getChildren().get(index-1));
        }

        //under the rectangle
        if(!(rectangleYCord == y-1)){
            //under the rectangle
            surrounding.add((Rectangle) gridPane.getChildren().get(index+x));

            //right under the rectangle
            if(!(rectangleXCord == x-1)) {
                surrounding.add((Rectangle) gridPane.getChildren().get((index + x) + 1));
            }

            //left under the rectangle
            if(!(rectangleXCord == 0)) {
                surrounding.add((Rectangle) gridPane.getChildren().get((index + x) - 1));
            }
        }

        return surrounding;
    }

    /*
      input: rectangle in the playing field
      output: int index of the rectangle in the gridpane
     */
    public int getIndexWithRectangle(Rectangle rectangle){
        int rectangleXCord = GridPane.getColumnIndex(rectangle);
        int rectangleYCord = GridPane.getRowIndex(rectangle);
        return getIndex(rectangleXCord, rectangleYCord);
    }

    /*
     input: x and y coordinates of a rectangle in the playing field
     output: int index of the rectangle in the gridpane
    */
    public int getIndex(int xCord, int yCord){
        return x*yCord + xCord;
    }
}
