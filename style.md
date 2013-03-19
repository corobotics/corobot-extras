# General

- Always use four spaces to indent.
- Always indent a new block of code (e.g., inside a class, function, or `if`/`for`/`while`/etc. block).
- Class names are always `UpperCase`.
- Always separate operators and operands with spaces: `x + y`, NOT `x+y`.
- Never put a space between a function and its arguments: `f(x)`, NOT `f (x)` or `f( x )`.
- Always separate function arguments with spaces: `f(a, b, c)`, NOT `f(a,b,c)`.
- Always put a space between `if`/`for`/`while`/etc. statements and their expressions: `if (n == 1) {`, NOT `if(n == 1){`. They aren't functions.
- Always use K&R ("Egyptian") style brackets:

        int f(int x) {
            return x;
        }

- Do not do one-line `if`/`for`/`while`/etc. statements. Break them into two lines and use braces.
- Try to break code into logical paragraphs and comment each one. If you look at code a week later and can't tell what it's doing, you didn't write good enough comments for anyone else to understand it either.
- Three import paragraphs: language defaults, third-party packages, and own packages. For example:

        import math
        
        import rospy
        
        from corobot_common.msg import Pose

# Java, C++, Dart

- Always name variables and methods using `camelCase`.
- Create full Javadoc style comments for every method.

# Python

- Adhere to [PEP8](http://www.python.org/dev/peps/pep-0008/) in all things, especially:
- Always name variables and functions using `lower_case_with_underscores`.
- Use the [proper documentation style](http://www.python.org/dev/peps/pep-0257/). For example:

        def f(x):
            """f does something to x and returns a list.
            
            x -- the value
            
            """
            return [i for i in range(x)]

# C++

- Pointer stars always go with types: `int* x`.
