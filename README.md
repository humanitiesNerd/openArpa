# What this is all about

The background is [here](https://medium.com/@catonano/a-daunting-job-ab19d8cc972a#.ibeq308cb)

The main difference is I dumped Grafter since then.

This is a very elementary exercise in clojure code.

Nothing interesting here. Just a plain old collection of maps being passed around.

It doesn't even make use of pattern matching, and it could really use it.

No parallelism, no fancy stuff.

I even misunderstood how the transducers were supposed to be used so I had to ask for clarifications on the beginners channel of the clojurians Slack

I have been just poking around to see if and how these data could be made ready to be dumped into a triple store.

# Known woes

Apart from not using the pattern matching, there's no test machinery here. 

Some time ago I read about a test framework that searched for some statistical properties of the produced dataset but I can't remember which one it was. I'd be grateful if someone could point me to it again.

Also, it there is room to improve the peformance around the datetimes parsing and moving around. 

There's no documentation at all. I'll try to write it in the future.

# State of the art

I couldn't understand the places where some of the measurements were taken. So not oll of the input files end up in the output. Measurements of the air pollution without geographical coordinates don't make much sense.

Even so, this thing produced a file of approximately 15 million records.
