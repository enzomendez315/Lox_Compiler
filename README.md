# Building A Tree-Walk Interpreter
This program is an interpreter for a custom scripting language called Lox. Lox is a high-level, dynamically-typed[^1] language like Python or PHP. Since Lox is a scripting language, it executes directly from source.

For this project, I used a book called Crafting Interpreters by Robert Nystrom, where an interpreter is built from the ground up.[^2] It is ideal to get a better understanding of how high-level languages are implemented, and what goes through the creation of an interpreter using popular programming languages like Java or C++. This is a project to document what I learned.

# Lox Documentation
In Lox, values are created by literals, computed by expressions, and stored in variables. But the user only sees Lox objects (that are implemented in the undrelying language the interpreter is written in, aka java).

All numbers in Lox are floating point at runtime, but both integer and decimal literals are supported. Lox doesn't allow leading or trailing decimal point, which means that `.1234` and `1234.` are not valid.

# Implementation
## Scanner
The first step in any interpreter (or compiler) is scanning. A scanner takes in raw source code as a stream of characters and groups them into a series of chunks called tokens. Tokens are recognized characters in programming languages like `(` or `;`, numbers, string literals, and identifiers. Tokens make up the language's grammar and they are what the scanner will feed into the parser.

It is important to understand that lexemes are sequences of characters that match the pattern for a token, and are identified as an instance of that particular token. Simply put, lexemes are the words derived from the character input stream while tokens are lexemes mapped into a token-name and an attribute-value.

The core of the scanner is a loop. Starting at the first character of the source code, the scanner will go through each one and idenfity what lexeme the character belongs to. Then it will consume the character and any following characters that are part of that lexeme. When it reaches the end of that lexeme, it creates a token. The scanner will continue to consume lexemes and occasionally emit tokens until it reaches the end of the file.

Reserved keywords like 'while', 'or', 'fun', etc. are scanned differently from operators like '<', '>=', or '=='. This is in order to avoid confusing the program between possible variable names and keywords. For example, if we wanted to define the variable name orbit and the program scanned the reserved keyword 'or' character by character, then it would consider that we are trying to use the keyword 'or', and would treat the rest of the word 'bit' like a variable. Instead, the program follows the principle of maximal munch, which defines that whenever we have two lexical grammar rules that match a chunk of code, whichever one matches the most characters wins. So if the rule states that we can match 'orbit' as an identifier and 'or' as a keyword, the former takes precedence over the latter.

## Parser
A parser takes the sequence of tokens and builds a tree structure with them. These trees are called parse trees or abstract syntax trees.
Given a series of tokens, the tokens are mapped to the terminals in the grammar to figure out which rules could have generated that string. This is done in order to understand what part of the language each token belongs to. The parser could also categorize tokens from the raw lexeme by comparing the strings, character by character. But that would be a really slow and inefficient solution.

A parser uses production rules to represent and organize the grammar. Production rules generate strings in the grammar and contain two elements - terminals and nonterminals.

A terminal is an individual lexeme. They are called terminals because they don't produce other rules in the grammar. A nonterminal however, references another rule in the grammar. It esentially plays that rule and inserts whatever it produces there.

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

A parser has two jobs:
1. To produce a corresponding syntax tree given a valid sequence of tokens.
2. To detect any errors and notify the user if the sequence of tokens is invalid.

The most important aspect of a parser is usability because at the end of the day, the user is the one who will be dealing with it the most. If the parser takes a long time to consume all the source files or if it doesn't notify the user of their mistakes, then it is not very usable. This is why the parser reports as many separate errors as it can while ignoring cascaded errors (meaning that it ignores the errors that are a side effect of previous errors).

When an error occurs, the parser discards tokens until it gets to the next statement. And then it will parse the rest of the file starting at that location.

For runtime errors however, it catches the exception thrown by the language it is implemented on (java) and notifies the user of the error that occurred.

## Error Handling
Since it is up to the program to notify the user of anything that could have gone wrong, the program has an error function that reports to the user that there is some syntax error on a given line.

For lexical errors, if the scanner finds a character that Lox doesn't use, the erronous character gets discarded and the scanner keeps going through the characters in the source code. At the end, the program reports the all the errors to the user at one time. This is done to avoid having an error, having the program report it to the user so that it can be fixed, and then going through the same tedious process for the next error.

And to prevent the program from crashing when it detects an error in non-critical operations, the program has a flag that is activated whenver it encounters an error. That way it can still perform non-critical operations without executing any code that could end the program abruptly.

-----------------------------------------

We can think of the compiler as a pipeline where each stage's job is to organize the data representing the user's code in a way that makes
the next stage simpler to implement.

A compiler translates a source language to some other language (usually lower-level). Although transpiling a low level language to a
higher level constitutes as compiling too. But the compiler only translates source code to some other form. It does not execute it. The user
has to take the resulting output and run it themselves. On the other hand, an interpreter takes in source code and executes it immediately.

The tree-walk interpreter evaluates nested expressions using recursive method calls.





[^1]: Dynamically-typed means that the interpreter assigns variables a type at runtime based on the variable's value. This is different from a statically-typed language like Java or C++, where variable types are known at compile time.

[^2]: https://craftinginterpreters.com/contents.html
