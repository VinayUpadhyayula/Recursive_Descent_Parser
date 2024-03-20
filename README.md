The project implements Recursive descent parsing.
It is based on the grammar defined based on the recipe's discussed in the class.
The grammar is mentioned as a comment above the Parser class in the Analyzer.class. 

The code implementation on an higher level is carried out as follows:

  1)input is read using Scanner class
  2) Lexer implementation is carried out using Regular Expressions(RegEx) and tokens are generated.
  3) Then the token list is passsed to Parser class which builds the AST.

**Language Used** : Java 

**External libraries used**: com.googlecode.json-simple for JsonArray construction.
   The external library jar is placed inside the source code under /jars directory.

make.sh contains commands that build the project.

javac -cp .:jars/json-simple-1.1.1.jar Analyzer.java  ->which adds the jar present in the project folder and adds it to java classpath

decls.sh contains commands that are used to run the project.

java -cp .:jars/json-simple-1.1.1.jar Analyzer.java

If in case make.sh and decls.sh shell scripts fail to work as expected, the project can be tested using the above mentioned commands within the project directory.


The warnings displayed on the terminal while running can be ignored as they are general warnings raised by the compiler and do not effect the execution.