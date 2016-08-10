#! /usr/bin/env python3

"""
+-------------------------------------------------------------------------+
|                            coverage_check.py                            |
|                                                                         |
| Author: Nic H.                                                          |
| Date: 2016-Aug-10                                                       |
|                                                                         |
| Checks the coverage of a Jacoco report file (in .csv format).  On       |
| success, it completes normally, on failure it prints an error and       |
| throws (or returns nonzero from command line).                          |
|                                                                         |
| Usage:                                                                  |
|   python3 coverage_check.py [OPTIONS] --[METRIC]=[LOW]-[AVG] <file>     |
|                                                                         |
| Metrics:                                                                |
|   instruction, branch, complexity, line, method, class                  |
|                                                                         |
| Options:                                                                |
|   -h            Print this help message                                 |
|   -x <regex>    Exclude data from classes with fully-qualified name     |
|                 matching <regex> (use multiple times)                   |
+-------------------------------------------------------------------------+
"""

__doc__ = __doc__.strip()

import csv
import getopt
import re
import sys

# class is last here
_METRICS=['instruction', 'branch', 'complexity', 'line', 'method', 'class']

def eprint(*args, **kwargs):
    """ thank you MarkH http://stackoverflow.com/a/14981125 """
    print(*args, file=sys.stderr, **kwargs)

def usage_and_exit(error=None):
    if error:
        eprint(error)
        eprint("for help use -h")
        sys.exit(1)
    else:
        print(__doc__)
        sys.exit(0)

def ensure(failures, name, metric, missed, covered, target):
    amt = 0.0 if missed+covered == 0 else covered/((covered+missed))
    if amt < target/100.0:
        failures.append("{: >40} {: >12} requires {: >3}%  {: >5}/{: <5} ({:.1%})".format(name[-40:], metric, target, covered, covered+missed, amt))

def coverage_check(fname, exclusions, minimums):
    excluded_matchers = [re.compile(x) for x in exclusions]
    totals = {n : (0,0) for n in _METRICS}
    failures=[]
    with open(fname, 'r') as jacoco_file:
        reader = csv.DictReader(jacoco_file)
        for row in reader:
            nm = row['PACKAGE'] + "." + row['CLASS']
            # try to exclude this row
            exclude = False
            for x in excluded_matchers:
                if x.search(nm):
                    exclude = True
            if exclude: continue
            class_covered = False
            for m in _METRICS:
                if m != "class":
                    missed = int(row[m.upper() + "_MISSED"])
                    covered = int(row[m.upper() + "_COVERED"])
                    ensure(failures, nm, m, missed, covered, minimums[m][0])
                    (tmis,tcov) = totals[m]
                    totals[m] = (tmis + missed, tcov + covered)
                    class_covered = class_covered or (covered > 0)
            cmissed = 0 if class_covered else 1
            ccovered = 1 - cmissed
            ensure(failures, nm, "class", cmissed, ccovered, minimums[m][0])
            (tmis,tcov) = totals["class"]
            totals["class"] = (tmis + cmissed, tcov + ccovered)
    for m in _METRICS:
        (missed, covered) = totals[m]
        ensure(failures, "TOTAL", m, missed, covered, minimums[m][1])
    for f in failures:
        eprint(f)
    return 2 if failures else 0

def read_args(cline):
    excludes = []
    minimums = {n : [0,0] for n in _METRICS}
    try:
        opts, args = getopt.getopt(cline, "hx:", [n + "=" for n in _METRICS])
    except getopt.error as msg:
        usage_and_exit(msg)
    for o, a in opts:
        if o == "-h":
            usage_and_exit()
        elif o == "-x":
            excludes.append(a)
        elif o[2:] in _METRICS:
            minimums[o[2:]] = [min(max(int(s), 0), 100) for s in a.split("-")]
    if not args:
        usage_and_exit("Please specify the path to the jacoco CSV report file")
    return (args[0], excludes, minimums)

if __name__ == "__main__":
    args = read_args(sys.argv[1:])
    exit_code = coverage_check(*args)
    sys.exit(exit_code)
