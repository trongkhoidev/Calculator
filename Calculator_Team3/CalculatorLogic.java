package Calculator_Team3;

import java.util.Arrays;
import java.util.Stack;

/**
 * Handles the calculation logic for the calculator application.
 * Processes mathematical expressions, supports different number radixes,
 * and provides various mathematical operations.
 */
public class CalculatorLogic {
    private boolean isError = false;
	private String varString[] = { "ans" };
	private String constString[] = { "pi", "π", "e" }; // Array of constant names
	public double var[] = new double[varString.length];
	private double cons[] = { Math.PI, Math.PI, Math.E };
	private boolean isDegOrRad = true; // true for degrees, false for radians
	private int radix = 10, sizeRound = 10;
	private FormatValue formatValue = new FormatValue();

	/**
	 * Checks if an error has occurred during calculation.
	 * @return true if an error has occurred, false otherwise
	 */
	public boolean isError() {
		return isError;
	}

	/**
	 * Sets the error state.
	 * @param isError the error state to set
	 */
	public void setError(boolean isError) {
		this.isError = isError;
	}

	/**
	 * Gets the number of decimal places for rounding.
	 * @return the decimal place count for rounding
	 */
	public int getSizeRound() {
		return sizeRound;
	}

	/**
	 * Sets the number of decimal places for rounding.
	 * @param sizeRound the decimal place count to set
	 */
	public void setSizeRound(int sizeRound) {
		this.sizeRound = sizeRound;
	}

	/**
	 * Gets the current number radix (base).
	 * @return the current radix value (2, 8, 10, or 16)
	 */
	public int getRadix() {
		return radix;
	}

	/**
	 * Sets the number radix (base) for calculations.
	 * @param radix the radix to set (2 for binary, 8 for octal, 10 for decimal, 16 for hexadecimal)
	 */
	public void setRadix(int radix) {
		this.radix = radix;
	}

	/**
	 * Checks if angle mode is set to degrees or radians.
	 * @return true if degrees, false if radians
	 */
	public boolean isDegOrRad() {
		return isDegOrRad;
	}

	/**
	 * Sets the angle mode (degrees or radians).
	 * @param isDegOrRad true for degrees, false for radians
	 */
	public void setDegOrRad(boolean isDegOrRad) {
		this.isDegOrRad = isDegOrRad;
	}

	/**
	 * Checks if a number is an integer (has no decimal part).
	 * @param num the number to check
	 * @return true if the number is an integer, false otherwise
	 */
	protected boolean isIntegerNumber(double num) {
		long a = (long) num;
		if (a == num) {
			return true;
		}
		return false;
	}
	
	/**
	 * Rounds a double number to a specified number of decimal places and returns it as a string.
	 * @param num the number to round
	 * @param size the maximum total number of digits to display
	 * @return the formatted string representation of the number
	 */
	private String myRound(double num, int size) {
		if (isIntegerNumber(num)) {
			return Long.toString((long) num);
		} else {
			int n = size - Long.toString((long) num).length();
			num = Math.round(num * Math.pow(10, n)) / Math.pow(10, n);
			if (isIntegerNumber(num)) {
				return Long.toString((long) num);
			} else {
				return Double.toString(num);
			}
		}
	}

	/**
	 * Calculates the factorial of a number: n!
	 * @param num the number to calculate factorial for
	 * @return the factorial result, or -1 if input is negative
	 */
	private long factorial(int num) {
		if (num >= 0) {
			long result = 1;
			for (int i = 1; i <= num; i++) {
				result *= i;
			}
			return result;
		}
		return -1;
	}

	/**
	 * Calculates the permutation: P(a,b) = a!/(a-b)! = (a-b+1)(a-b+2)...a
	 * @param a the total number of elements
	 * @param b the number of elements to select
	 * @return the permutation result, or -1 if invalid input
	 */
	private long permutation(int a, int b) {
		if (a < b) {
			return -1;
		}
		if (a >= 0 && b >= 0) {
			long result = 1;
			int c = a - b;
			for (int i = c + 1; i <= a; i++) {
				result *= i;
			}
			return result;
		}
		return -1;
	}

	/**
	 * Calculates the combination: C(a,b) = a!/[b!(a-b)!]
	 * @param a the total number of elements
	 * @param b the number of elements to select
	 * @return the combination result, or -1 if invalid input
	 */
	private long combination(int a, int b) {
		if (a < b) {
			return -1;
		}
		if (a >= 0 && b >= 0) {
			long result = 1;
			int c = a - b;
			if (c > b) {
				int temp = c;
				c = b;
				b = temp;
			}
			for (int i = b + 1; i <= a; i++) {
				result *= i;
			}
			result /= factorial(c);
			return result;
		}
		return -1;
	}
	
	/**
	 * Converts an angle from radians to degrees.
	 * @param num the angle in radians
	 * @return the angle in degrees
	 */
	private double convertToDeg(double num) {
		num = num * 180 / Math.PI;
		return num;
	}
	
	/**
	 * Converts an angle from degrees to radians.
	 * @param num the angle in degrees
	 * @return the angle in radians
	 */
	private double convertToRad(double num) {
		num = num * Math.PI / 180;
		return num;
	}

	/**
	 * Checks if a string represents a valid number (including variables and constants).
	 * @param s the string to check
	 * @return true if the string represents a valid number, false otherwise
	 */
	protected boolean isNumber(String s) {
		if (radix != 10 && formatValue.isRadixString(s, radix)) {
			return true;
		}
		if (isVarOrConst(s)) {
			return true;
		}
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if a character is a valid digit in the current radix.
	 * @param c the character to check
	 * @return true if the character is a valid digit in the current radix, false otherwise
	 */
	private boolean isNumber(char c) {
		String numberChar = ".0123456789abcdef";
		int index = numberChar.indexOf(c);
		if (radix == 10 && index >= 0 && index <= 10) {
			System.out.println(c + " is number");
			return true;
		}
		if (radix == 16 && index >= 0) {
			System.out.println(c + " is number");
			return true;
		}
		if (radix == 8 && index >= 0 && index <= 8) {
			System.out.println(c + " is number");
			return true;
		}
		if (radix == 2 && index >= 0 && index <= 2) {
			System.out.println(c + " is number");
			return true;
		}
		System.out.println(c + " isn't number");
		return false;
	}

	/**
	 * Converts a string to its numeric value, handling variables and constants.
	 * @param s the string to convert
	 * @return the numeric value of the string
	 */
	private double stringToNumber(String s) {
		int index = indexVar(s);
		if (index >= 0) {
			return var[index];
		}
		index = indexConst(s);
		if (radix != 10) {
			if (formatValue.isRadixString(s, radix)) {
				return formatValue.stringRadixToDouble(s, radix);
			} else {
				isError = true;
				System.out.println("Error number in radix = " + radix);
			}
		}
		if (index >= 0) {
			return cons[index];
		}
		if (s.charAt(s.length() - 1) == '.') {
			isError = true;
			System.out.println("Error number have '.'");
			return -1;
		}
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			isError = true;
			System.out.println("Error parse number");
		}
		return -1;
	}

	/**
	 * Converts a number to its string representation according to current radix and precision.
	 * @param num the number to convert
	 * @param radix the base to use (2, 8, 10, 16)
	 * @param size the precision (number of digits)
	 * @return the string representation of the number
	 */
	public String numberToString(double num, int radix, int size) {
		if (radix != 10) {
			return formatValue.doubleToStringRadix(num, radix, size);
		}
		return myRound(num, size);

	}

	/**
	 * Gets the index of a variable in the varString array.
	 * @param s the variable name to look for
	 * @return the index of the variable, or -1 if not found
	 */
	private int indexVar(String s) {
		for (int i = 0; i < varString.length; i++) {
			if (s.equals(varString[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the index of a constant in the constString array.
	 * @param s the constant name to look for
	 * @return the index of the constant, or -1 if not found
	 */
	private int indexConst(String s) {
		for (int i = 0; i < constString.length; i++) {
			if (s.equals(constString[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if a string represents a variable or constant.
	 * @param s the string to check
	 * @return true if the string is a variable or constant, false otherwise
	 */
	private boolean isVarOrConst(String s) {
		if (indexConst(s) >= 0 || indexVar(s) >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a string represents a mathematical operator.
	 * @param s the string to check
	 * @return true if the string is an operator, false otherwise
	 */
	private boolean isOperator(String s) {
		String operator[] = { "+", "-", "*", "/", "^",
				"~", "√", "sqrt", "!", "%", ")", "(", "²", "sin",
				"cos", "tan", "log", "→", "sto", "°",
				"mod", "and", "or", "xor", "not", "∧", "∨", "⊻", "¬", "<<",
				">>", "≫", "≪", "ln", "1/" };
		Arrays.sort(operator);
		if (Arrays.binarySearch(operator, s) > -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines the precedence of an operator in the order of operations.
	 * Higher value means higher precedence.
	 * @param s the operator
	 * @return the precedence value (higher means higher precedence)
	 */
	private int priority(String s) {
		int p = 1;
		if (s.equals("→") || s.equals("sto")) {
			return p;
		}
		p++;
		if (s.equals("+") || s.equals("-")) {
			return p;
		}
		p++;
		if (s.equals("*") || s.equals("/")) {
			return p;
		}
		p++;
		if (s.equals("and") || s.equals("∧") || s.equals("or") || s.equals("∨")
				|| s.equals("xor") || s.equals("⊻") || s.equals("mod")
				|| s.equals(">>") || s.equals("<<") || s.equals("≫")
				|| s.equals("≪")) {
			return p;
		}
		p++;
		if (s.equals("not") || s.equals("¬")) {
			return p;
		}
		p++;
		if (s.equals("~")) {
			return p;
		}
		p++;
		if (s.equals("sin") || s.equals("cos") || s.equals("tan")
				|| s.equals("log") || s.equals("ln") || s.equals("1/")) {
			return p;
		}
		p++;
		if (s.equals("√") || s.equals("!") || s.equals("^") 
		        || s.equals("²") || s.equals("sqrt") || s.equals("°")) {
			return p;
		}
		p++;
		return 0;
	}

	/**
	 * Checks if an operator is a unary operator (operates on a single operand).
	 * @param c the operator to check
	 * @return true if the operator is unary, false otherwise
	 */
	private boolean isOneMath(String c) {
		String operator[] = { "sin", "cos", "tan", "√", "sqrt", 
		        "(", "~", "not", "¬", "log", "ln", "1/" };
		Arrays.sort(operator);
		if (Arrays.binarySearch(operator, c) > -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if an operator is a postfix operator (appears after its operand).
	 * @param s the operator to check
	 * @return true if the operator is postfix, false otherwise
	 */
	private boolean isPostOperator(String s) {
		String postOperator[] = { "!", "²" };
		for (int i = 0; i < postOperator.length; i++) {
			if (s.equals(postOperator[i])) {
				System.out.println(s + "isPostOperator");
				return true;
			}
		}
		System.out.println(s + "no isPostOperator");
		return false;
	}

	/**
	 * Checks if two characters can form part of a word (function or constant).
	 * @param c1 the first character
	 * @param c2 the second character
	 * @return true if the characters can form part of a word, false otherwise
	 */
	private boolean isWord(char c1, char c2) {
		char word[][] = { { 'p', 'i' },
				{ 's', 'i', 'n' }, { 'c', 'o', 's' }, { 't', 'a', 'n' },
				{ 'a', 'n', 's' }, { 's', 'q', 'r', 't' },
				{ 's', 't', 'o' }, { 'a', 'n', 'd' }, { 'o', 'r' },
				{ 'x', 'o', 'r' }, { 'n', 'o', 't' }, { 'm', 'o', 'd' },
				{ '<', '<' }, { '>', '>' }, { 'l', 'n' }, { '1', '/' } };
		for (int i = 0; i < word.length; i++) {
			for (int j = 0; j < word[i].length; j++) {
				for (int k = j + 1; k < word[i].length; k++) {
					if (c1 == word[i][j] && c2 == word[i][k]) {
						System.out.println("is word: " + c1 + " " + c2);
						return true;
					}
				}
			}
		}
		System.out.println("is'nt word:" + c1 + " " + c2);
		return false;
	}

	/**
	 * Checks if a string is a recognized word (function or constant name).
	 * @param s the string to check
	 * @return true if the string is a recognized word, false otherwise
	 */
	private boolean isWord(String s) {
		String word[] = { "pi", "sin", "cos", "tan", "sqrt", 
		                  "sto", "and", "or", "xor", "not", "mod", 
		                  "<<", ">>", "ans", "ln", "1/" };
		for (int i = 0; i < word.length; i++) {
			if (s.equals(word[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Standardizes a string by removing leading/trailing spaces and reducing multiple spaces to one.
	 * @param s the string to standardize
	 * @return the standardized string
	 */
	private String standardize(String s) {
		s = s.trim();
		s = s.replaceAll("\\s+", " ");
		return s;
	}

	/**
	 * Splits a string into tokens by space.
	 * @param s the string to split
	 * @return array of tokens
	 */
	private String[] trimString(String s) {
		String temp[] = s.split(" ");
		return temp;
	}
	
	/**
	 * Standardizes a mathematical expression by properly formatting operators and operands.
	 * Handles implicit multiplication, parentheses, etc.
	 * @param s array of tokens to standardize
	 * @return standardized mathematical expression
	 */
	private String standardizeMath(String[] s) {
		String s1 = "";

		int open = 0, close = 0;
		for (int i = 0; i < s.length; i++) {
			if (s[i].equals("(")) {
				open++;
			} else if (s[i].equals(")")) {
				close++;
			}
		}

		for (int i = 0; i < s.length; i++) {
			// Convert ")(..." to ")*(...", adding implicit multiplication
			if (i > 0 && isOneMath(s[i])
					&& (s[i - 1].equals(")") || isNumber(s[i - 1]))) {
				s1 = s1 + "* ";
			}
			// Handle consecutive factorial operators like "3!2!"
			if (i > 0 && isPostOperator(s[i - 1]) && isNumber(s[i])) {
				s1 = s1 + "* ";
			}
			// Handle positive signs (+) at the beginning or after other operators
			if ((i == 0 || (i > 0 && !isNumber(s[i - 1])
					&& !s[i - 1].equals(")") && !isPostOperator(s[i - 1])))
					&& (s[i].equals("+"))
					&& (isNumber(s[i + 1]) || s[i + 1].equals("+"))) {
				continue;
			}
			// Handle negative signs (-) as unary operator
			if ((i == 0 || (i > 0 && !isNumber(s[i - 1])
					&& !s[i - 1].equals(")") && !isPostOperator(s[i - 1])))
					&& (s[i].equals("-"))
					&& (isNumber(s[i + 1]) || s[i + 1].equals("-"))) {
				s1 = s1 + "~ ";
			}
			// Handle implicit multiplication like "6π" or "...")π"
			else if (i > 0
					&& ((isNumber(s[i - 1]) || s[i - 1].equals(")")) && isVarOrConst(s[i]))) {
				s1 = s1 + "* " + s[i] + " ";
			} else {
				s1 = s1 + s[i] + " ";
			}
		}

		// Add missing closing parentheses
		for (int i = 0; i < (open - close); i++) {
			s1 += ") ";
		}
		System.out.println("standardizeMath: " + s1);
		return s1;
	}

	/**
	 * Processes the input string and converts it into tokens for calculation.
	 * Breaks down the input into numbers, operators, functions, etc.
	 * @param sMath the mathematical expression to process
	 * @return formatted expression with proper spacing
	 */
	private String processInput(String sMath) {
		sMath = sMath.toLowerCase();
		sMath = standardize(sMath); // Standardize the expression
		String s = "", temp = "";
		for (int i = 0; i < sMath.length(); i++) {
			// If not a number or is part of a word
			if (!isNumber(sMath.charAt(i))
					|| (i < sMath.length() - 1 && isWord(sMath.charAt(i),
							sMath.charAt(i + 1)))) {
				s += " " + temp;
				temp = "" + sMath.charAt(i);
				// If it's an operator and not part of a word
				if (isOperator(sMath.charAt(i) + "") && i < sMath.length() - 1
						&& !isWord(sMath.charAt(i), sMath.charAt(i + 1))) {
					s += " " + temp;
					temp = "";
				} else { // If it's not an operator but part of a word
					i++;
					while (i < sMath.length()
							&& !isNumber(sMath.charAt(i))
							&& (!isOperator(sMath.charAt(i) + ""))
							|| (i < sMath.length() - 1 && isWord(
									sMath.charAt(i - 1), sMath.charAt(i)))) {
						temp += sMath.charAt(i);
						i++;
						if (isWord(temp)) {
							s += " " + temp;
							temp = "";
							break;
						}
					}
					i--;
					s += " " + temp;
					temp = "";
				}
			} else { // If it's a number
				temp = temp + sMath.charAt(i);
			}
		}
		s += " " + temp;

		System.out.println("process input 1 : " + s);
		s = standardize(s);
		s = standardizeMath(trimString(s));
		System.out.println(s);
		return s;
	}

	/**
	 * Converts an infix mathematical expression to postfix notation (Reverse Polish Notation).
	 * Uses the Shunting-yard algorithm.
	 * @param math the infix expression to convert
	 * @return the expression in postfix notation
	 */
	private String postFix(String math) {
		String[] elementMath = trimString(math);

		String s1 = "";
		Stack<String> S = new Stack<String>();
		for (int i = 0; i < elementMath.length; i++) { // Process each token
			if (!isOperator(elementMath[i])) // If not an operator (i.e., an operand)
			{
				s1 = s1 + elementMath[i] + " "; // Add to output
			} else { // If it's an operator
				if (elementMath[i].equals("(")) {
					S.push(elementMath[i]); // Push "(" onto the stack
				} else {
					if (elementMath[i].equals(")")) { // If ")"
						// Pop operators until matching "(" is found
						String temp = "";
						do {
							temp = S.peek();
							if (!temp.equals("(")) {
								s1 = s1 + S.peek() + " "; // Add popped operators to output
							}
							S.pop();
						} while (!temp.equals("("));
					} else {
						// While stack not empty and top operator has higher or equal precedence,
						// pop operators to output
						while (!S.isEmpty()
								&& priority(S.peek()) >= priority(elementMath[i])
								&& !isOneMath(elementMath[i])) {
							s1 = s1 + S.pop() + " ";
						}
						S.push(elementMath[i]); // Push current operator onto stack
					}
				}
			}
		}
		// Pop remaining operators to output
		while (!S.isEmpty()) {
			s1 = s1 + S.pop() + " ";
		}
		System.out.println("CalculatorLogic: " + s1);
		return s1;
	}

	/**
	 * Evaluates a mathematical expression and returns the result.
	 * Converts the expression to postfix notation and evaluates it.
	 * @param math the mathematical expression to evaluate
	 * @return the result of the evaluation
	 */
	public Double valueMath(String math) {
		math = processInput(math);
		math = postFix(math);
		String[] elementMath = trimString(math);
		Stack<Double> S = new Stack<Double>();
		double num = 0.0;
		double ans = 0.0;
		System.out.println("Element math: ");
		for (int i = 0; i < elementMath.length; i++) {
			System.out.print(elementMath[i] + "\t");
			if (!isOperator(elementMath[i])) {
				S.push(stringToNumber(elementMath[i]));
			} else { // Process operators
				if (S.isEmpty()) {
					System.out.println("Stack is empty ^^ ");
					isError = true;
					return 0.0;
				}
				double num1 = S.pop();
				String ei = elementMath[i];
				if (ei.equals("~")) {
					num = -num1; // Unary negation
				} else if (ei.equals("sin")) {
					if (isDegOrRad) {
						num1 = convertToRad(num1); // Convert degrees to radians if needed
					}
					num = Math.sin(num1);
				} else if (ei.equals("cos")) {
					if (isDegOrRad) {
						num1 = convertToRad(num1); // Convert degrees to radians if needed
					}
					num = Math.cos(num1);
				} else if (ei.equals("tan")) {
					if (isDegOrRad) {
						num1 = convertToRad(num1); // Convert degrees to radians if needed
					}
					num = Math.tan(num1);
				} else if (ei.equals("log")) {
					num = Math.log10(num1); // Base-10 logarithm
				} else if (ei.equals("ln")) {
					num = Math.log(num1); // Natural logarithm
				} else if (ei.equals("1/")) {
					if (num1 != 0) {
						num = 1 / num1; // Reciprocal
					} else {
						isError = true;
						return 0.0; // Division by zero
					}
				} else if (ei.equals("%")) {
					num = num1 / 100; // Percentage
				} else if (ei.equals("°")) {
					num = convertToRad(num1); // Convert degrees to radians
				} else if (ei.equals("²")) {
					num = Math.pow(num1, 2); // Square
				} else if (ei.equals("√") || ei.equals("sqrt")) {
					if (num1 >= 0) {
						num = Math.sqrt(num1); // Square root
					} else {
						isError = true;
						System.out.println("Error sqrt");
						return 0.0; // Square root of negative number
					}
				} else if (ei.equals("not") || ei.equals("¬") || ei.equals("!")) {
					if (isIntegerNumber(num1) && num1 >= 0) {
						if (ei.equals("not") || ei.equals("¬")) {
							num = ~(long) num1; // Bitwise NOT
						} else if (ei.equals("!")) {
							num = factorial((int) num1); // Factorial
						}
					}
				} else if (!S.empty()) {
					double num2 = S.peek();

					if (ei.equals("→") || ei.equals("sto")) {
						if (indexVar(elementMath[i - 1]) >= 0) {
							var[indexVar(elementMath[i - 1])] = num2; // Store value in variable
							ans = num2;
							return ans;
						} else {
							isError = true;
							System.out.println("Error sto");
							return 0.0;
						}
					} else if (ei.equals("+")) {
						num = num2 + num1; // Addition
						S.pop();
					} else if (ei.equals("-")) {
						num = num2 - num1; // Subtraction
						S.pop();
					} else if (ei.equals("*")) {
						num = num2 * num1; // Multiplication
						S.pop();
					} else if (ei.equals("/")) {
						if (num1 != 0) {
							num = num2 / num1; // Division
						} else {
							isError = true;
							return 0.0; // Division by zero
						}
						S.pop();
					} else if (ei.equals("^")) {
						num = Math.pow(num2, num1); // Exponentiation
						S.pop();
					} else if (isIntegerNumber(num1) && isIntegerNumber(num2)) {
						if (ei.equals("and") || ei.equals("∧")) {
							num = (long) num2 & (long) num1; // Bitwise AND
							S.pop();
						} else if (ei.equals("or") || ei.equals("∨")) {
							num = (long) num2 | (long) num1; // Bitwise OR
							S.pop();
						} else if (ei.equals("xor") || ei.equals("⊻")) {
							num = (long) num2 ^ (long) num1; // Bitwise XOR
							S.pop();
						} else if (ei.equals("mod")) {
							num = (long) num2 % (long) num1; // Modulo
							S.pop();
						} else if (ei.equals("<<") || ei.equals("≪")) {
							num = (long) num2 << (long) num1; // Left shift
							S.pop();
						} else if (ei.equals(">>") || ei.equals("≫")) {
							num = (int) num2 >> (int) num1; // Right shift
							S.pop();
						}
					}
				} else {
					System.out.println("Error stack empty");
					isError = true;
					return 0.0;
				}
				S.push(num);
			}
		}
		ans = S.pop();
		System.out.println("\nans = " + ans + "\t radix = " + radix);
		return ans;
	}

	/**
	 * Returns the prime factorization of a number.
	 * @param num the number to factorize
	 * @return a string representation of the prime factorization
	 */
	public String primeMulti(double num) {
		return formatValue.primeMulti(num);
	}
}
