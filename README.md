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
? YCOMBI λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n) THREE

- DELTA -
YCOMBI λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n) THREE
YCOMBI <- λ g.(λ x.g (x x)) λ x.g (x x)
---------
- BETA  -
(λ g.(λ x.g (x x)) λ x.g (x x)) λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n) THREE
g <- λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)
---------
- ALPHA -
(λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x) THREE <--> n
---------
- ALPHA -
(λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f n'.IF (ISZERO n') ONE (MULT (f (PRED n')) n')) (x x) THREE <--> f
---------
- BETA  -
(λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f' n'.IF (ISZERO n') ONE (MULT (f' (PRED n')) n')) (x x) THREE
x <- λ x.(λ f' n'.IF (ISZERO n') ONE (MULT (f' (PRED n')) n')) (x x)
---------
...
- BETA  -
λ f.(λ y.ONE) (MULT ((λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x) ZERO) ZERO) (ONE (TWO (THREE f)))
y <- MULT ((λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x)) λ x.(λ f n.IF (ISZERO n) ONE (MULT (f (PRED n)) n)) (x x) ZERO) ZERO
---------
- DELTA -
λ f.ONE (ONE (TWO (THREE f)))
ONE <- λ s z.s z
---------
- BETA  -
λ f.(λ s z.s z) (ONE (TWO (THREE f)))
s <- ONE (TWO (THREE f))
---------
- DELTA -
λ f z.ONE (TWO (THREE f)) z
ONE <- λ s z.s z
---------
- BETA  -
λ f z.(λ s z.s z) (TWO (THREE f)) z
s <- TWO (THREE f)
---------
- BETA  -
λ f z.(λ z.TWO (THREE f) z) z
z <- z
---------
- DELTA -
λ f z.TWO (THREE f) z
TWO <- λ s z.s (s z)
---------
- BETA  -
λ f z.(λ s z.s (s z)) (THREE f) z
s <- THREE f
---------
- BETA  -
λ f z.(λ z.THREE f (THREE f z)) z
z <- z
---------
- DELTA -
λ f z.THREE f (THREE f z)
THREE <- λ s z.s (s (s z))
---------
- BETA  -
λ f z.(λ s z.s (s (s z))) f (THREE f z)
s <- f
---------
- BETA  -
λ f z.(λ z.f (f (f z))) (THREE f z)
z <- THREE f z
---------
- DELTA -
λ f z.f (f (f (THREE f z)))
THREE <- λ s z.s (s (s z))
---------
- BETA  -
λ f z.f (f (f ((λ s z.s (s (s z))) f z)))
s <- f
---------
- BETA  -
λ f z.f (f (f ((λ z.f (f (f z))) z)))
z <- z
---------
λ f z.f (f (f (f (f (f z)))))
```

