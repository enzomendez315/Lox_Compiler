# Building A Tree-Walk Interpreter
This program is an interpreter for a custom scripting language called Lox. Lox is a high-level, dynamically-typed[^1] language like Python or PHP. Since Lox is a scripting language, it executes directly from source.

For this project, I used a book called Crafting Interpreters by Robert Nystrom, where an interpreter is built from the ground up.[^2] It is ideal to get a better understanding of how high-level languages are implemented, and what goes through the creation of an interpreter using popular programming languages like Java or C++. This is a project to document what I learned.

# Lox Documentation
In Lox, values are created by literals, computed by expressions, and stored in variables. But the user only sees Lox objects (that are implemented in the undrelying language the interpreter is written in, aka Java).

All numbers in Lox are floating point at runtime, but both integer and decimal literals are supported. Lox doesn't allow leading or trailing decimal point, which means that `.1234` and `1234.` are not valid.

# Implementation
## Scanner
The first step in any interpreter (or compiler) is scanning. A scanner takes in raw source code as a stream of characters and groups them into a series of chunks called tokens. Tokens are recognized characters in programming languages like `(` or `;`, numbers, string literals, and identifiers. Tokens make up the language's grammar and they are what the scanner will feed into the parser.

It is important to understand that lexemes are sequences of characters that match the pattern for a token, and are identified as an instance of that particular token. Simply put, lexemes are the words derived from the character input stream while tokens are lexemes mapped into a token-name and an attribute-value.

The core of the scanner is a loop. Starting at the first character of the source code, the scanner will go through each one and idenfity what lexeme the character belongs to. Then it will consume the character and any following characters that are part of that lexeme. When it reaches the end of that lexeme, it creates a token. The scanner will continue to consume lexemes and occasionally emit tokens until it reaches the end of the file.

Reserved keywords like `while`, `or`, `fun`, etc. are scanned differently from operators like `<`, `>=`, or `==`. This is in order to avoid confusing the program between possible variable names and keywords. For example, if we wanted to define the variable name `orbit` and the program scanned the reserved keyword `or` character by character, then it would consider that we are trying to use the keyword `or`, and would treat the rest of the word `bit` like a variable. Instead, the program follows the principle of maximal munch, which defines that whenever we have two lexical grammar rules that match a chunk of code, whichever one matches the most characters wins. So if the rule states that we can match `orbit` as an identifier and `or` as a keyword, the former takes precedence over the latter.

## Parser
A parser takes the sequence of tokens and builds a tree structure with them. These trees are called parse trees or abstract syntax trees.
Given a series of tokens, the tokens are mapped to the terminals in the grammar to figure out which rules could have generated that string. This is done in order to understand what part of the language each token belongs to. The parser could also categorize tokens from the raw lexeme by comparing the strings, character by character. But that would be a really slow and inefficient solution.

While the lexical grammar used for the scanner was called a _regular language_, the parser uses _syntactic grammar_ to define an infinite set of strings that are in the grammar. Put another way, in order to move from the scanner to the parser, we need another level of granularity. In the scanner's grammar, the "alphabet" consists of individual characters and the strings are the valid lexemes. But in the parser's grammar, now individual tokens can be thought of as "letters" in the alphabet while strings are individual expressions made by a sequence of tokens. This subtle but critical distinction helps us separate each lexeme, token, and expression before using them in the interpreter.

A parser uses production rules to represent and organize grammars that contain an infinite number of valid strings. Production rules generate strings in the grammar and contain two elements - terminals and nonterminals. A terminal is an individual lexeme. They are called terminals because they don't produce other rules in the grammar. A nonterminal however, references another rule in the grammar. It esentially plays that rule and inserts whatever it produces there.

This is an example of a set of production rules, where terminals are quoted strings and nonterminals are lowercase words:
```
breakfast -> protein "with" breakfast "on the side" ;
breakfast -> protein ;
breakfast -> bread ;

protein -> crispiness "crispy" "bacon" ;
protein -> "sausage" ;
protein -> cooked "eggs" ;

crispiness -> "really" ;
crispiness -> "really" crispiness ;

cooked -> "scrambled" ;
cooked -> "poached" ;
cooked -> "fried" ;

bread -> "toast" ;
bread -> "biscuits" ;
bread -> "English muffin" ;
```

Just like in the example above, the grammar that represents expressions like `grouping`, `unary`, and `binary`, is recursive by nature. This is because the expression refers to itself or to another expression, and it is the reason why the data structure will form a (syntax) tree.

A parser has two jobs:
1. To produce a corresponding syntax tree given a valid sequence of tokens.
2. To detect any errors and notify the user if the sequence of tokens is invalid.

The most important aspect of a parser is usability because at the end of the day, the user is the one who will be dealing with it the most. If the parser takes a long time to consume all the source files or if it doesn't notify the user of their mistakes, then it is not very usable. This is why the parser reports as many separate errors as it can while ignoring cascaded errors (meaning that it ignores the errors that are a side effect of previous errors).

## Interpreter
Each kind of expression in Lox behaves differently at runtime, which means that the interpreter needs to select a different chunk of code to handle each expression type. Since the tree classes are used by both the parser and the interpreter, it would be a mess to associate all their behavior using methods on the classes themselves. In addition, doing name resolution on all those classes every time we need them or adding instance methods for _all_ the operations we need to perform over different domains would be really slow and inefficient; and it would violate separation of concerns. 

Instead, the best way to model syntax tree nodes is by using the Visitor pattern, which combines functional and object-oriented programming. With this design choice, we can define all of the behavior for a new operation on a set of types (in this case multiple different classes) in one place, without having to touch the types themselves. So we define a separate interface, and each operation that can be performed on expressions is a new class that implements that interface. To perform an operation on an expression, we call its `accept()` method and pass in the visitor for the operation we want to execute. That way we can use that method for as many visitors as we want without ever having to touch the expression classes again.

The leaves of an expression tree are literals. A literal is a bit of syntax that produces a value when it is evaluated at runtime. During scanning, the program produces the runtime value and puts it in a token that is later consumed by the parser. So the interpreter simply pulls it back out when evaluating the literal.

To evaluate a grouping expression, the interpreter recursively evaluates the subexpression inside. This subexpression is what exists inside the parantheses.

Like grouping expressions, unary expressions have a single subexpression that needs to be evaluated first. Once the operand expression is evaluated, the unary operator is applied to the result.

Unlike unary expressions, binary expressions have two operands to evaluate using the appropriate operator.

An expression statement can be defined as an expression that has side effects, such as calling a function or a method, or declaring variables. For example, a print statement evaluates an expression and displays the result to the user. But unlike expressions, statements produce no values. So the interpreter evaluates the inner expression and discards the value before returning `null`.

In Lox, an environment stores the bindings that associate variables to values. It is like a map where the keys are variable names and the values are the variable's values themselves. There are different levels of environments, such that the program remembers the state of global variables, defined functions, and local variables. It also evaluates the precedence for each one using scopes and uses the right environment at runtime. Multiple scopes enable the same name to refer to different things in different contexts.

## Error Handling
Since it is up to the program to notify the user of anything that could have gone wrong, the program has an error function that reports to the user that there is some syntax error on a given line.

For lexical errors, if the scanner finds a character that Lox doesn't use, the erronous character gets discarded and the scanner keeps going through the characters in the source code. At the end, the program reports the all the errors to the user at one time. This is done to avoid having an error, having the program report it to the user so that it can be fixed, and then going through the same tedious process for the next error.

And to prevent the program from crashing when it detects an error in non-critical operations, the program has a flag that is activated whenever it encounters an error. That way it can still perform non-critical operations without executing any code that could end the program abruptly.

When an error occurs, the parser discards tokens until it gets to the next statement. And then it will parse the rest of the file starting at that location. This process of getting its state and the sequence of following tokens aligned such that the next token does match the rule being parsed, is called synchronization. The parser fixes its parsing state by jumping out of any nested production rules until it gets back to that rule. Then it synchronizes the token stream by discarding tokens until it reaches an expected one based on the rule. For runtime errors however, it catches the exception thrown by the language it is implemented on (Java) and notifies the user of the error that occurred.

Having a ParseError class gives us the opportunity to unwind the parser if there is an unexpected error. In fact, the `error()` method _returns_ the error as opposed to throwing it because that way, the calling method inside the parser decides whether to unwind or not. Some parse errors occur in non-critical places where the parser doesn't need to synchronize. In those places, the program simply reports the error and keeps parsing.

It is critical to detect and address runtime errors appropriately. If these errors are not handled correctly, the program will throw a Java exception that will unwind the whole stack before exiting the application and printing the Java stack trace on the screen. But the fact that Lox is implemented in Java should be a detail hidden from the user, which is why dealing with runtime errors is important. The program uses its own class to extend the functionality of Java's RuntimeException class in order to detect and report errors to the user.

-----------------------------------------

We can think of the compiler as a pipeline where each stage's job is to organize the data representing the user's code in a way that makes
the next stage simpler to implement.

A compiler translates a source language to some other language (usually lower-level). Although transpiling a low level language to a
higher level constitutes as compiling too. But the compiler only translates source code to some other form. It does not execute it. The user
has to take the resulting output and run it themselves. On the other hand, an interpreter takes in source code and executes it immediately.

The tree-walk interpreter evaluates nested expressions using recursive method calls.





[^1]: Dynamically-typed means that the interpreter assigns variables a type at runtime based on the variable's value. This is different from a statically-typed language like Java or C++, where variable types are known at compile time.

[^2]: https://craftinginterpreters.com/contents.html
