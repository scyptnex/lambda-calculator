Lambda Calculator
=================

[![Build Status](https://travis-ci.org/scyptnex/lambda-calculator.svg?branch=master)](https://travis-ci.org/scyptnex/lambda-calculator)

A high performance lambda-calculus-simulator, written in Java.
This project is intended for demonstrating the relationship between lambda calculus and computation.

The intended audience is:
  * Students
  * Teachers/Lecturers
  * The morbidly curious

Usage
-----

The lambda calculator reads from stdin and writes to stdout.

  * Lambda terms are written using `\` as the lambda character. E.g. `\x.x` is the identity function.
  * The syntax `# <NAME> <EXPRESSION>` defines the symbol `<NAME>` to have `<EXPRESSION>` as its value.
  * The syntax `? <EXPRESSION>` computes the reduced form (if it can) of `<EXPRESSION>`.

Given the input:

```
# TRUE \x y.x
# NOT \f x y.f y x
? NOT TRUE
```

The calculator shows all the intermediate working steps (we call the substitution of a dictioanry value to an identifier the "delta" transformation) and produces an answer:

```
- DELTA -
NOT TRUE
NOT <- λ f x y.f y x
---------
- BETA  -
(λ f x y.f y x) TRUE
f <- TRUE
---------
- DELTA -
λ x y.TRUE y x
TRUE <- λ x y.x
---------
- ALPHA -
λ x y.(λ x y.x) y x <--> y
---------
- BETA  -
λ x y'.(λ x y.x) y' x
x <- y'
---------
- BETA  -
λ x y'.(λ y.y') x
y <- x
---------
λ x y'.y'
```

A more complicated example:

```
? YCOMBI (\f n . IF (ISZERO n) ONE (MULT (f (PRED n)) n))

- DELTA -
YCOMBI λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n) THREE
YCOMBI <- λ g.(λ x.g (x x)) λ x.g (x x)
---------
- BETA  -
(λ g.(λ x.g (x x)) λ x.g (x x)) λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n) THREE
g <- λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)
---------
...
- BETA  -
(λ y.y) (MULT ((λ x.(λ f' n'.IF (ISZERO n') ONE (MULT (f' (PRED n')) n')) (x x)) λ x.(λ f' n'.IF (ISZERO n') ONE (MULT (f' (PRED n')) n')) (x x) (PRED THREE)) THREE)
y <- MULT ((λ x.(λ f' n'.IF (ISZERO n') ONE (MULT (f' (PRED n')) n')) (x x)) λ x.(λ f' n'.IF (ISZERO n') ONE (MULT (f' (PRED n')) n')) (x x) (PRED THREE)) THREE
---------
...
- DELTA -
λ f.ISZERO (PRED (PRED (PRED THREE))) ONE (MULT ((λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x) (PRED (PRED (PRED (PRED THREE))))) (PRED (PRED (PRED THREE)))) (PRED (PRED THREE) (PRED THREE (THREE f)))
ISZERO <- λ n.n λ x.FALSE TRUE
---------
...
- BETA  -
λ f.(λ y.ONE) (MULT ((λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x) (PRED (PRED (PRED (PRED THREE))))) (PRED (PRED (PRED THREE)))) (PRED (PRED THREE) (PRED THREE (THREE f)))
y <- MULT ((λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x) (PRED (PRED (PRED (PRED THREE))))) (PRED (PRED (PRED THREE)))
---------
- DELTA -
λ f.ONE (PRED (PRED THREE) (PRED THREE (THREE f)))
ONE <- λ s z.s z
---------
...
---------
- BETA  -
λ f z.f (f (f (f (f (f ((λ u'.z) (PRED THREE (THREE f))))))))
u' <- PRED THREE (THREE f)
---------
λ f z.f (f (f (f (f (f z)))))
```

Design
------

TODO

Thanks
------

  * Wikipedia
