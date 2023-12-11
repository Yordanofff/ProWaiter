package FrontEnd;

import java.util.ArrayList;
import java.util.List;

import static FrontEnd.MenuBuilder.*;

public class MenuBuilderFrameDrawers {
    static final String midCross = "┼";
    static final String topCross = "┬";
    static final String bottomCross = "┴";
    static final String topLeftCorner = "┌";
    static final String bottomLeftCorner = "└";
    static final String midLeft = "├";
    static final String topRightCorner = "┐";
    static final String bottomRightCorner = "┘";
    static final String midRight = "┤";
    static final String topBottom = "─";
    static final String sideWall = "│";

    // ┌────────── [ADMIN] ───────────┐
    static String getTopLineOfMenu(int length, String label) {
        int numDashesEachSide = length - label.length() - 2 - 2; // 2 for the spaces, 2 for the corners
        if (label.isEmpty()) {
            return getGreenLine(length, topLeftCorner, topRightCorner);
        }
        String dashesBefore = topBottom.repeat(numDashesEachSide / 2);
        String dashesAfter = topBottom.repeat(numDashesEachSide / 2);
        if ((label.length() % 2 == 1 && length % 2 == 0) || (label.length() % 2 == 0 && length % 2 == 1)) {
            dashesAfter = topBottom.repeat((numDashesEachSide / 2) + 1);
        }
        return ConsolePrinter.getGreenMsg(topLeftCorner + dashesBefore + " ")
                + label + ConsolePrinter.getGreenMsg(" " + dashesAfter + topRightCorner);
    }

    // ┌──────────────────────────────┐
    static String getTopLineOfMenu(int length) {
        return getTopLineOfMenu(length, "");
    }

    // ┌──────────────────┬─────────┐
    static String getTopLineTable(int[] maxColumnLengths) {
        return getGreenLineTable(topLeftCorner, topRightCorner, topCross, maxColumnLengths);
    }

    static String getGreenLine(int length, String mostLeftSymbol, String mostRightSymbol) {
        return ConsolePrinter.getGreenMsg(mostLeftSymbol + topBottom.repeat(length - 2) + mostRightSymbol);
    }

    // ├────────────────────────┬───────────┬────────────────┬────────────────┤
    static String getTopLineTableEndingUpDown(int[] maxColumnLengths) {
        return getGreenLineTable(midLeft, midRight, topCross, maxColumnLengths);
    }

    static String getGreenLineTable(String mostLeftSymbol, String mostRightSymbol, String crossSymbol, int[] maxColumnLengths) {
        String toReturn = "";
        toReturn += mostLeftSymbol;
        for (int i = 0; i < maxColumnLengths.length; i++) {
            int numBottomSymbols = maxColumnLengths[i] + MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU - 1;
            toReturn += topBottom.repeat(numBottomSymbols);
            if (i != maxColumnLengths.length - 1) {
                toReturn += crossSymbol;
            }
        }
        toReturn += mostRightSymbol;
        return ConsolePrinter.getGreenMsg(toReturn);
    }
    static String getGreenLineTableForTotal(String mostLeftSymbol, String mostRightSymbol, String crossSymbol, String midCross, int[] maxColumnLengths) {
        String toReturn = "";
        toReturn += mostLeftSymbol;
        for (int i = 0; i < maxColumnLengths.length; i++) {
            int numBottomSymbols = maxColumnLengths[i] + MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU - 1;
            toReturn += topBottom.repeat(numBottomSymbols);
            if (i == maxColumnLengths.length - 2) {
                toReturn += midCross;
            } else if (i != maxColumnLengths.length - 1) {
                toReturn += crossSymbol;
            }
        }
        toReturn += mostRightSymbol;
        return ConsolePrinter.getGreenMsg(toReturn);
    }

    // └──────────┴───────────┴────────────────┴────────────────┘
    static String getBottomLineTable(int[] maxColumnLengths) {
        return getGreenLineTable(bottomLeftCorner, bottomRightCorner, bottomCross, maxColumnLengths);
    }

    // ├──────────┴───────────┴────────────────┴────────────────┤
    static String getBottomLineTableContinuingDownCorners(int[] maxColumnLengths) {
        return getGreenLineTable(midLeft, midRight, bottomCross, maxColumnLengths);
    }

    // ├─────────┴─────────┴─────────┴────────────┼───────────────┤
    static String getTopLineTableForTotal(int[] maxColumnLengths) {
        return getGreenLineTableForTotal(midLeft, midRight, bottomCross, midCross,maxColumnLengths);
    }

    //└──────────────────────────────────────────┴───────────────┘
    static String getBottomLineTableForTotal(int[] maxColumnLengths) {
        return getGreenLineTableForTotal(bottomLeftCorner, bottomRightCorner, topBottom, bottomCross,maxColumnLengths);
    }

    // │                                  Total:  │  25.30        │
    static void printMiddleMenuLineTableForTotal(String totalText, String totalSum, int[] maxColumnLengths) {

        int textLength = totalText.length();  // Make sure to add as many spaces as needed

        StringBuilder toPrint = new StringBuilder();
        int numSpaces = getNumberOfSpacesToSeparatorForTotal(maxColumnLengths);
        int numSpacesBeforeWord = numSpaces - textLength;  // - numSpacesAroundEachColumnWord
        toPrint.append(ConsolePrinter.getGreenMsg(sideWall));

        // Will not add num spaces around each word. To be able to get the label as close to the border as needed.
        toPrint.append(" ".repeat(numSpacesBeforeWord));

        toPrint.append(totalText);
        toPrint.append(ConsolePrinter.getGreenMsg(sideWall)).append(" ".repeat(numSpacesAroundEachColumnWord));

        toPrint.append(totalSum);
        int numSymbolsLastColumn = maxColumnLengths[maxColumnLengths.length -1];
        int numSpacesAfterTotalPrice = numSymbolsLastColumn + numSpacesAroundEachColumnWord - totalSum.length();

        toPrint.append(" ".repeat(numSpacesAfterTotalPrice)).append(ConsolePrinter.getGreenMsg(sideWall));

        System.out.println(toPrint);
    }

    private static int getNumberOfSpacesToSeparatorForTotal(int[] maxColumnLengths) {
        int numSpaces = 0;
        for (int i = 0; i < maxColumnLengths.length - 1; i++) {
            numSpaces += maxColumnLengths[i] + (2 * numSpacesAroundEachColumnWord) + 1;
        }
        return numSpaces - 1; // remove the first wall symbol length.
    }

    // ├──────────┼───────────┼────────────────┼────────────────┤
    static String getMidLineTable(int[] maxColumnLengths) {
        return getGreenLineTable(midLeft, midRight, midCross, maxColumnLengths);
    }

    // ├──────────────────────────────┤
    static String getMidLine(int length) {
        return getGreenLine(length, midLeft, midRight);
    }

    // └──────────────────────────────┘
    static String getBottomLine(int length) {
        return getGreenLine(length, bottomLeftCorner, bottomRightCorner);
    }

    // │  column 1  │  column 2  │  column 3  │  column 4  │
    static void printMiddleMenuLineTable(String row, int[] maxColumnLengths, int numSpacesAroundEachColumnWord) {

        // Green frame, white letters like everywhere.
        String[] elements = getRowElementsTrimmed(row);
        int elementsLength = elements.length;

        StringBuilder toPrint = new StringBuilder("");

        for (int i = 0; i < elementsLength; i++) {
            String currentElement = elements[i];

            int maxLengthCurrentPosition = maxColumnLengths[i];
            int count = maxLengthCurrentPosition - currentElement.length() + numSpacesAroundEachColumnWord;
            toPrint.append(ConsolePrinter.getGreenMsg(sideWall)).append(" ".repeat(numSpacesAroundEachColumnWord)).append(currentElement).append(" ".repeat(count));

            // Add end of frame symbol
            if (i == elementsLength - 1) {
                toPrint.append(ConsolePrinter.getGreenMsg(sideWall));
            }
        }
        System.out.println(toPrint);
    }

    static void printElementsTable(List<String> myList, int[] maxColumnLengths) {
        int numSpacesAroundEachColumnWord = 2;
        int maxNumberOfSymbolsAllRows = getMaxNumberOfSymbolsAllRows(maxColumnLengths);
        int numberOfColumns = maxColumnLengths.length; // todo
        int numAddedSpaces = numberOfColumns * 2 * numSpacesAroundEachColumnWord;
        // top frame len = maxColumnLength + (numberOfColumns +1) (1 symbol each column + sides) + numAddedSpaces
        int frameLength = maxNumberOfSymbolsAllRows + numAddedSpaces + numberOfColumns + 1;
        System.out.println("_".repeat(frameLength));
        for (String row : myList) {
            String[] elements = getRowElementsTrimmed(row);

            for (int i = 0; i < elements.length; i++) {
                String currentElement = elements[i];
                int maxLengthCurrentPosition = maxColumnLengths[i];

                System.out.print("|" + " ".repeat(numSpacesAroundEachColumnWord) + currentElement +
                        " ".repeat(maxLengthCurrentPosition - currentElement.length() + numSpacesAroundEachColumnWord));
                if (i == elements.length - 1) {
                    System.out.println("|");
                }
            }
        }
        System.out.println("_".repeat(frameLength));
    }

    static String getTopLineOfMenuTable(int[] maxColumnLengths) {
        return getGreenLineTable(topLeftCorner, topRightCorner, topCross, maxColumnLengths);
    }

    /**
     * |      0 - Exit                                   |
     * |      1 - Login                                  |
     *
     * @param frameLength - longest row data + min spaces on each side + 2
     * @param rowData     - the actual data that needs to be printed "0 - Exit"
     */
    static void printMiddleMenuLine(int frameLength, String rowData, int numberOfSymbolsFromLeftWall) {
        String coloredFrameAndSpacesBeginningOfRow = ConsolePrinter.getGreenMsg(sideWall + " ".repeat(numberOfSymbolsFromLeftWall));
        System.out.print(coloredFrameAndSpacesBeginningOfRow + rowData);
        if (numberOfSymbolsFromLeftWall == MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU) {
            System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + sideWall));
        } else {
            int diff = MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU - numberOfSymbolsFromLeftWall;
            frameLength = frameLength + diff;  // add more spaces when printing the frame so that the right wall is in the correct place.
            System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + sideWall));
        }
    }

    static void printMiddleMenuLine(int frameLength, String rowData) {
        printMiddleMenuLine(frameLength, rowData, MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU);
    }

    private static int getNumberOfRemainingSpacesToTheEndOfTheFrame(int frameLength, String rowData) {
        return (frameLength - (getStringLengthWithoutANSI(rowData) + MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU + 2));
    }

    private static int getLengthOfTheLongestStringInList(List<String> list) {
        int longest = 0;
        for (String row : list) {
            int currentLength = getStringLengthWithoutANSI(row);
            if (currentLength > longest) {
                longest = currentLength;
            }
        }
        return longest;
    }

    private static int getStringLengthWithoutANSI(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    static int getTheNumberOfSymbolsInTheLongestString(String str, List<String> listOfStrings) {
        // Create a new list that combines the current list and the string and get the length of the longest one.
        List<String> menuQuestionAndOptions = new ArrayList<>(listOfStrings);
        menuQuestionAndOptions.add(str);
        return getLengthOfTheLongestStringInList(menuQuestionAndOptions);
    }

}
