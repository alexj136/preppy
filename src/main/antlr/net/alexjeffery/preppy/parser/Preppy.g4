grammar Preppy;

@parser::header {
    import net.alexjeffery.preppy.syntax.Declaration;
    import net.alexjeffery.preppy.syntax.Statement;
    import net.alexjeffery.preppy.syntax.Expression;
}

declarations returns [List<Declaration> out]
    : declaration ds=declarations { $out = $ds.out; $out.add(0, $declaration.out); }
    | declaration EOF { $out = new ArrayList<>(); $out.add($declaration.out); }
    ;

declaration returns [Declaration out]
    : KwInt Ident '(' params ')' statement { $out = new Declaration($Ident.text, $params.out, $statement.out); }
    ;

params returns [List<String> out]
    : /* empty */ { $out = new ArrayList<>(); }
    | params_ne { $out = $params_ne.out; }
    ;

params_ne returns [List<String> out]
    : KwInt Ident ',' params { $out = $params.out; $out.add(0, $Ident.text); }
    | KwInt Ident { $out = new ArrayList<>(); $out.add($Ident.text); }
    ;

statement returns [Statement out]
    : '{' statements ';' '}' { $out = new Statement.Block($statements.out); }
    | Ident '=' expression { $out = new Statement.Assignment($Ident.text, $expression.out); }
    | KwWhile '(' expression ')' statement { $out = new Statement.While($expression.out, $statement.out); }
    | KwReturn expression { $out = new Statement.Return($expression.out); }
    | KwIf '(' expression ')' t=statement KwElse f=statement { $out = new Statement.Cond($expression.out, $t.out, $f.out); }
    ;

statements returns [List<Statement> out]
    : statement ';' statements { $out = $statements.out; $out.add(0, $statement.out); }
    | statement { $out = new ArrayList<>(); $out.add($statement.out); }
    ;

expression returns [Expression out]
    : Digit { $out = new Expression.IntLiteral(Integer.parseInt($Digit.text)); }
    | Ident { $out = new Expression.Variable($Ident.text); }
    | l=expression binop r=expression { $out = new Expression.BinOp($l.out, $r.out, $binop.out); }
    | Ident '(' args ')' { $out = new Expression.Call($Ident.text, $args.out); }
    ;

args returns [List<Expression> out]
    : /* empty */ { $out = new ArrayList<>(); }
    | args_ne { $out = $args_ne.out; }
    ;

args_ne returns [List<Expression> out]
    : expression ',' args { $out = $args.out; $out.add(0, $expression.out); }
    | expression { $out = new ArrayList<>(); $out.add($expression.out); }
    ;

binop returns [Expression.BinOp.Type out]
    : '+' { $out = Expression.BinOp.Type.ADD; }
    | '-' { $out = Expression.BinOp.Type.SUB; }
    | '*' { $out = Expression.BinOp.Type.MUL; }
    | '/' { $out = Expression.BinOp.Type.DIV; }
    | '%' { $out = Expression.BinOp.Type.MOD; }
    | '==' { $out = Expression.BinOp.Type.EQ; }
    ;

KwIf : 'if';
KwElse : 'else';
KwInt : 'int' ;
KwWhile : 'while' ;
KwReturn : 'return' ;

Lower : ('a'..'z') ;
Upper : ('A'..'Z') ;
Digit : ('0'..'9') ;
Alpha : Lower | Upper ;
AlNum : Alpha | Digit ;
Ident : (AlNum | '_')+ ;
Whitespace : [ \t\r\n]+ -> skip ;
