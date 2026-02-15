# Languages and Automata – TP 4 Interpreter

This repository contains the **starter code** for TP 4 of the *Languages and Automata* course at Nantes University.

The goal of this practical session is to build an **interpreter** for a small algorithmic language, based on an
**abstract syntax tree (AST)** produced by a CUP parser.  
This TP introduces the separation between *syntax analysis* and *execution*.

See the [main organization](https://github.com/LangagesEtAutomates/) for more information on the course
and related teaching materials.

---

## Structure

```
├── LICENSE.txt           # MIT license (see organization-wide license file)
├── README.md             # This file
├── build.xml             # Ant build file
├── lib/                  # External libraries (JFlex, CUP, etc.)
├── src/
│   └── lea/
│       ├── Lexer.flex    # JFlex lexer specification
│       ├── Parser.cup    # CUP parser specification
│       ├── AST.java      # Abstract Syntax Tree definitions
│       ├── Interpreter.java # Interpreter implementation
│       ├── Reporter.java # Error reporting utility
│       └── Main.java     # Main entry point
├── gen/                  # Generated sources (lexer and parser)
├── build/                # Compiled classes
```

Generated directories (`gen/`, `build/`) are produced automatically and should not be edited manually.

---

## Build and Execution

The project uses **Apache Ant**.

- Generate lexer and parser:

```bash
ant generate
```

- Compile all sources (including generated code):

```bash
ant compile
```

- Run the interpreter:

```bash
ant run
```

- Compile and immediately run:

```bash
ant build
```

- Remove generated and compiled files:

```bash
ant clean
```

The project targets **Java 21**.

---

## Pedagogical Objectives

In this TP, students will:

- understand how a CUP parser builds an AST instead of directly computing values;
- explore a recursive interpreter architecture for instructions and expressions;
- implement control structures such as:
  - conditional instructions,
  - `pour` loops,
  - `tant que` loops;
- handle runtime and syntax errors using explicit error nodes.

---

## Dependencies

All dependencies are provided in the `lib/` directory:

- **JFlex** — lexer generation  
- **Java CUP** — parser generation  

No external installation is required beyond a JDK and Ant.

---

## License

All **source code** in this repository is distributed under the **MIT License**.

- The full legal text is available in [`LICENSE.txt`](LICENSE.txt).
- Organization-wide licensing details and attributions are documented in  
  https://github.com/LangagesEtAutomates/.github/blob/main/LICENSE.md

This license applies to all Java sources and grammar files (`.flex`, `.cup`)
in this repository.

---

## Contributing

Contributions are welcome, in particular:
- improvements to the interpreter design,
- clarifications of error handling,
- additional test programs.

Please use pull requests to propose changes.
For significant modifications, consider opening an issue first to discuss the design.
