# JSON Tester

Test utilities for configuring tests in JSON in the format:

    {
        "given": [2, 3], // initial state
        "when": "+"      // command
        "then": 5        // result
    }

...or 

    {
        "given": null,
        "when": "2 + 3",
        "then": 5
    }