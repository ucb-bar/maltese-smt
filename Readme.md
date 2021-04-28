# maltese-smt


To use, add the following to your `build.sbt`:
```.sbt
resolvers += Resolver.sonatypeRepo("snapshots")
libraryDependencies += "edu.berkeley.cs" %% "maltese-smt" % "0.5-SNAPSHOT"
```

## Dependencies

This library is compatible with the Chisel/firrtl `X.5-SNAPSHOT` series.

We currently depend on [firrtl](https://github.com/chipsalliance/firrtl)
and [treadle](https://github.com/chipsalliance/treadle).
It should be possible to eventually remove these dependencies, however,
we currently do not have the engineering resources to do so.

- we depend on `firrtl` in order to
  [convert the output of the firrtl SMT backend to the maltese datastructures](https://github.com/ucb-bar/maltese-smt/blob/main/src/firrtl/backends/experimental/smt/ExpressionConverter.scala)
- we depend on `treadle` for its VCD library
