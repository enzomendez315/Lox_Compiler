# Building An Interpreter
This program is an interpreter for a custom scripting language called Lox. Lox is a high-level, dynamically-typed[^1] language like Python or PHP. Since Lox is a scripting language, it executes directly from source.

## Implementation
The first step in any interpreter (or compiler) is scanning. A scanner takes in raw source code as a stream of characters and groups them into a series of chunks called tokens. Tokens are recognized characters in programming languages like `(` or `;`, numbers, string literals, and identifiers. Tokens make up the language's grammar and they are what the scanner will feed into the parser.

-----------------------------------------

A parser takes the sequence of tokens and builds a tree structure with them. These trees are called parse trees or abstract syntax trees.

We can think of the compiler as a pipeline where each stage's job is to organize the data representing the user's code in a way that makes
the next stage simpler to implement.

A compiler translates a source language to some other language (usually lower-level). Although transpiling a low level language to a
higher level constitutes as compiling too. But the compiler only translates source code to some other form. It does not execute it. The user
has to take the resulting output and run it themselves. On the other hand, an interpreter takes in source code and executes it immediately.



[^1]: Dynamically-typed means that the interpreter assigns variables a type at runtime based on the variable's value. This is different from
a statically-typed language like Java or C++, where variable types are known at compile time.
