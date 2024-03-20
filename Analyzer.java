import org.json.simple.JSONArray;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {
    private static Token tok;

    public static void main(String[] args) throws Exception{
            List<String> tokenList = new ArrayList<>();
            List<Token> token = new ArrayList<>();
            List<String> reservedWords = Arrays.asList("end", "var", "record", "number", "string");
            Scanner s = new Scanner(System.in);
            while (s.hasNextLine()) {
                String lineOnlineInput = s.nextLine();
                if (lineOnlineInput.contains("#"))
                    tokenList.addAll(lexer(lineOnlineInput.substring(0, lineOnlineInput.indexOf("#"))));
                else
                    tokenList.addAll(lexer(lineOnlineInput));
            }
            /*--Lexer---*/
            for (String t : tokenList) {
                if (t.matches("[:]|[;]") || reservedWords.contains(t))
                    token.add(new Token(t, t));
                else if (t.matches("[a-zA-z_][\\w]*"))
                    token.add(new Token("ID", t));
                else
                    token.add(new Token(t.substring(0, 1), t.substring(0, 1)));
            }
            Token[] tokenArray = new Token[token.size()];
            tokenArray = token.toArray(tokenArray);
                        /*---Pass the token list to parser---*/
            JSONArray jsonArray = new Parser(token.toArray(tokenArray)).parse();
            System.out.println(jsonArray.toJSONString().replaceAll("\\[\\],",""));

    }

    public static List<String> lexer(String input) {
        try {
            List<String> tokenList = new ArrayList<>();
            Matcher m = Pattern.compile("[\\w]+|:|;|[^\\w:;\\s\\t]+").matcher(input);
            while (m.find()) {
                tokenList.add(m.group());
            }
            return tokenList;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}

class Token {
    String kind, lexeme;

    public Token(String kind, String lexeme) {
        this.kind = kind;
        this.lexeme = lexeme;
    }
}
/*----Grammar----------
*     input
*        |string \n string
*        |string
*     string
*         | declaration
      declaration
          | var ID : type;
      type
        |number
        |string
        |record
      record
        | (field_declaration)+
      field_declaration
        | var ID :type;
        |end;

 */
class Parser {
    private final Token[] tokens;
    public Token tok;
    List<String> reservedWords = Arrays.asList("end", "var", "record", "number", "string");
    private int index;


    public Parser(Token[] tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.tok = this.nextToken();
    }

    public Token nextToken() {
        if (this.index < this.tokens.length) {
            return this.tokens[this.index++];
        } else {
            return new Token("EOF", "<EOF>");
        }
    }

    public boolean peek(String kind) {
        return this.tok.kind.equals(kind);
    }

    public void consume(String kind) throws Exception {
        if (this.peek(kind)) {
            this.tok = this.nextToken();
        } else {
            //throw syntax error code
            throw new Exception("Illegal token " + kind + ". The grammar does  not  accept this.");
        }
    }

    public JSONArray parse() throws Exception {
        JSONArray fin = new JSONArray();
        JSONArray syntax_tree = new JSONArray();
        try {
            for (Token t : this.tokens) {
                if (this.index == 1 && !this.tok.kind.equals("var"))
                    throw new Exception("Expression/Declaration should start with reserved word 'var'");
                if (this.peek("var")) {
                    this.consume(this.tok.kind);
                } else if (this.peek("ID")) {
                    if (this.reservedWords.contains(this.tok.lexeme))
                        throw new Exception("Identifier cannot be a reserved word");
                    fin.add(this.tok.lexeme);
                    this.consume(this.tok.kind);
                    if (!this.tok.kind.equals(":"))
                        throw new Exception("Colon Expected after Identifier");

                } else if (this.peek("number") || this.peek("string")) {
                    fin.add(this.tok.lexeme);
                    this.consume(this.tok.kind);
                    if (!this.tok.kind.equals(";"))
                        throw new Exception("Semi Colon Expected after type");
                } else if (this.peek("record")) {
                    this.consume(this.tok.kind);
                    if (this.tok.kind.equals("end"))
                        throw new Exception("Record cannot be empty");
                    JSONArray temp1 = new JSONArray();
                    while (!this.tok.kind.equals("var")) {
                        if (this.peek("end")) {
                            this.consume(this.tok.kind);
                            break;
                        }
                        if (this.peek("EOF"))
                            break;
                        List<String> temp = this.parse();
                        if (temp.size() != 0)
                            temp1.addAll(temp);
                    }
                    if (temp1.size() != 0)
                        fin.add(temp1);
                    if (fin.size() != 0) {
                        syntax_tree.add(fin);
                    }
                } else if (this.peek(":")) {
                    this.consume(this.tok.kind);
                    if (!((this.tok.kind.equals("number") || this.tok.kind.equals("string")) || this.tok.kind.equals("record")))
                        throw new Exception("Valid Type Expected after Identifier");
                } else if (this.peek(";")) {
                    JSONArray result = new JSONArray();
                    if (fin.size() != 0) {
                        result.add(fin.get(0));
                        result.add(fin.get(1));
                        syntax_tree.add(result);
                    }
                    this.consume(this.tok.kind);
                    fin.clear();

                } else if (this.peek("EOF")) {
                   break;
                } else {
                    if (!this.tok.kind.equals("end"))
                        throw new Exception("Bad character:  '" + this.tok.lexeme + "' Not supported in our grammar");
                }
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return syntax_tree;
    }
}