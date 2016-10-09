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
-- DELTA --
(NOT TRUE)
NOT -> (λ f.(λ x.(λ y.((f y) x))))
--------
-- BETA --
((λ f.(λ x.(λ y.((f y) x)))) TRUE)
(λ f.(λ x.(λ y.((f y) x)))) -> TRUE
--------
-- DELTA --
(λ x.(λ y.((TRUE y) x)))
TRUE -> (λ x.(λ y.x))
--------
-- ALPHA --
(λ x.(λ y.(((λ x.(λ y.x)) y) x)))
y -> y'
--------
-- BETA --
(λ x.(λ y'.(((λ x.(λ y.x)) y') x)))
(λ x.(λ y.x)) -> y'
--------
-- BETA --
(λ x.(λ y'.((λ y.y') x)))
(λ y.y') -> x
--------
(λ x.(λ y'.y'))
```

Design
------

TODO

Thanks
------

  * Wikipedia
