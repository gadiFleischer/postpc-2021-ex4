"I pledge the highest level of ethical principles in support of academic excellence.  I ensure that all of my work reflects my own abilities and not those of someone else."

Question:
Testing the CalculateRootsService for good input is pretty easy - we pass in a number and we expect a broadcast intent with the roots.
Testing for big prime numbers can be frustrating - currently the service is hard-coded to run for 20 seconds before giving up, which would make the tests run for too long.

What would you change in the code in order to let the service run for maximum 200ms in tests environments, but continue to run for 20sec max in the real app (production environment)?

Answer:
I would add to the "CalculateRootsService" a parameter for time give up which will have a
default value of 20 seconds for robostness.
the service will get from an Intent the time value and will give up after that time,
thus from the MainActivity I can send via an Intent a value of 20 seconds and
in the tests enviroment send a value for 200ms.