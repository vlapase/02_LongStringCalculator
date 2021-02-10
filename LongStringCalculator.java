import java.util.*;

// some shorty calc, not longer then 500 code rows :-)
public class LongStringCalculator {

    /* Sample test:
    // Prog arg: "- ( 1 + 2 * 3 / 4 ^ 5 + ( -6 * 7 / ( cos ( 8 ) ^ 9 + sin ( tan ( atan ( log2 ( 10 ) ^ 11 ) / 12 ) * 13 ) + 14 - 15 * 16 ) ) ^ 17 - 18 + ( -19 ^ ( - 20 ) ) * ( -21 ) + 22 ^ 23 + tan ( 24 ) - sqrt ( 25 ) - 26 + 27 ^ 28 / 29 - 30 ) / b1 ^ a + sqrt ( sqrt ( 625 ) )" "a = 36" "b1 = 31"
    // Calculated expression result: 4.999999999999999
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static String formula = "";
    public static HashMap<String, Double> variables = new HashMap<>();

    // this variable is using for args number check
    public static int argsNumber = 0;

    // this variable is using for tracing number check
    public static int trace = 0;

    public static void main(String[] args) {

        System.out.println("pre-Tracing. Step #" + (trace++) + " Program arguments is: " + Arrays.toString(args));

        // taking formula catching "no Program arguments" exception
        try {
            formula = args[0];
        } catch (Exception e) {
            System.out.println("Sorry, no Program arguments added, there is nothing to calculate.");
            System.exit(1);
        }

        System.out.println("pre-Tracing. Step #" + (trace++) + " It means formula is: " + Arrays.toString(new String[]{formula}));


        // hashmap for comparison
        Set<Object> operatorsExceptMinus = new HashSet<>(Arrays.asList('^', '*', '/', '+'));

        // hashmap for unary functions
        //noinspection SpellCheckingInspection
        Set<Object> unaryFunctions = new HashSet<>(Arrays.asList(
                "sin", "cos", "tan", "atan", "log10", "log2", "sqrt"));

        // split formula
        String[] formulaAsStringArray = formula.split(" ");

        // formula taken. STARTING MAIN CYCLE
        //noinspection InfiniteLoopStatement
        while (true) MainCycle(args, operatorsExceptMinus, unaryFunctions, formulaAsStringArray);
    }

    /**
     * Iterable cycle for parsed formula
     *
     * @param args                 args to work with
     * @param operatorsExceptMinus added for comparison
     * @param unaryFunctions       added for comparison
     * @param formulaAsStringArray formula to work with
     */
    private static void MainCycle(String[] args, Set<Object> operatorsExceptMinus, Set<Object> unaryFunctions, String[] formulaAsStringArray) {

        trace = 0;
        argsNumber = 0;
        ArgsGetAndErrorCheck(args);
        // taking temporary instance of formula
        String[] currentFormulaAsStringArray = formulaAsStringArray.clone();

        // add variables values to formula
        for (int i = 0; i < currentFormulaAsStringArray.length; i++) {
            for (Map.Entry<String, Double> entry : variables.entrySet()) {
                String k = entry.getKey();
                Double v = entry.getValue();
                if (currentFormulaAsStringArray[i].equals(k)) {
                    currentFormulaAsStringArray[i] = String.valueOf(v);
                    argsNumber--;
                }
            }
        }

        // args quantity error check
        if (argsNumber > 0) {
            System.out.println("Sorry, missing " + argsNumber + " variable(s) in formula, there is nothing to calculate.");
            System.exit(1);
        }

        // formula converted to ArrayList
        List<String> formulaAsStringArrayList = new ArrayList<>();
        Collections.addAll(formulaAsStringArrayList, currentFormulaAsStringArray);

        UnaryMinusImplement(operatorsExceptMinus, formulaAsStringArrayList);

        System.out.println("Tracing. Step #" + (trace++) + " UNARY MINUS PHASE DONE. Parsed formula is: " + formulaAsStringArrayList);


        BracesProcessor(operatorsExceptMinus, unaryFunctions, formulaAsStringArrayList);

        System.out.println(" ");
        System.out.println("Tracing. Step #" + (trace++) + " FINAL STEP. NO PAIRED BRACES LEFT.");
        System.out.println("Tracing. Step #" + (trace++) + " Parsed formula is: " + formulaAsStringArrayList);


        // final calculation start with no braces and unary functions
        binaryCalc(operatorsExceptMinus, formulaAsStringArrayList);

        System.out.println("Tracing. Step #" + (trace++) + " Parsed formula is: " + formulaAsStringArrayList);

        double currentResult = Double.parseDouble(formulaAsStringArrayList.get(0));

        System.out.println(" ");
        System.out.println("Calculated expression result: " + currentResult);

        // out if there is no variables
        if (args.length == 1) System.exit(1);

        // variables change cycle
        int i = 1;
        while (i < args.length) {
            String[] argAsStringArray = args[i].split(" ");
            Scanner scanner = new Scanner(System.in);
            System.out.println("To EXIT enter some letter, etc. (Q, for example)");
            System.out.println("OR PLEASE ENTER NEW DIGITAL VALUE FOR VARIABLE: --> " + argAsStringArray[0]);
            argAsStringArray[2] = scanner.nextLine();
            System.out.print("You entered variable: --> " + argAsStringArray[0] + " <-- value: --> " + argAsStringArray[2] + " <--  ");
            try {
                if (Character.isLetter(argAsStringArray[2].charAt(0))) {
                    System.out.println("That way calculation is finished, thank you!");
                    System.out.println("Last calculated expression result: " + currentResult);
                    System.exit(0);
                }
            } catch (Exception e) {
                System.out.println("That way calculation is finished, thank you!");
                System.out.println("Last calculated expression result: " + currentResult);
                System.exit(0);
            }
            try {
                Double.parseDouble(argAsStringArray[2]);
            } catch (NumberFormatException e) {
                System.out.println("That way calculation is finished, thank you!");
                System.out.println("Last calculated expression result: " + currentResult);
                System.exit(0);
            }
            args[i] = argAsStringArray[0] + " = " + argAsStringArray[2];
            i++;
        }
    }

    /**
     * Process braces and unary functions
     *
     * @param operatorsExceptMinus     added for comparison
     * @param unaryFunctions           added for comparison
     * @param formulaAsStringArrayList formula to work with
     */
    private static void BracesProcessor(Set<Object> operatorsExceptMinus, Set<Object> unaryFunctions, List<String> formulaAsStringArrayList) {
        boolean bracePresent = false;
        do {
            int lastPosition = -1;
            int firstPosition = -1;
            for (int i = 0; i < formulaAsStringArrayList.size(); i++) {
                String currentString = formulaAsStringArrayList.get(i);
                if (currentString.equals("(")) {
                    firstPosition = i;
                }
                if (currentString.equals(")") && firstPosition >= 0) {
                    lastPosition = i;
                    // two braces found. flag is on
                    bracePresent = true;
                }

                // unary function start
                if (lastPosition - firstPosition == 2) {
                    System.out.println("Tracing. Step #" + (trace++) + " Parsed formula is: " + formulaAsStringArrayList);
                    System.out.println("Tracing. Step #" + (trace++) + " current position is: " + i);
                    formulaAsStringArrayList.remove(lastPosition);
                    formulaAsStringArrayList.remove(firstPosition);
                    if (firstPosition > 0 && unaryFunctions.contains(formulaAsStringArrayList.get(i - 3))) {
                        unaryCalc(formulaAsStringArrayList.subList(lastPosition - 3, lastPosition - 1));
                    }
                    // try to implement unary "-"
                    UnaryMinusImplement(operatorsExceptMinus, formulaAsStringArrayList);

                    // roll back all to initial position
                    System.out.println("Tracing. Step #" + (trace++) + " first BRACE is: " + firstPosition);
                    System.out.println("Tracing. Step #" + (trace++) + " last BRACE is: " + lastPosition);
                    // two braces erased. flag is off
                    bracePresent = false;
                    lastPosition = -1;
                    firstPosition = -1;
                    i = -1;
                }

                // process part of formula inside braces
                if (firstPosition >= 0 && lastPosition > 3) {
                    System.out.println("Tracing. Step #" + (trace++) + " Parsed formula is: " + formulaAsStringArrayList);
                    System.out.println("Tracing. Step #" + (trace++) + " current position is: " + i);
                    binaryCalc(operatorsExceptMinus, formulaAsStringArrayList.subList(firstPosition + 1, lastPosition));
                    System.out.println("Tracing. Step #" + (trace++) + " first BRACE is: " + firstPosition);
                    System.out.println("Tracing. Step #" + (trace++) + " last BRACE is: " + lastPosition);

                    // roll back all to initial position
                    bracePresent = false;
                    lastPosition = -1;
                    firstPosition = -1;
                    i = -1;
                }
            }
        } while (bracePresent);
    }

    /**
     * Unary "-" implementation
     *
     * @param operatorsExceptMinus     added for comparison
     * @param formulaAsStringArrayList formula to work with
     */
    private static void UnaryMinusImplement(Set<Object> operatorsExceptMinus, List<String> formulaAsStringArrayList) {

        for (int i = 0; i < formulaAsStringArrayList.size() - 1; i++) {
            String currentString = formulaAsStringArrayList.get(i);
            String nextString = formulaAsStringArrayList.get(i + 1);
            String pastNextString;
            String previousString;

            // processing minus: ( - 1 ) or ( - 1 "operator"
            if (currentString.equals("-") && i > 0 && i < formulaAsStringArrayList.size() - 2) {
                previousString = formulaAsStringArrayList.get(i - 1);
                pastNextString = formulaAsStringArrayList.get(i + 2);
                if ((previousString.equals("(") && (pastNextString.equals(")") || pastNextString.equals("-") ||
                        operatorsExceptMinus.contains(pastNextString.charAt(0))))) {
                    double signSwitcher = 0.0;
                    try {
                        signSwitcher = -Double.parseDouble(formulaAsStringArrayList.get(i + 1));
                    } catch (NumberFormatException e) {
                        System.out.println("Sorry, variable number " + (i + 1) + " value is not Double. There is nothing to calculate.");
                        System.exit(1);
                    }
                    formulaAsStringArrayList.set(i + 1, String.valueOf(signSwitcher));
                    //noinspection SuspiciousListRemoveInLoop
                    formulaAsStringArrayList.remove(i);
                }
            }
            // processing first minus in string or minus before left brace
            if (currentString.equals("-") && (i == 0 ||
                    operatorsExceptMinus.contains(formulaAsStringArrayList.get(i - 1).charAt(0)))) {
                if (!nextString.equals("(")) {
                    double signSwitcher = 0.0;
                    try {
                        signSwitcher = -Double.parseDouble(formulaAsStringArrayList.get(i + 1));
                    } catch (NumberFormatException e) {
                        System.out.println("Sorry, variable number " + (i + 1) + " value is not Double. There is nothing to calculate.");
                        System.exit(1);
                    }
                    formulaAsStringArrayList.set(i + 1, String.valueOf(signSwitcher));
                    //noinspection SuspiciousListRemoveInLoop
                    formulaAsStringArrayList.remove(i);
                } else {
                    formulaAsStringArrayList.set(i, "~");
                }
            }
            // processing tilda to minus after braces calculated
            if (currentString.equals("~") && !nextString.equals("(")) {
                double signSwitcher = 0.0;
                try {
                    signSwitcher = -Double.parseDouble(formulaAsStringArrayList.get(i + 1));
                } catch (NumberFormatException e) {
                    System.out.println("Sorry, variable number " + (i + 1) + " value is not Double. There is nothing to calculate.");
                    System.exit(1);
                }
                formulaAsStringArrayList.set(i + 1, String.valueOf(signSwitcher));
                //noinspection SuspiciousListRemoveInLoop
                formulaAsStringArrayList.remove(i);
            }
        }
    }

    /**
     * main unary calculation cycle
     *
     * @param functionAndOperandAsStringArrayList function and operand
     */

    private static void unaryCalc(List<String> functionAndOperandAsStringArrayList) {

        String function = functionAndOperandAsStringArrayList.get(0);
        double argument = 1;
        try {
            argument = Double.parseDouble(functionAndOperandAsStringArrayList.get(1));
        } catch (NumberFormatException e) {
            System.out.println("Sorry, argument of the function " + function + " value is not Double. There is nothing to calculate.");
            System.exit(1);
        }
        double result = 0;
        System.out.println("Tracing. Step #" + (trace++) + " BEFORE " + functionAndOperandAsStringArrayList);

        try {
            switch (function) {
                case ("sin"):
                    result = Math.sin(argument);
                    break;
                case ("cos"):
                    result = Math.cos(argument);
                    break;
                case ("tan"):
                    result = Math.tan(Math.toRadians(argument));
                    break;
                //noinspection SpellCheckingInspection
                case ("atan"):
                    result = Math.atan(Math.toRadians(argument));
                    break;
                case ("log10"):
                    if (argument >= 0) result = Math.log10(argument);
                    else {
                        System.out.println("Sorry, can not take log10 of negative number, there is nothing to calculate.");
                        System.exit(1);
                    }
                    break;
                case ("log2"):
                    if (argument >= 0) result = Math.log(argument) / Math.log(2);
                    else {
                        System.out.println("Sorry, can not take log2 of negative number, there is nothing to calculate.");
                        System.exit(1);
                    }
                    break;
                case ("sqrt"):
                    if (argument >= 0) result = Math.sqrt(argument);
                    else {
                        System.out.println("Sorry, can not take sqrt of negative number, there is nothing to calculate.");
                        System.exit(1);
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Sorry, unary function argument " + argument + " causes error. There is nothing to calculate.");
            System.exit(1);
        }
        functionAndOperandAsStringArrayList.set(0, String.valueOf(result));
        functionAndOperandAsStringArrayList.remove(1);

        System.out.println("Tracing. Step #" + (trace++) + " AFTER " + functionAndOperandAsStringArrayList);
    }

    /**
     * main binary calculation cycle
     *
     * @param operatorsExceptMinus           added for comparison
     * @param partOfFormulaAsStringArrayList formula to work with
     */
    private static void binaryCalc(Set<Object> operatorsExceptMinus, List<String> partOfFormulaAsStringArrayList) {

        FormulaErrorCheck(operatorsExceptMinus, partOfFormulaAsStringArrayList);

        // priority 0 operation: ^
        // priority 1 operations: * and /
        // priority 2 operations: + and -
        for (int priority = 0; priority < 3; priority++) {

            for (int i = 1; i < partOfFormulaAsStringArrayList.size() - 1; i += 2) {

                boolean operationDoneFlag = false;
                String currentString = partOfFormulaAsStringArrayList.get(i);
                double arg1 = Double.parseDouble(partOfFormulaAsStringArrayList.get(i - 1));
                double arg2 = Double.parseDouble(partOfFormulaAsStringArrayList.get(i + 1));

                // priority 0
                if (priority == 0 && currentString.equals("^")) {
                    double result = Math.pow(arg1, arg2);
                    partOfFormulaAsStringArrayList.set(i - 1, String.valueOf(result));
                    operationDoneFlag = true;
                }

                // priority 1
                if (priority == 1 && (currentString.equals("*") || currentString.equals("/"))) {

                    if (currentString.equals("/") && arg2 == 0) {
                        System.out.println("Sorry, found zero division error in formula, there is nothing to calculate.");
                        System.exit(1);
                    }
                    double result = currentString.equals("*") ? arg1 * arg2 : arg1 / arg2;
                    partOfFormulaAsStringArrayList.set(i - 1, String.valueOf(result));
                    operationDoneFlag = true;
                }

                // priority 2
                if (priority == 2 && (currentString.equals("+") || currentString.equals("-"))) {

                    double result = currentString.equals("+") ? arg1 + arg2 : arg1 - arg2;
                    partOfFormulaAsStringArrayList.set(i - 1, String.valueOf(result));
                    operationDoneFlag = true;
                }

                // removing operator & argument
                if (operationDoneFlag) {
                    partOfFormulaAsStringArrayList.remove(i + 1);
                    partOfFormulaAsStringArrayList.remove(i);
                    i -= 2;
                    System.out.println("Tracing. Step #" + (trace++) + " part AFTER CALC: " + partOfFormulaAsStringArrayList);

                }
            }
        }
    }

    /**
     * variables data get & error check
     *
     * @param args variables data
     */
    private static void ArgsGetAndErrorCheck(String[] args) {
        // args get
        argsNumber = args.length - 1;
        int i = 1;
        while (i < args.length) {
            String[] argAsStringArray = args[i].split(" ");
            try {
                //noinspection unused
                char testChar = argAsStringArray[0].charAt(0);
            } catch (Exception e) {
                System.out.println("Sorry, variable number " + i + " name is not readable. There is nothing to calculate.");
                System.exit(1);
            }
            if (!Character.isLetter(argAsStringArray[0].charAt(0))) {
                System.out.println("Sorry, variable number " + i + " name NOT starts from letter. There is nothing to calculate.");
                System.exit(1);
            }
            if (argAsStringArray.length < 3) {
                System.out.println("Sorry, variable number " + i + " value is not readable. There is nothing to calculate.");
                System.exit(1);
            }
            try {
                Double.parseDouble(argAsStringArray[2]);
            } catch (NumberFormatException e) {
                System.out.println("Sorry, variable number " + i + " value is not Double. There is nothing to calculate.");
                System.exit(1);
            }

            // adding variables if there is no errors
            variables.put(argAsStringArray[0], Double.parseDouble(argAsStringArray[2]));
            System.out.println("Tracing. Step #" + (trace++) + " It means variable " + argAsStringArray[0] + " is: " + argAsStringArray[2]);
            i++;
        }
    }

    /**
     * formula errors processor
     *
     * @param operatorsExceptMinus     added for comparison
     * @param formulaAsStringArrayList formula to work with
     */
    private static void FormulaErrorCheck(Set<Object> operatorsExceptMinus, List<String> formulaAsStringArrayList) {
        for (int i = 0; i < formulaAsStringArrayList.size(); i++) {
            String s = formulaAsStringArrayList.get(i);
            if (i % 2 == 0) {
                try {
                    Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    System.out.println("Sorry, formula argument number " + (i + 1) + " is not Double. There is nothing to calculate.");
                    System.exit(1);
                }
            } else {
                try {
                    Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    char testChar = formulaAsStringArrayList.get(i).charAt(0);
                    if (!(operatorsExceptMinus.contains(testChar) || testChar == '-')) {
                        System.out.println("Sorry, formula argument number " + (i + 1) + " is not operator. There is nothing to calculate.");
                        System.exit(1);
                    }
                }
            }
        }
    }
}