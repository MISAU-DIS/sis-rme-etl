# Connector architecture — phase 5 processor migration

## EtlProcessor increment

The public execution contract of `EtlProcessor` is connector-based:

- `transformAndLoad`, `transform` and `load` receive a
  `ConnectorRecordReader` and `ConnectorRecordWriters`;
- `performTransformationAndLoading` exposes the same connector-oriented
  context;
- no public or protected `EtlProcessor` method receives `srcConn` or `dstConn`.

Transformation remains JDBC-based in this increment. Native connections are
unwrapped only inside the implementation and passed to the existing private
transformation methods, transformers and legacy loading routines.

Subclasses that still publish the old JDBC method inherit the temporary
`JdbcEtlProcessor` compatibility boundary. They will be migrated individually
before that bridge is removed.

`performLoading` now has only one implementation and receives the reader and
writer aggregate. The same connector context is propagated through child-item
loading and recursive transformation/loading. JDBC compatibility callers are
adapted at the boundary and no longer select a separate connection-based load
path.
