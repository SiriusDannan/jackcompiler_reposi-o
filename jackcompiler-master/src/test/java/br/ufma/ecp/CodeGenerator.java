package br.ufma.ecp;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
public class CodeGenerator extends TestSupport{
    @Test
    public void testInt () {
        var input = """
            10
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10       
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testSimpleExpression () {
        var input = """
            10 + 30
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10
                push constant 30
                add       
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testLiteralString () {
        var input = """
            "OLA"
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 3
                call String.new 1
                push constant 79
                call String.appendChar 2
                push constant 76
                call String.appendChar 2
                push constant 65
                call String.appendChar 2
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testFalse () {
        var input = """
            false
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0       
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testNull () {
        var input = """
            null
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0       
                    """;
            assertEquals(expected, actual);
    }


    @Test
    public void testTrue () {
        var input = """
            true
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0
                not       
                    """;
            assertEquals(expected, actual);
    }


    @Test
    public void testThis () {
        var input = """
            this
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        try {
            parser.parseExpression();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String actual = parser.VMOutput();
        String expected = """
                push pointer 0
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testNot () {
        var input = """
            ~ false
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0   
                not    
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testReturn () {
        var input = """
            return;
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseStatement();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0
                return       
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testReturnExpr () {
        var input = """
            return 10;
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseStatement();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10
                return       
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testMinus () {
        var input = """
            - 10
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10   
                neg    
                    """;
            assertEquals(expected, actual);
    }

    @Test
    public void testWhile () {
        var input = """
            while (false) {
                return 10;
            } 
            """;
        
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseStatement();
        String actual = parser.VMOutput();
        String expected = """
label WHILE_EXP0
push constant 0
not
if-goto WHILE_END0
push constant 10
return
goto WHILE_EXP0
label WHILE_END0
                    """;
            assertEquals(expected, actual);
    }

    
    @Test
    public void testSimpleFunctions () {
        var input = """
            class Main {
 
                function int soma (int x, int y) {
                        return  30;
                 }
                
                 function void main () {
                        var int d;
                        return;
                  }
                
                }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.soma 0
            push constant 30
            return
            function Main.main 1
            push constant 0
            return    
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void testSimpleFunctionWithVar () {
        var input = """
            class Main {

                 function int funcao () {
                        var int d;
                        return d;
                  }
                
                }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.funcao 1
            push local 0
            return
            """;
        assertEquals(expected, actual);
    }

    @Test
    public void testLet () {
        var input = """
            class Main {
            
              function void main () {
                  var int x;
                  let x = 42;
                  return;
              }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 1
            push constant 42
            pop local 0
            push constant 0
            return
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void arrayTest () {
        var input = """
            class Main {
                function void main () {
                    var Array v;
                    let v[2] = v[3] + 42;
                    return;
                }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 1
            push constant 2
            push local 0
            add
            push constant 3
            push local 0
            add
            pop pointer 1
            push that 0
            push constant 42
            add
            pop temp 0
            pop pointer 1
            push temp 0
            pop that 0
            push constant 0
            return        
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void callFunctionTest() {

        var input = """
            class Main {
                function int soma (int x, int y) {
                       return  x + y;
                }
               
                function void main () {
                       var int d;
                       let d = Main.soma(4,5);
                       return;
                 }
               
               }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();


        String actual = parser.VMOutput();
        String expected = """
            function Main.soma 0
            push argument 0
            push argument 1
            add
            return
            function Main.main 1
            push constant 4
            push constant 5
            call Main.soma 2
            pop local 0
            push constant 0
            return
                """;
        assertEquals(expected, actual);
 
 
    }

    @Test
    public void methodTest () {
        var input = """
            class Main {
                function void main () {
                    var Point p;
                    var int x;
                    let p = Point.new (10, 20);
                    let x = p.getX();
                    return;
                }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 2
            push constant 10
            push constant 20
            call Point.new 2
            pop local 0
            push local 0
            call Point.getX 1
            pop local 1
            push constant 0
            return
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void doStatement () {
        var input = """
            class Main {
                function void main () {
                    var int x;
                    let x = 10;
                    do Output.printInt(x);
                    return;
                }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 1
            push constant 10
            pop local 0
            push local 0
            call Output.printInt 1
            pop temp 0
            push constant 0
            return
                """;
        assertEquals(expected, actual);
    }
}
