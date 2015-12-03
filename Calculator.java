import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.util.*;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.border.*;

/*
	Calculator Program, featuring expression parsing via Shunting Yard Algorithm.

	@author
	Sunil Kunnakkat
*/

public class Calculator extends JFrame implements ActionListener {
	/*
		For this calculator there are ten rows, with thirty six buttons taking up
		nine out of the ten rows.

		The top row is reserved for the text area.
	*/

	JPanel[] rows = new JPanel[10];
	JButton[] buttons = new JButton[36];
	JTextArea main_screen = new JTextArea();
	
	/*
		There are two fonts. The first is for the text area, and the second is for
		the calculator buttons.
	*/
	Font font = new Font("Callibri", Font.PLAIN, 12);
	Font font2 = new Font("Callibri", Font.PLAIN, 10);
	
	/*
		These two values will be used later to define the associativity of functions
		within the order of operations.
	*/
	public static final int left_associative  = 0;
	public static final int right_associative = 1;
	
	/*
		This operators_table defines the major operations in PEMDAS, not including
		parentheses.

		The structure of the table is:

			Operator, Associativity Value, Precedence Value
	*/
	public static final Hashtable<String, ArrayList<Integer>> operators_table = 
				new Hashtable<String, ArrayList<Integer>>();
	static {

		operators_table.put("+", new ArrayList<Integer>(){{
								add(0);
								add(0);
							}});
		operators_table.put("-", new ArrayList<Integer>(){{
								add(0);
								add(0);
							}});
		operators_table.put("*", new ArrayList<Integer>(){{
								add(0);
								add(1);
							}});
		operators_table.put("/", new ArrayList<Integer>(){{
								add(0);
								add(1);
							}});
		operators_table.put("^", new ArrayList<Integer>(){{
								add(1);
								add(10);
							}});
	}
	/*
		This functions_list defines the major functions included in the calculator.
	*/
	public static final ArrayList<String> functions_list = new ArrayList<String>();  
	static {
		functions_list.addAll(Arrays.asList("sin", "cos", "tan", "cot", "sec", "csc",
									"arcsin", "arccos", "arctan", "ln"));
	}
	/*
		Names of all of the buttons to be included in the calculator.
	*/
	String[] button_names =	
		{"e", "π", "√", "Bksp",
		"sin", "cos", "tan", "(",
		"sec", "csc", "cot", ")",
		"arcsin", "arccos", "arctan", "ln",
		"7", "8", "9", "+",
		"4", "5", "6", "-",
		"1", "2", "3", "*",
		"0", ".", "+/-", "/",
		"CLEAR", "x^2", "^", "="};

	Calculator() {
	
		super("Calculator");
		/*
			Creating the constructor. Set the size and do not allow the window to be
			resizable.

			The program will terminate upon closing.
		*/
		setSize(320, 600);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		/*
			Create the layout for the application, evenly spaced grid layout with ten
			rows and ten columns, then set the layout.
		*/
		GridLayout grid = new GridLayout(10, 10);
		setLayout(grid);
		
		/*
			Create a new panel for each row, then set a grid layout for each row.
		*/
		for (int i = 0; i < 10; i++) {
			rows[i] = new JPanel();
			rows[i].setLayout(new GridLayout());
		}
		/*
			Create the 36 buttons, assign the names for each button, the proper font,
			and add an ActionListener for when it gets clicked.
		*/
		for (int i = 0; i < 36; i++) {
			buttons[i] = new JButton();
			buttons[i].setText(button_names[i]);
			buttons[i].setFont(font2);
			buttons[i].addActionListener(this);
		}

		/*
			No text, non-editable, left-to-right text orientation. 
		*/
		main_screen.setText("");
		main_screen.setFont(font);
		main_screen.setEditable(false);
		main_screen.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		/*
			Add the main_screen to the row, and add the row to the layout.
		*/
		rows[0].add(main_screen);
		add(rows[0]);
		
		/*
			Add the buttons to the rows, thirty six in total, meaning
			four for each of the remaining nine rows.

			Then, add the final nine rows to the layout.
		*/		
		for ( int i = 0; i < 4; i++ ) rows[1].add(buttons[i]);
		for ( int i = 4; i < 8; i++ ) rows[2].add(buttons[i]);
		for ( int i = 8; i < 12; i++ ) rows[3].add(buttons[i]);
		for ( int i = 12; i < 16; i++ ) rows[4].add(buttons[i]);
		for ( int i = 16; i < 20; i++ ) rows[5].add(buttons[i]);
		for ( int i = 20; i < 24; i++ ) rows[6].add(buttons[i]);
		for ( int i = 24; i < 28; i++ ) rows[7].add(buttons[i]);
		for ( int i = 28; i < 32; i++ ) rows[8].add(buttons[i]);
		for ( int i = 32; i < 36; i++ ) rows[9].add(buttons[i]);
		for ( int i = 1; i < 10; i++ ) add(rows[i]);

		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		/*
			Grab the source of the event, which has to be a button, and store it
			separately for string comparisons. 
		*/
		JButton test_button = (JButton) event.getSource();

		/*
			If the button text is a number, add it to the text area.
		*/
		if (test_button.getText().matches("-?\\d+(\\.\\d+)?")) 
			main_screen.append(test_button.getText());
		else if (!(test_button.getText().matches("-?\\d+(\\.\\d+)?"))) {
			/*
				Each remaining option for the button's text that isn't a number must be
				handled accordingly, so that the text can be reinterpreted later for
				expression parsing and solving.
			*/
			switch (test_button.getText()) {
				case "x^2":
					main_screen.append(" ^ 2 ");
					break;
				case "CLEAR":
					main_screen.setText("");
					break;
				case "+/-":
					main_screen.append(" -");
					break;
				case "sin": case "cos":
				case "tan": case "cot":
				case "csc": case "sec":
				case "arcsin": case "arccos":
				case "arctan": case "ln":
				case "√":
					String space = " ";
					String left_parentheses = "(";
					String append_function = space + test_button.getText() 
										+ space + left_parentheses;
					main_screen.append(append_function);
					break;
				case ".":
					main_screen.append(".");
					break;
				case "=":
					String[] postfix_input = main_screen.getText().split(" ");
					String[] converted_input = convertToRPN(postfix_input);
					String solved_output = Double.toString(solveRPN(converted_input));
					main_screen.setText(solved_output);
					break;
				case "Bksp":
					String backspace_string = 
					main_screen.getText().substring(0, main_screen.getText().length()-1);
					main_screen.setText(backspace_string);
					break;
				default:
					main_screen.append(" ");
					main_screen.append(test_button.getText());
					main_screen.append(" ");
					break;
			}
		}
	}
	public static Boolean associative_and_precedence(String token1, String token2) {
		int token1_associativity = operators_table.get(token1).get(0);
		int token1_precedence = operators_table.get(token1).get(1);
		int token2_precedence = operators_table.get(token2).get(1);

		/*
			This function is for determining both whether a given operator is either
			left associative or right associative, and determining the operator precedence
			within the order of operations. This is all for expression parsing to be
			performed later.

			Check if either the operator is left associative 
		*/
		return (((token1_associativity == left_associative) && 
			(token1_precedence - token2_precedence <= 0)) ||	
		((token1_associativity == right_associative) &&
			(token1_precedence - token2_precedence < 0)));
	}
	public static String[] convertToRPN(String[] inputTokens) {
		/*
			Application of the Shunting Yard Algorithm for converting infix notation to
			postfix notation. This will be the form used for solving the expression.

			First, create an array list for creating the final expression, and a stack
			all of the operators.
		*/
		ArrayList<String> rpn_list = new ArrayList<String>();  
		Stack<String> operator_stack = new Stack<String>();

		for (String token : inputTokens) {
			/*
				If the token is a number, including values e or π, add it to the list.

				If the token is a function (sin, cos, ln, etc.), push the token onto
				the operator stack.
			*/
			if (token.matches( "-?\\d+(\\.\\d+)?") || 
					token.equals("e") || token.equals("π"))
				rpn_list.add(token);

			if (functions_list.contains( token )) operator_stack.push( token );
			
			/*
				If the token is an operator, while the operator stack isn't empty and the
				top value of the stack is an operator, check the token operator's associativity
				and operator precedence compared to the top value of the operator stack.

				Accordingly, pop off the top value of the operator stack and add it to the list.

				Once the operator stack is empty, push the token onto the operator stack.
			*/
			if (operators_table.containsKey(token)) {
				while (!operator_stack.empty() && 
					operators_table.containsKey(operator_stack.peek())) {

					if (associative_and_precedence(token, operator_stack.peek())) {

						rpn_list.add(operator_stack.pop());
						continue;  
					}
					break;  
				}
				operator_stack.push(token);
			}
			/*
				If the token is a left parentheses, push it onto the operator stack.

				If the token is a right parentheses, then while the operator stack is not empty
				and the top operator stack value is not a left parentheses:
					Pop all operator stack values and add it to the list.

				The remaining value for the operator stack should be a left parentheses, so just
				pop it off. Then check if the remaining top stack value is a function, and if so
				add it to the list.
			*/
			else if (token.equals("(")) operator_stack.push(token);
			else if (token.equals(")")) {    
				while (!operator_stack.empty() && !operator_stack.peek().equals("("))
					rpn_list.add(operator_stack.pop());
				if (!operator_stack.empty()) {
					operator_stack.pop();
					if ( functions_list.contains( operator_stack.peek() ) )
						rpn_list.add(operator_stack.pop());
				}
			}
		}
		/*
			For all remaining values in the operator stack, pop them off and add them to the list. If
			any are left parentheses, there was a mismatch.

			Store the list as a String array and return that value.
		*/
		while (!operator_stack.empty()) rpn_list.add(operator_stack.pop());
		String[] output = new String[rpn_list.size()];
		return rpn_list.toArray(output);  
	}
	public Double solveRPN(String[] rpnString) {

		/*
			Create a stack for storing all of the numbers.
		*/
		Stack<Double> rpn_stack = new Stack<Double>();

		for (String token : rpnString) {
			/*
				If the token is a number, including values e or π, convert these strings to 
				mathematical values, in this case doubles, and push it onto the stack.
			*/
			if ( token.matches( "-?\\d+(\\.\\d+)?") || 
				token.equals("e") || token.equals("π")) {
				
				if (token.equals("e")) rpn_stack.push(Math.E);
				else if (token.equals("π")) rpn_stack.push(Math.PI);
				else {
					double d1 = Double.parseDouble(token);
					rpn_stack.push(d1);
				}
			}
			/*
				If the string is an operator or function, check which type it is, and accordingly
				perform the operation on the first two values of the stack popped off, and for only
				the first value off of the stack if the string is a function.

				For each process completed, the result is pushed onto the stack. This is performed
				for all strings, until all stack values popped and all that is left is the final result
				of the expression, which is popped off and returned.
			*/
			else if (operators_table.containsKey(token) || 
				functions_list.contains(token)) {		
				
				Double s1 = 0.0;
				Double s2 = 0.0;

				if (operators_table.containsKey(token)) {
					s1 = rpn_stack.pop();
					s2 = rpn_stack.pop();
					if (token.equals("+")) {
						double add = s2 + s1;
						rpn_stack.push(add);
					} else if (token.equals("-")) {
						double subract = s2 - s1;
						rpn_stack.push(subract);
					} else if (token.equals("*")) {
						double multiply = s2 * s1;
						rpn_stack.push(multiply);
					} else if (token.equals("/")) {
						double divide = s2 / s1;
						rpn_stack.push(divide);
					} else if (token.equals("^")) {
						double exponent = Math.pow(s2, s1);
						rpn_stack.push(exponent);
					}
				}
				else if (functions_list.contains(token)) {
					s1 = rpn_stack.pop();
					if (token.equals("sin")) {
						double sin = Math.sin(s1);
						rpn_stack.push(sin);
					} else if (token.equals("cos")) {
						double cos = Math.cos(s1);
						rpn_stack.push(cos);
					} else if (token.equals("tan")) {
						double tan = Math.tan(s1);
						rpn_stack.push(tan);
					} else if (token.equals("cot")) {
						double cot = 1 / Math.tan(s1);
						rpn_stack.push(cot);
					} else if (token.equals("sec")) {
						double sec = 1 / Math.cos(s1);
						rpn_stack.push(sec);
					} else if (token.equals("csc")) {
						double sec = 1 / Math.sin(s1);
						rpn_stack.push(sec);
					} else if (token.equals("arcsin")) {
						double arcsin = Math.asin(s1);
						rpn_stack.push(arcsin);
					} else if (token.equals("arccos")) {
						double arccos = Math.acos(s1);
						rpn_stack.push(arccos);
					} else if (token.equals("arctan")) {
						double arctan = Math.atan(s1);
						rpn_stack.push(arctan);
					} else if (token.equals("ln")) {
						double ln = Math.log(s1);
						rpn_stack.push(ln);
					} else if (token.equals("√")) {
						double square_root = Math.sqrt(s1);
						rpn_stack.push(square_root);
					} else if (token.equals("x^2")) {
						double squareit = Math.pow(s1, 2);
						rpn_stack.push(squareit);
					}
				}	
			}
		}
		return rpn_stack.pop();
	}
	public static void main(String[] args) {
		/*
			Create a new instance of the Calculator.
		*/
		Calculator calc = new Calculator();		
	}
}
