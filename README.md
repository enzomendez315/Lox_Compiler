# Building An Interpreter
This program is an interpreter for a custom scripting language called Lox. Lox is a high-level, dynamically-typed[^1] language like Python or PHP. Since Lox is a scripting language, it executes directly from source.

## Implementation
The first step in any interpreter (or compiler) is scanning. A scanner takes in raw source code as a stream of characters and groups them into a series of chunks called tokens. Tokens are recognized characters in programming languages like `(` or `;`, numbers, string literals, and identifiers. Tokens make up the language's grammar and they are what the scanner will feed into the parser.

A parser takes the sequence of tokens and builds a tree structure with them. These trees are called parse trees or abstract syntax trees.
Given a series of tokens (aka a string), the tokens are mapped to the terminals in the grammar to figure out which rules could have generated that string. This is done in order to understand what part of the language each token belongs to.

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

The most important aspect of a parser is usability because at the end of the day, the user is the one who will be dealing with it the most. If the parser takes a long time to consume all the source files or if it doesn't notify the user of their mistakes, then it is not very usable. This is why the parser reports as many separate errors as it can while ignoring cascaded errors (meaning that we ignore the errors that are a side effect of previous errors).

-----------------------------------------

We can think of the compiler as a pipeline where each stage's job is to organize the data representing the user's code in a way that makes
the next stage simpler to implement.

A compiler translates a source language to some other language (usually lower-level). Although transpiling a low level language to a
higher level constitutes as compiling too. But the compiler only translates source code to some other form. It does not execute it. The user
has to take the resulting output and run it themselves. On the other hand, an interpreter takes in source code and executes it immediately.





[^1]: Dynamically-typed means that the interpreter assigns variables a type at runtime based on the variable's value. This is different from
a statically-typed language like Java or C++, where variable types are known at compile time.
