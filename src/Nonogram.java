import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class Nonogram {

    private State state;
    private int n;
    ArrayList<ArrayList<Integer>> row_constraints;
    ArrayList<ArrayList<Integer>> col_constraints;

    public Nonogram(State state, 
        ArrayList<ArrayList<Integer>> row_constraints, 
        ArrayList<ArrayList<Integer>> col_constraints) {
        this.state = state;
        this.n = state.getN();
        this.row_constraints = row_constraints;
        this.col_constraints = col_constraints;
    }


    public void start() {
        long tStart = System.nanoTime();
        backtrack(state);
        long tEnd = System.nanoTime();
        System.out.println("Total time: " + (tEnd - tStart)/1000000000.000000000);
    }

    private boolean backtrack(State state) {

        if (isFinished(state)) {

            System.out.println("Result Board: \n");
            state.printBoard();
            return true;
        }
        if (allAssigned(state)) {
            return false;
        }

        int[] mrvRes = MRV(state);
        ForwardChecking(mrvRes, state);
        ac3(mrvRes,state);
        for (String s : LCV(state, mrvRes)) {
            State newState = state.copy();

            newState.setIndexBoard(mrvRes[0], mrvRes[1], s);
            newState.removeIndexDomain(mrvRes[0], mrvRes[1], s);

            if (!isConsistent(newState)) {
                newState.removeIndexDomain(mrvRes[0], mrvRes[1], s);
                continue;
            }

            if (backtrack(newState)) {
                return true;
            }
        }


        return false;
    }

    public void ForwardChecking(int[] status, State state){

        if(state.getDomain().get(status[0]).get(status[1]).size()<2){
            System.out.println("ok");
            return;
        }

        ArrayList<Integer> i_constraint = Main.row_constraints.get(status[0]);
        ArrayList<Integer> j_constraint = Main.col_constraints.get(status[1]);

        boolean s = true;
        int count;
        int index = 0;

        for (int k : i_constraint) {
            count = 0;
            int j;
            for(j = index;j<state.getN(); j++){
                if(count>0 && state.getBoard().get(status[0]).get(j) == "X"){
//                    index = j ;
                    break;
                }
                if(state.getBoard().get(status[0]).get(j) == "F")
                    count++;
            }
            index = j;
            if(count != k) {
                s = false;
                break;
            }

        }
        if(s){
            for(int j=0;j<state.getN();j++){
                if(state.getBoard().get(status[0]).get(j).equals("E")){
                    state.setIndexBoard(status[0], j, "X");
                    state.removeIndexDomain(status[0], j, "F");
                }
            }
        }

        index = 0;
        s = true;
        for(int k : j_constraint){
            count = 0;
            int i;
            for(i=index;i<state.getN();i++){
                if (count > 0 && state.getBoard().get(i).get(status[1]) == "X") {
                    break;
                }
                if(state.getBoard().get(i).get(status[1]) == "F")
                    count++;

            }
            index = i;
            if(count != k) {
                s = false;
                break;
            }
        }

        if(s){
            for(int i=0;i<state.getN();i++){
                if(state.getBoard().get(i).get(status[1]) == "E"){
                    state.setIndexBoard(i, status[1], "X");
                    state.removeIndexDomain(i, status[1], "F");
                }
            }
        }

    }

    private void ac3(int[] status , State state) {
        if (this.row_constraints.get(status[0]).size() == 1) {
            if ((this.row_constraints.get(status[0]).get(0) * 2) > state.getN()) {
                for (int i = state.getN() - this.row_constraints.get(status[0]).get(0); i < this.row_constraints.get(status[0]).get(0); i++) {
                    state.removeIndexDomain(status[0], i, "X");
                    state.setIndexBoard(status[0], i, "F");
                }
            }
        }
        if (this.col_constraints.get(status[1]).size() == 1) {
            if ((this.col_constraints.get(status[1]).get(0) * 2) > state.getN()) {
                for (int i = state.getN() - this.col_constraints.get(status[1]).get(0); i < this.col_constraints.get(status[1]).get(0); i++) {
                    state.removeIndexDomain(i, status[1], "X");
                    state.setIndexBoard(i, status[1], "F");
                }
            }
        }
        {
            int F = this.row_constraints.get(status[0]).get(0);
            int cnt = 0, max_block = 0;
            boolean F_come = false;
            for (int i = 0; i < status[1]; i++) {
                String a = state.getBoard().get(status[0]).get(i);
                if (a == "F") {
                    cnt++;
                    F_come = true;
                }
                if (a == "E") {
                    cnt++;
                }
                if (a == "X") {
                    cnt = 0;
                }
                if (max_block < cnt) max_block = cnt;
            }
            if (F_come == true && max_block < F) {
                state.removeIndexDomain(status[0], status[1], "X");
                state.setIndexBoard(status[0], status[1], "F");
            }
        }
        {
            int F = this.col_constraints.get(status[1]).get(0);
            int cnt = 0, max_block = 0;
            boolean F_come = false;
            for (int i = 0; i < status[0]; i++) {
                String a = state.getBoard().get(i).get(status[1]);
                if (a == "F") {
                    cnt++;
                    F_come = true;
                }
                if (a == "E") {
                    cnt++;
                }
                if (a == "X") {
                    cnt = 0;
                }
                if (max_block < cnt) max_block = cnt;
            }
            if (F_come == true && max_block < F) {
                state.removeIndexDomain(status[0], status[1], "X");
                state.setIndexBoard(status[0], status[1], "F");
            }
        }
        {
            int F = this.row_constraints.get(status[0]).get(this.row_constraints.get(status[0]).size()-1);
            int cnt = 0, max_block = 0;
            boolean F_come = false;
            for (int i = state.getN()-1; i > status[1]; i--) {
                String a = state.getBoard().get(status[0]).get(i);
                if (a == "F") {
                    cnt++;
                    F_come = true;
                }
                if (a == "E") {
                    cnt++;
                }
                if (a == "X") {
                    cnt = 0;
                }
                if (max_block < cnt) max_block = cnt;
            }
            if (F_come == true && max_block < F) {
                state.removeIndexDomain(status[0], status[1], "X");
                state.setIndexBoard(status[0], status[1], "F");
            }
        }
        {
            int F = this.col_constraints.get(status[1]).get(this.col_constraints.get(status[1]).size()-1);
            int cnt = 0, max_block = 0;
            boolean F_come = false;
            for (int i = state.getN()-1; i > status[0]; i--) {
                String a = state.getBoard().get(i).get(status[1]);
                if (a == "F") {
                    cnt++;
                    F_come = true;
                }
                if (a == "E") {
                    cnt++;
                }
                if (a == "X") {
                    cnt = 0;
                }
                if (max_block < cnt) max_block = cnt;
            }
            if (F_come == true && max_block < F) {
                state.removeIndexDomain(status[0], status[1], "X");
                state.setIndexBoard(status[0], status[1], "F");
            }
        }
    }
    private ArrayList<String> LCV (State state, int[] var) {
        if(state.getDomain().get(var[0]).get(var[1]).size()<2) {
            return state.getDomain().get(var[0]).get(var[1]);
        }

        int row = 0;
        for(int k : this.row_constraints.get(var[0])) {
            row += k;
        }
        int col = 0;
        for(int k : this.col_constraints.get(var[1])){
            col += k;
        }

        int count = 0;
        boolean FisOk = false;
        for (int i = 0; i < state.getN(); i++) {
            if(state.getBoard().get(var[0]).get(i) == "F")
                count++;
        }
        if (count < row) {
            FisOk = true;
        }
        count = 0;
        for (int i = 0; i < state.getN(); i++) {
            if(state.getBoard().get(i).get(var[1]) == "F")
                count++;
        }

        if (count < col) {
            FisOk = (FisOk && true);
        }


        if(!FisOk) {
            ArrayList<String> res = new ArrayList<>();
            res.add("X");
            return res;
        }


        double sum = 0;
        for (int k : this.row_constraints.get(var[0])) {
            sum += k;
        }


        for (int k : this.col_constraints.get(var[1])) {
            sum += k;
        }

        double F = 0;
        double E = 0;
        for (int i = 0; i < state.getN(); i++) {
            if(state.getBoard().get(i).get(var[1]) == "E") {
                E++;
            } else if (state.getBoard().get(i).get(var[1]) == "F") {
                F++;
            }
        }

        for (int i = 0; i < state.getN(); i++) {
            if(state.getBoard().get(var[0]).get(i) == "E") {
                E++;
            } else if (state.getBoard().get(var[0]).get(i) == "F") {
                F++;
            }
        }

        double size = state.getN() * 2 - 1;
        double probability = (sum - F)/ E;
//        System.out.println(probability);
        ArrayList<String> res = new ArrayList<>();
        if (probability > 0.3) {
            res.add("F");
            res.add("X");
        }else {
            res.add("X");
            res.add("F");
        }
        return res;

    } 

    private int[] MRV (State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        ArrayList<ArrayList<ArrayList<String>>> cDomain = state.getDomain();

        int min = Integer.MAX_VALUE;
        int[] result = new int[2];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("E")) {
                    int val = cDomain.get(i).get(j).size();
                    if (val < min) {
                        min = val;
                        result[0] = i;
                        result[1] = j;
                    }
                }
            }
        }
        return result;
    }

    private boolean allAssigned(State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String s = cBoard.get(i).get(j);
                if (s.equals("E"))
                    return false;
            }
        }
        return true;
    }

    
    private boolean isConsistent(State state) {
        
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        //check row constraints
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int x : row_constraints.get(i)) {
                sum += x;
            }
            int count_f = 0;
            int count_e = 0;
            int count_x = 0;
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    count_f++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    count_e++;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    count_x++;
                }
            }

            if (count_x > n - sum) {
                return false;
            }
            if (count_f != sum && count_e == 0) {
                return false;
            }
            
            Queue<Integer> constraints = new LinkedList<>();
            constraints.addAll(row_constraints.get(i));
            int count = 0;
            boolean flag = false;
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    flag = true;
                    count++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    break;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    if (flag) {
                        flag = false;
                        if (!constraints.isEmpty()){
                            if (count != constraints.peek()) {
                                return false;
                            }
                            constraints.remove();
                        }
                        count = 0;
                    }
                }
            }

        }

        //check col constraints

        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int x : col_constraints.get(j)) {
                sum += x;
            }
            int count_f = 0;
            int count_e = 0;
            int count_x = 0;
            for (int i = 0; i < n; i++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    count_f++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    count_e++;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    count_x++;
                }
            }
            if (count_x > n - sum) {
                return false;
            }
            if (count_f != sum && count_e == 0) {
                return false;
            }
            
            Queue<Integer> constraints = new LinkedList<>();
            constraints.addAll(col_constraints.get(j));
            int count = 0;
            boolean flag = false;
            for (int i = 0; i < n; i++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    flag = true;
                    count++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    break;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    if (flag) {
                        flag = false;
                        if (!constraints.isEmpty()){
                            if (count != constraints.peek()) {
                                return false;
                            }
                            constraints.remove();
                        }
                        count = 0;
                    }
                }
            }
        }
        return true;
    }

    private boolean isFinished(State state) {
        return allAssigned(state) && isConsistent(state);
    }

}
