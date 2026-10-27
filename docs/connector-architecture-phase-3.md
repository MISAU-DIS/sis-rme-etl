# Connector architecture — phase 3 source execution port

## Outcome

Phase 3 routes the primary ETL extraction through a connector-neutral reader
port. The existing JDBC search implementation remains unchanged and is now
invoked by `JdbcConnectorRecordReader`.

```text
EtlItemConfiguration.createSourceReader
  -> ConnectorRecordReaderFactory
  -> ConnectorRecordReader.read
  -> JdbcConnectorRecordReader
  -> AbstractEtlSearchParams (existing SQL, filters and pagination)
```

The resolved `ConnectorResource` created during `EtlItemConfiguration.init()`
is authoritative for reader selection. Runtime code does not resolve the
resource again.

Reader creation also belongs to `EtlItemConfiguration`. It selects the resolved
resource and asks `EtlConfiguration` to open the connector's native source
connection. `Engine` no longer owns `openSrcConn()` or a reader factory method.

The reader is also propagated through `TaskProcessor` and the single-thread,
range-partitioning and result-partitioning strategies. These components no
longer receive a source `Connection` as their extraction dependency.

## Contracts

- `ConnectorReadRequest` carries the interval, parent context, auxiliary source
  objects and parallel-read choice without exposing JDBC.
- `ConnectorRecordReader` is the source execution port.
- `ConnectorRecordReaderFactory` validates source direction and `READ`
  capability before choosing an implementation.
- `JdbcConnectorRecordReader` is the compatibility adapter. JDBC connections
  and `AbstractEtlSearchParams` are held by that adapter.
- `unwrap(...)` is the temporary compatibility boundary used when transform,
  stage-area or persistence code still requires the native JDBC connection.

## Compatibility boundary

This phase deliberately changes neither SQL construction nor connection and
transaction ownership. Single-thread and multi-thread reads delegate to the
same `AbstractEtlSearchParams` methods previously called directly by
`Engine.extract()`.

Only JDBC has an execution adapter in this phase. Other connector types are
still rejected before execution until their readers are implemented.

## Acceptance criteria

1. `EtlItemConfiguration` creates the reader from its already resolved source
   resource; `Engine` only consumes it.
2. JDBC single-thread and multi-thread extraction preserve their former paths.
3. `TaskProcessor.extractTransformAndLoad()` performs extraction exclusively
   through the supplied reader.
4. Processing strategies propagate the reader instead of a source connection.
5. A destination resource cannot be used as a source reader.
6. Reader creation requires the `READ` capability.
7. Existing JDBC and connector regression tests remain green.
