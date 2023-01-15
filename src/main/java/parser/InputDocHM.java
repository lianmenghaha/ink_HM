package parser;

import java.util.ArrayList;

public class InputDocHM extends InputDoc {

    ArrayList<Solution> solutions;

    public InputDocHM() {
        this.solutions = new ArrayList<>();
    }

    public ArrayList<Solution> getSolutions() {
        return solutions;
    }

    public void addToSolutions(Solution solution) {
        this.solutions.add(solution);
    }
}
