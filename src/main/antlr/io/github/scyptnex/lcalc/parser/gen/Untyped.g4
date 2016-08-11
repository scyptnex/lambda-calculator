grammar Untyped;

specification
    :   command specification       # instruction
    |   EOF                         # end
    ;

command
    :   '#' ID expression           # definition
//  |   '#' ID '!' expression       # fixed
    |   '?' expression              # output
//  |   '@' ID                      # option
    ;

expression
    :   '\\' var+ '.' expression    # abstraction
    |   unit+ expression?           # chain
    ;

unit
    :   var                         # variable
    | '(' expression ')'            # subExpression
    ;

var
    : ID
    ;

ID  :   [a-zA-Z0-9_-]+ ; // identifiers
WS  :   [ \t\n\r]+ -> skip ; // toss out whitespace
