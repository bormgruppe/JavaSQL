grammar Sql;

Whitespace
    : [ \t]+
    -> skip
    ;

Newline
    : ( '\r' '\n'? | '\n' )
    -> skip
    ;

LineComment
    : '--' ~[\r\n]*
    -> skip
    ;

Select : [sS][eE][lL][eE][cC][tT] ;
From : [fF][rR][oO][mM] ;
Where : [wW][hH][eE][rR][eE] ;
Join : [jJ][oO][iI][nN] ;
Order : [oO][rR][dD][eE][rR] ;
Top : [tT][oO][pP] ;
As : [aA][sS] ;
On : [oO][nN] ;
By : [bB][yY] ;
Asc : [aA][sS][cC] ;
Desc : [dD][eE][sS][cC] ;
And : [aA][nN][dD] ;
Or : [oO][rR] ;
Null : [nN][uU][lL][lL] ;
Left : [lL][eE][fF][tT] ;
Right : [rR][iI][gG][hH][tT] ;
Inner : [iI][nN][nN][eE][rR] ;
Outer: [oO][uU][tT][eE][rR] ;
Full : [fF][uU][lL][lL] ;
Cross : [cC][rR][oO][sS][sS] ;
Not : [nN][oO][tT] ;
Like : [lL][iI][kK][eE] ;
Is : [iI][sS] ;
With : [wW][iI][tT][hH] ;
Union : [uU][nN][iI][oO][nN] ;
All : [aA][lL][lL] ;
In : [iI][nN] ;

Int : [iI][nN][tT] ;
Float : [fF][lL][oO][aA][tT] ;
Datetime : [dD][aA][tT][eE][tT][iI][mM][eE] ;
Varchar : [nN]?[vV][aA][rR][cC][hH][aA][rR] ;
Max : [mM][aA][xX] ;

Cast : [cC][aA][sS][tT] ;

Year : [yY][eE][aA][rR] ;
Quarter : [qQ][uU][aA][rR][tT][eE][rR] ;
Month : [mM][oO][nN][tT][hH] ;
DayOfYear : [dD][aA][yY][oO][fF][yY][eE][aA][rR] ;
Day : [dD][aA][yY] ;
Week : [wW][eE][eE][kK] ;
WeekDay : [wW][eE][eE][kK][dD][aA][yY] ;
Hour : [hH][oO][uU][rR] ;
Minute : [mM][iI][nN][uU][tT][eE] ;
Second : [sS][eE][cC][oO][nN][dD] ;
MilliSecond : [mM][iI][lL][lL][iI][sS][eE][cC][oO][nN][dD] ;
MicroSecond : [mM][iI][cC][rR][oO][sS][eE][cC][oO][nN][dD] ;
NanoSecond : [nN][aA][nN][oO][sS][eE][cC][oO][nN][dD] ;

Identifier
    : IdentifierNondigit ( IdentifierNondigit | Digit )*
    ;

fragment
IdentifierNondigit
    : Nondigit
    ;

fragment
Nondigit
    : [a-zA-Z_]
    ;

fragment
Digit
    : [0-9]
    ;

IntegerConstant
    : DecimalConstant
    ;

fragment
DecimalConstant
    : NonzeroDigit Digit*
    | '0'
    ;

fragment
NonzeroDigit
    : [1-9]
    ;

FloatingConstant
    : FractionalConstant
    ;

fragment
FractionalConstant
    : DigitSequence? '.' DigitSequence
    | DigitSequence '.'
    ;

fragment
DigitSequence
    : Digit+
    ;

fragment
EscapeSequence
    : '\'\''
    ;

StringLiteral
    : '\'' SCharSequence? '\''
    ;

fragment
SCharSequence
    : SChar+
    ;

fragment
SChar
    : ~['\r\n]
    | EscapeSequence
    ;

varcharType
    : varcharTypeIntMax
    | varcharTypeMax
    | varcharTypeNoMax
    ;
    
varcharTypeIntMax
    : Varchar '(' IntegerConstant ')'
    ;
    
varcharTypeMax
    : Varchar '(' Max ')'
    ;
    
varcharTypeNoMax
    : Varchar
    ;
    
intType
    : Int
    ;
    
floatType
    : Float
    ;
    
datetimeType
    : Datetime
    ;
    
dataType
    : varcharType
    | intType
    | floatType
    | datetimeType
    ;

sqlIdentifier
    : '[' Identifier ']'
    | Identifier
    ;

fullStatement
    : dataStatement orderStatement
    | dataStatement 
    ;

dataStatement
    : cteStatementHead unionAllStatement
    | unionAllStatement
    ;

unionAllStatement
    : unionAllStatement Union All statement
    | statement
    ;

cteStatementHead
    : With cteStatementBlock
    ;

cteStatementBlock
    : cteStatementBlock ',' cteStatement
    | cteStatement
    ;

cteStatement
    : sqlIdentifier As '(' statement ')'
    ;

statement
    : selectStatement (fromStatement joinStatement*)? whereStatement?
    ;

selectStatement
    : selectAllStatement
    | selectTopStatement
    ;

selectAllStatement
    : Select valueList
    ;

selectTopStatement
    : Select Top IntegerConstant valueList
    ;

fromStatement
    : From sourceList
    ;

joinDirectionLeft
    : Left
    ;

joinDirectionRight
    : Right
    ;

joinDirectionFull
    : Full
    ;

joinDirection
    : joinDirectionLeft
    | joinDirectionRight
    | joinDirectionFull
    ;

joinSpecialCross
    : Cross
    ;

joinSpecialInner
    : Inner
    ;

joinTypeSpecial
    : joinSpecialCross
    | joinSpecialInner
    ;

joinTypeOuter
    : joinDirection Outer?
    ;

joinType
    : joinTypeSpecial
    | joinTypeOuter
    ;

joinStatement
    : joinType? Join source On condition
    ;

whereStatement
    : Where condition
    ;

orderStatement
    : Order By orderList
    ;

primaryValue
    : field
    | tableField
    | stringValue
    | numericValue
    | functionValue
    | queryValue
    | castValue
    ;

stringValue
    : StringLiteral
    ;

numericValue
    : integerValue
    | floatingValue
    ;
    
integerValue
    : IntegerConstant
    ;
    
floatingValue
    : FloatingConstant
    ;

functionValue
    : Identifier '(' argumentList? ')'
    ;

queryValue
    : '(' statement ')'
    ;

castValue
    : Cast '(' expression As dataType ')'
    ;
    
argument
    : dataType
    | constArg
    | expression
    ;

argumentList
    : argument ',' argumentList
    | argument
    ;

constArg
    : Year
    | Quarter
    | Month
    | DayOfYear
    | Day
    | Week
    | WeekDay
    | Hour
    | Minute
    | Second
    | MilliSecond
    | MicroSecond
    | NanoSecond
    ;

aliasedValue
    : expression As sqlIdentifier
    ;

value
    : expression
    | aliasedValue
    | allFields
    | allTableFields
    ;

allFields
    : '*'
    ;

allTableFields
    : table '.*'
    ;

field
    : sqlIdentifier
    ;
    
table
    : sqlIdentifier
    | sqlIdentifier '.' sqlIdentifier
    ;

tableField
    : table '.' sqlIdentifier
    ;

valueList
    : value ',' valueList
    | value
    ;

source
    : tableSource
    | aliasedSource
    ;

tableSource
    : table
    ;

aliasedTable
    : tableSource As sqlIdentifier
    ;

aliasedStatement
    : '(' statement ')' As sqlIdentifier
    ;

aliasedSource
    : aliasedTable
    | aliasedStatement
    ;

sourceList
    : source ',' sourceList
    | source
    ;

defaultOrderValue
    : expression
    ;

ascOrderValue
    : expression Asc
    ;    

descOrderValue
    : expression Desc
    ;

orderValue
    : defaultOrderValue
    | ascOrderValue
    | descOrderValue
    ;

orderList
    : orderValue ',' orderList
    | orderValue
    ;

condition
    : logicalOrCondition
    ;

logicalOrCondition
    : logicalAndCondition
    | logicalOrCondition Or logicalAndCondition
    ;

logicalAndCondition
    : notCondition
    | logicalAndCondition And notCondition
    ;

notCondition
    : primaryCondition
    | Not primaryCondition
    ;

primaryCondition
    : equalCondition
    | unequalCondition
    | lessCondition
    | lessEqualCondition
    | greaterCondition
    | greaterEqualCondition
    | likeCondition
    | isNullCondition
    | isNotNullCondition
    | inCondition
    | notInCondition
    | parCondition
    ;

equalCondition
    : expression '=' expression
    ;

unequalCondition
    : expression '<>' expression
    ;

lessCondition
    : expression '<' expression
    ;

lessEqualCondition
    : expression '<=' expression
    ;

greaterCondition
    : expression '>' expression
    ;

greaterEqualCondition
    : expression '>=' expression
    ;

likeCondition
    : expression Like expression
    ;

isNullCondition
    : expression Is Null
    ;

isNotNullCondition
    : expression Is Not Null
    ;

inCondition
    : expression In '(' statement ')'
    ;

notInCondition
    : expression Not In '(' statement ')'
    ;

parCondition
    : '(' condition ')'
    ;

expression
    : additiveExpression
    ;

additiveExpression
    : multiplicativeExpression
    | additiveExpression additiveOperator multiplicativeExpression
    ;

additiveOperator
    : '+' | '-'
    ;

multiplicativeExpression
    : negateExpression
    | multiplicativeExpression multiplicativeOperator negateExpression
    ;

multiplicativeOperator
    : '*' | '/'
    ;

negateExpression
    : primaryExpression
    | negateOperator primaryExpression
    ;

negateOperator
    : '-'
    ;

primaryExpression
    : primaryValue
    | parExpression
    ;

parExpression
    : '(' expression ')'
    ;
